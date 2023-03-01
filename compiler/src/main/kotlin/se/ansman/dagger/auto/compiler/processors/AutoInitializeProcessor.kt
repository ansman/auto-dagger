package se.ansman.dagger.auto.compiler.processors

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.android.testing.Replaces
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.models.AutoInitializeModule
import se.ansman.dagger.auto.compiler.models.AutoInitializeObject
import se.ansman.dagger.auto.compiler.processing.AnnotationModel
import se.ansman.dagger.auto.compiler.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.processing.Function
import se.ansman.dagger.auto.compiler.processing.Node
import se.ansman.dagger.auto.compiler.processing.Property
import se.ansman.dagger.auto.compiler.processing.Type
import se.ansman.dagger.auto.compiler.processing.error
import se.ansman.dagger.auto.compiler.processing.filterQualifiers
import se.ansman.dagger.auto.compiler.processing.getAnnotation
import se.ansman.dagger.auto.compiler.processing.getQualifiers
import se.ansman.dagger.auto.compiler.processing.isAnnotatedWith
import se.ansman.dagger.auto.compiler.processing.isFullyPublic
import se.ansman.dagger.auto.compiler.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.renderers.Renderer
import javax.inject.Scope
import javax.inject.Singleton
import kotlin.reflect.KClass

class AutoInitializeProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    private val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    private val logger = environment.logger.withTag("auto-initialize")
    override val annotations: Set<KClass<out Annotation>> = setOf(AutoInitialize::class)

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        val initializableObjectsByModule =
            HashMultimap.create<ModuleKey<N, ClassName>, AutoInitializeObject<TypeName, AnnotationSpec>>()
        for (node in resolver.nodesAnnotatedWith(AutoInitialize::class)) {
            when (node) {
                is ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> -> {
                    if (node.isAnnotatedWith(Replaces::class)) {
                        logger.error(Errors.Replaces.isAutoBindOrInitialize, node)
                        continue
                    }
                    if (node.isGeneric) {
                        logger.error(Errors.genericType(AutoInitialize::class), node)
                    }
                    node.validateScopes()
                    val obj = process(node)
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

        for ((moduleKey, providers) in initializableObjectsByModule.asMap()) {
            val module = with(environment.renderEngine) {
                AutoInitializeModule(
                    moduleName = rootPeerClass(
                        moduleKey.moduleName,
                        simpleNames(moduleKey.moduleName).joinToString(
                            separator = "",
                            prefix = "AutoInitialize"
                        )
                    ),
                    installation = HiltModuleBuilder.Installation.InstallIn(className(SingletonComponent::class)),
                    originatingTopLevelClassName = topLevelClassName(moduleKey.moduleName),
                    originatingElement = moduleKey.originatingElement,
                    objects = providers.toList()
                )
            }
            val file = renderer.render(module)
            environment.write(file)
        }
    }

    fun process(
        type: ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>,
    ): AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec> = with(environment.renderEngine) {
        val obj = AutoInitializeObject(
            targetType = type.className,
            priority = type.getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = type.isFullyPublic,
            isInitializable = type.asType().isAssignableTo(Initializable::class),
            qualifiers = type.getQualifiers()
        )
        return AutoInitializeModule(
            moduleName = obj.targetType.moduleName,
            installation = HiltModuleBuilder.Installation.InstallIn(className(SingletonComponent::class)),
            originatingTopLevelClassName = environment.renderEngine.topLevelClassName(type.className),
            originatingElement = type.node,
            objects = listOf(obj),
        )
    }

    private fun Node<N, TypeName, ClassName, AnnotationSpec>.processMethod(
        receiver: Type<N, TypeName, ClassName, AnnotationSpec>?,
        returnType: Type<N, TypeName, ClassName, AnnotationSpec>,
        arguments: Sequence<Type<N, TypeName, ClassName, AnnotationSpec>>,
        output: Multimap<ModuleKey<N, ClassName>, AutoInitializeObject<TypeName, AnnotationSpec>>,
    ) {

        val module = enclosingType
            ?.let { if (it.isCompanionObject) it.enclosingType else it }
            ?: run {
                logger.error(Errors.AutoInitialize.methodInNonModule, this@processMethod)
                return
            }

        if (!module.isAnnotatedWith(Module::class)) {
            logger.error(Errors.AutoInitialize.methodInNonModule, node)
        }

        val isProvider = annotations.any { it.isOfType(Provides::class) }
        val isBinding = annotations.any { it.isOfType(Binds::class) }
        if (!isProvider && !isBinding) {
            logger.error(Errors.AutoInitialize.invalidAnnotatedMethod, this)
            return
        }

        val annotations = annotations + if (isBinding) {
            receiver?.declaration?.annotations
                ?: arguments.firstOrNull()?.declaration?.annotations
                ?: emptyList()
        } else {
            returnType.declaration.annotations
        }

        annotations.validateScopes(node)
        val moduleKey = ModuleKey(module.className, module.node)
        val obj = AutoInitializeObject(
            targetType = returnType.toTypeName(),
            priority = getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = isFullyPublic,
            isInitializable = returnType.isAssignableTo(Initializable::class),
            qualifiers = annotations.filterQualifiers().toSet()
        )
        output.put(moduleKey, obj)
    }

    private fun List<AnnotationModel<*, *>>.validateScopes(node: N) {
        val scopes = filter { it.isAnnotatedWith(Scope::class) }

        if (scopes.isEmpty()) {
            logger.error(Errors.AutoInitialize.unscopedType, node)
        } else if (scopes.none { it.isOfType(Singleton::class) }) {
            logger.error(Errors.AutoInitialize.unscopedType, node)
        }
    }

    private fun Node<N, *, *, *>.validateScopes() = annotations.validateScopes(node)

    private val ClassName.moduleName: ClassName
        get() = environment.renderEngine.rootPeerClass(
            this,
            environment.renderEngine.simpleNames(this).joinToString(
                prefix = "AutoInitialize",
                postfix = "Module",
                separator = ""
            )
        )

    private data class ModuleKey<out Node, ClassName>(
        val moduleName: ClassName,
        val originatingElement: Node,
    )
}