package se.ansman.dagger.auto.compiler.autoinitialize

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
import se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule
import se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeObject
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.*
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import javax.inject.Scope
import javax.inject.Singleton

class AutoInitializeProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val logger = environment.logger.withTag("auto-initialize")
    override val annotations: Set<String> = setOf(AutoInitialize::class.java.canonicalName)

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

                is ExecutableNode<N, TypeName, ClassName, AnnotationSpec> -> node.processMethod(
                    output = initializableObjectsByModule
                )

                else -> throw IllegalArgumentException("Unknown node $this")
            }
        }

        for ((moduleKey, providers) in initializableObjectsByModule.asMap()) {
            val module = with(environment) {
                AutoInitializeModule(
                    processor = this@AutoInitializeProcessor.javaClass,
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
    ): AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec> = with(environment) {
        val obj = AutoInitializeObject(
            targetType = type.className,
            priority = type.getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = type.isFullyPublic,
            isInitializable = type.asType().isAssignableTo(Initializable::class),
            qualifiers = type.getQualifiers()
        )
        return AutoInitializeModule(
            processor = this@AutoInitializeProcessor.javaClass,
            moduleName = obj.targetType.moduleName,
            installation = HiltModuleBuilder.Installation.InstallIn(className(SingletonComponent::class)),
            originatingTopLevelClassName = environment.topLevelClassName(type.className),
            originatingElement = type.node,
            objects = listOf(obj),
        )
    }

    private fun ExecutableNode<N, TypeName, ClassName, AnnotationSpec>.processMethod(
        output: Multimap<ModuleKey<N, ClassName>, AutoInitializeObject<TypeName, AnnotationSpec>>,
    ) {

        val module = enclosingType
            ?.let { if (it.kind == Kind.CompanionObject) it.enclosingType else it }
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
            returnType.declaration?.annotations ?: emptyList()
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
        get() = environment.rootPeerClass(
            this,
            environment.simpleNames(this).joinToString(
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