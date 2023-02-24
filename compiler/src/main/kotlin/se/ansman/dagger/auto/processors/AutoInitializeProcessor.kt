package se.ansman.dagger.auto.processors

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dagger.Binds
import dagger.Module
import dagger.Provides
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Errors
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.models.AutoInitializeModule
import se.ansman.dagger.auto.models.AutoInitializeObject
import se.ansman.dagger.auto.processing.AnnotationModel
import se.ansman.dagger.auto.processing.AutoDaggerProcessing
import se.ansman.dagger.auto.processing.ClassDeclaration
import se.ansman.dagger.auto.processing.Function
import se.ansman.dagger.auto.processing.Node
import se.ansman.dagger.auto.processing.Property
import se.ansman.dagger.auto.processing.Type
import se.ansman.dagger.auto.processing.filterQualifiers
import se.ansman.dagger.auto.processing.getAnnotation
import se.ansman.dagger.auto.processing.getQualifiers
import se.ansman.dagger.auto.processing.isAnnotatedWith
import se.ansman.dagger.auto.processing.isFullyPublic
import se.ansman.dagger.auto.processing.logError
import se.ansman.dagger.auto.renderers.Renderer
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.reflect.KClass

class AutoInitializeProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    private val processing: AutoDaggerProcessing<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor {
    override val annotations: Set<KClass<out Annotation>> = setOf(AutoInitialize::class)
    override fun process() {
        val initializableObjectsByModule =
            HashMultimap.create<ClassName, AutoInitializeObject<N, TypeName, AnnotationSpec>>()
        for (node in processing.nodesAnnotatedWith(AutoInitialize::class)) {
            when (node) {
                is ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> -> {
                    val obj = node.process() ?: continue
                    val file = renderer.render(obj)
                    processing.write(file)
                }

                is Function<N, TypeName, ClassName, AnnotationSpec> -> node.processMethod(
                    methodName = node.name,
                    receiver = node.receiver,
                    returnType = node.returnType,
                    arguments = node.arguments,
                    output = initializableObjectsByModule
                )

                is Property<N, TypeName, ClassName, AnnotationSpec> -> node.processMethod(
                    methodName = "get${node.name.replaceFirstChar(Char::uppercaseChar)}",
                    receiver = node.receiver,
                    returnType = node.type,
                    arguments = emptySequence(),
                    output = initializableObjectsByModule
                )

                else -> throw IllegalArgumentException("Unknown node $this")
            }
        }

        for ((moduleName, providers) in initializableObjectsByModule.asMap()) {
            val module = with(processing.renderEngine) {
                AutoInitializeModule(
                    moduleName = moduleName.peerClass(
                        moduleName.simpleNames.joinToString(
                            separator = "",
                            prefix = "AutoInitialize"
                        )
                    ),
                    objects = providers.toList(),
                    topLevelClassName = moduleName.topLevelClassName,
                )
            }
            val file = renderer.render(module)
            processing.write(file)
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.process(): AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>? =
        with(processing.renderEngine) {
            if (!validateScopes()) {
                return null
            }

            val isInitializable = asType().isAssignableTo(Initializable::class)
            val targetType = toClassName()
            val obj = AutoInitializeObject(
                targetType = targetType,
                priority = getAnnotation(AutoInitialize::class)!!.getValue("priority"),
                isPublic = isFullyPublic,
                method = if (isInitializable) {
                    AutoInitializeObject.Method.Binding.fromType(targetType.simpleName)
                } else {
                    AutoInitializeObject.Method.Provider.fromType(targetType.simpleName)
                },
                originatingElement = node,
                qualifiers = getQualifiers()
            )
            return AutoInitializeModule(
                moduleName = obj.moduleName,
                objects = listOf(obj),
                topLevelClassName = targetType.topLevelClassName
            )
        }

    private fun Node<N, TypeName, ClassName, AnnotationSpec>.processMethod(
        methodName: String,
        receiver: Type<N, TypeName, ClassName, AnnotationSpec>?,
        returnType: Type<N, TypeName, ClassName, AnnotationSpec>,
        arguments: Sequence<Type<N, TypeName, ClassName, AnnotationSpec>>,
        output: Multimap<ClassName, AutoInitializeObject<N, TypeName, AnnotationSpec>>,
    ) {

        val module = enclosingType
            ?.let { if (it.isCompanionObject) it.enclosingType else it }
            ?: run {
                processing.logError(Errors.methodInNonModule, this@processMethod)
                return
            }

        if (!module.isAnnotatedWith(Module::class)) {
            processing.logError(Errors.methodInNonModule, node)
        }

        val isProvider = annotations.any { it.isOfType(Provides::class) }
        val isBinding = annotations.any { it.isOfType(Binds::class) }
        if (!isProvider && !isBinding) {
            processing.logError(Errors.invalidAnnotatedMethod, this)
            return
        }

        val annotations = annotations + if (isBinding) {
            receiver?.declaration?.annotations
                ?: arguments.firstOrNull()?.declaration?.annotations
                ?: emptyList()
        } else {
            returnType.declaration.annotations
        }

        if (!annotations.validateScopes(node)) {
            return
        }

        val isInitializable = returnType.isAssignableTo(Initializable::class)
        val targetType = returnType.toTypeName()
        val name = "${methodName}AsInitializable"
        val obj = AutoInitializeObject(
            targetType = targetType,
            priority = getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = isFullyPublic,
            method = if (isInitializable) {
                AutoInitializeObject.Method.Binding(name)
            } else {
                AutoInitializeObject.Method.Provider(name)
            },
            originatingElement = node,
            qualifiers = annotations.filterQualifiers().toSet()
        )
        output.put(module.toClassName(), obj)
    }

    private fun List<AnnotationModel<*, *>>.validateScopes(node: N): Boolean {
        val scopes = filter { it.isAnnotatedWith(Scope::class) }

        if (scopes.isEmpty()) {
            processing.logError(Errors.unscopedType, node)
            return false
        }
        if (scopes.none { it.isOfType(Singleton::class) }) {
            processing.logError(Errors.unscopedType, node)
            return false
        }
        return true
    }

    private fun Node<N, *, *, *>.validateScopes(): Boolean = annotations.validateScopes(node)

    private val AutoInitializeObject<*, TypeName, *>.moduleName: ClassName
        get() = with(processing.renderEngine) {
            className(
                targetType.rawType.packageName,
                targetType.rawType.simpleNames.joinToString(
                    prefix = "AutoInitialize",
                    postfix = "Module",
                    separator = ""
                )
            )
        }
}