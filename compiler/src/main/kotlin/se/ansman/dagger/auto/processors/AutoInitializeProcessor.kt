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
import se.ansman.dagger.auto.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.processing.AutoDaggerResolver
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
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val annotations: Set<KClass<out Annotation>> = setOf(AutoInitialize::class)

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        val initializableObjectsByModule =
            HashMultimap.create<ClassName, AutoInitializeObject<N, TypeName, AnnotationSpec>>()
        for (node in resolver.nodesAnnotatedWith(AutoInitialize::class)) {
            when (node) {
                is ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> -> {
                    val obj = node.process() ?: continue
                    val file = renderer.render(obj)
                    environment.write(file)
                }

                is Function<N, TypeName, ClassName, AnnotationSpec> -> node.processMethod(
                    receiver = node.receiver,
                    returnType = node.returnType,
                    arguments = node.arguments,
                    output = initializableObjectsByModule
                )

                is Property<N, TypeName, ClassName, AnnotationSpec> -> node.processMethod(
                    receiver = node.receiver,
                    returnType = node.type,
                    arguments = emptySequence(),
                    output = initializableObjectsByModule
                )

                else -> throw IllegalArgumentException("Unknown node $this")
            }
        }

        for ((moduleName, providers) in initializableObjectsByModule.asMap()) {
            val module = with(environment.renderEngine) {
                AutoInitializeModule(
                    moduleName = className(
                        packageName(moduleName),
                        simpleNames(moduleName).joinToString(
                            separator = "",
                            prefix = "AutoInitialize"
                        )
                    ),
                    objects = providers.toList(),
                    topLevelClassName = topLevelClassName(moduleName),
                )
            }
            val file = renderer.render(module)
            environment.write(file)
        }
    }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.process(): AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>? {
        if (!validateScopes()) {
            return null
        }

        val targetType = className
        val obj = AutoInitializeObject(
            targetType = targetType,
            priority = getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = isFullyPublic,
            isInitializable = asType().isAssignableTo(Initializable::class),
            originatingElement = node,
            qualifiers = getQualifiers()
        )
        return AutoInitializeModule(
            moduleName = obj.moduleName,
            objects = listOf(obj),
            topLevelClassName = environment.renderEngine.topLevelClassName(targetType)
        )
    }

    private fun Node<N, TypeName, ClassName, AnnotationSpec>.processMethod(
        receiver: Type<N, TypeName, ClassName, AnnotationSpec>?,
        returnType: Type<N, TypeName, ClassName, AnnotationSpec>,
        arguments: Sequence<Type<N, TypeName, ClassName, AnnotationSpec>>,
        output: Multimap<ClassName, AutoInitializeObject<N, TypeName, AnnotationSpec>>,
    ) {

        val module = enclosingType
            ?.let { if (it.isCompanionObject) it.enclosingType else it }
            ?: run {
                environment.logError(Errors.AutoInitialize.methodInNonModule, this@processMethod)
                return
            }

        if (!module.isAnnotatedWith(Module::class)) {
            environment.logError(Errors.AutoInitialize.methodInNonModule, node)
        }

        val isProvider = annotations.any { it.isOfType(Provides::class) }
        val isBinding = annotations.any { it.isOfType(Binds::class) }
        if (!isProvider && !isBinding) {
            environment.logError(Errors.AutoInitialize.invalidAnnotatedMethod, this)
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

        val obj = AutoInitializeObject(
            targetType = returnType.toTypeName(),
            priority = getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = isFullyPublic,
            isInitializable = returnType.isAssignableTo(Initializable::class),
            originatingElement = node,
            qualifiers = annotations.filterQualifiers().toSet()
        )
        output.put(module.className, obj)
    }

    private fun List<AnnotationModel<*, *>>.validateScopes(node: N): Boolean {
        val scopes = filter { it.isAnnotatedWith(Scope::class) }

        if (scopes.isEmpty()) {
            environment.logError(Errors.AutoInitialize.unscopedType, node)
            return false
        }
        if (scopes.none { it.isOfType(Singleton::class) }) {
            environment.logError(Errors.AutoInitialize.unscopedType, node)
            return false
        }
        return true
    }

    private fun Node<N, *, *, *>.validateScopes(): Boolean = annotations.validateScopes(node)

    private val AutoInitializeObject<*, TypeName, *>.moduleName: ClassName
        get() = with(environment.renderEngine) {
            className(
                packageName(rawType(targetType)),
                simpleNames(rawType(targetType)).joinToString(
                    prefix = "AutoInitialize",
                    postfix = "Module",
                    separator = ""
                )
            )
        }
}