package se.ansman.dagger.auto.compiler.common.autoinitialize

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.AutoInitialize
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.autoinitialize.models.AutoInitializeModule
import se.ansman.dagger.auto.compiler.common.autoinitialize.models.AutoInitializeObject
import se.ansman.dagger.auto.compiler.common.get
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.error
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration.Kind
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.ExecutableDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.enclosingType
import se.ansman.dagger.auto.compiler.common.processing.model.filterQualifiers
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getQualifiers
import se.ansman.dagger.auto.compiler.common.processing.model.getValue
import se.ansman.dagger.auto.compiler.common.processing.model.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.model.isAssignableTo
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.model.isType
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer
import javax.inject.Scope
import javax.inject.Singleton

class AutoInitializeProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: Renderer<AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec>, F>,
) : Processor<N, TypeName, ClassName, AnnotationSpec>,
    RenderEngine<N, TypeName, ClassName, AnnotationSpec> by environment {
    override val logger = environment.logger.withTag("auto-initialize")
    override val annotations: Set<String> = setOf(AutoInitialize::class.java.canonicalName)

    override fun process(
        elements: Map<String, List<Declaration<N, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<N, TypeName, ClassName>
    ) {
        val initializableObjectsByModule = mutableMapOf<ModuleKey<N, ClassName>, MutableList<AutoInitializeObject<TypeName, AnnotationSpec>>>()
        for (node in elements[AutoInitialize::class]) {
            when (node) {
                is ClassDeclaration<N, TypeName, ClassName> -> {
                    if (node.isAnnotatedWith("se.ansman.dagger.auto.android.testing.Replaces")) {
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

                is ExecutableDeclaration<N, TypeName, ClassName> -> node.processMethod(
                    output = initializableObjectsByModule
                )

                else -> throw IllegalArgumentException("Unknown node $this")
            }
        }

        for ((moduleKey, providers) in initializableObjectsByModule) {
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
        type: ClassDeclaration<N, TypeName, ClassName>,
    ): AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec> = with(environment) {
        val obj = AutoInitializeObject(
            targetType = type.className,
            priority = type.getAnnotation(AutoInitialize::class)!!.getValue("priority"),
            isPublic = type.isFullyPublic,
            isInitializable = type.asType().isAssignableTo(Initializable::class),
            qualifiers = type.getQualifiers().toAnnotationSpecs().toSet()
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

    private fun ExecutableDeclaration<N, TypeName, ClassName>.processMethod(
        output: MutableMap<ModuleKey<N, ClassName>, MutableList<AutoInitializeObject<TypeName, AnnotationSpec>>>,
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

        val isProvider = annotations.any { it.declaration.isType(Provides::class) }
        val isBinding = annotations.any { it.declaration.isType(Binds::class) }
        if (!isProvider && !isBinding) {
            logger.error(Errors.AutoInitialize.invalidAnnotatedMethod, this)
            return
        }

        val annotations = annotations + if (isBinding) {
            receiver?.declaration?.annotations
                ?: valueParameters.firstOrNull()?.type?.declaration?.annotations
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
            qualifiers = annotations.filterQualifiers().toAnnotationSpecs().toSet()
        )
        output.getOrPut(moduleKey, ::mutableListOf).add(obj)
    }

    private fun List<AnnotationNode<*, *, *>>.validateScopes(node: N) {
        val scopes = filter { it.isAnnotatedWith(Scope::class) }

        if (scopes.isEmpty()) {
            logger.error(Errors.AutoInitialize.unscopedType, node)
        } else if (scopes.none { it.declaration.isType<Singleton>() }) {
            logger.error(Errors.AutoInitialize.unscopedType, node)
        }
    }

    private fun Declaration<N, *, *>.validateScopes() = annotations.validateScopes(node)

    private val ClassName.moduleName: ClassName
        get() = environment.rootPeerClass(
            this,
            environment.simpleNames(this).joinToString(
                prefix = "AutoInitialize",
                postfix = "Module",
                separator = ""
            )
        )

    private data class ModuleKey<N, ClassName>(
        val moduleName: ClassName,
        val originatingElement: N,
    )
}