package se.ansman.dagger.auto.compiler.common.androidx.room

import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.compiler.common.Errors
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration
import se.ansman.dagger.auto.compiler.common.processing.model.ExecutableDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.FunctionDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.PropertyDeclaration
import se.ansman.dagger.auto.compiler.common.processing.model.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.model.getClassValue
import se.ansman.dagger.auto.compiler.common.processing.model.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.model.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.utils.validateComponent

class AndroidXRoomProcessor<E, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<E, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: AndroidXRoomDatabaseModuleRenderer<E, TypeName, ClassName, AnnotationSpec, *, *, *, F>
) : Processor<E, TypeName, ClassName, AnnotationSpec> {
    override val logger = environment.logger.withTag("androidx.room")
    private val annotation = "se.ansman.dagger.auto.androidx.room.AutoProvideDaos"
    override val annotations: Set<String> = setOf(annotation)

    override fun process(
        elements: Map<String, List<Declaration<E, TypeName, ClassName>>>,
        resolver: AutoDaggerResolver<E, TypeName, ClassName>
    ) {
        logger.info("@AutoProvideDaos processing started")
        elements.getValue(annotation)
            .asSequence()
            .filterIsInstance<ClassDeclaration<E, TypeName, ClassName>>()
            .map { database ->
                logger.info("Processing ${database.className}")
                val targetComponent = database
                    .getAnnotation(annotation)!!
                    .getClassValue("inComponent")
                    ?: resolver.lookupType(SingletonComponent::class)

                database.validateDatabase()
                validateComponent(database, resolver, targetComponent)

                AndroidXRoomDatabaseModule(
                    processor = javaClass,
                    moduleName = environment.rootPeerClass(
                        database.className,
                        environment.simpleNames(database.className).joinToString(
                            separator = "",
                            prefix = "AutoDagger",
                            postfix = "Module"
                        )
                    ),
                    installation = HiltModuleBuilder.Installation.InstallIn(targetComponent.className),
                    originatingTopLevelClassName = environment.topLevelClassName(database.className),
                    originatingElement = database.node,
                    databaseType = database.className,
                    daos = database.daos.toList(),
                )
            }
            .map(renderer::render)
            .forEach(environment::write)
    }

    private val ClassDeclaration<E, TypeName, ClassName>.daos: Sequence<AndroidXRoomDatabaseModule.Dao<TypeName>>
        get() = declarations
            .asSequence()
            .filterIsInstance<ExecutableDeclaration<E, TypeName, ClassName>>()
            .filter { it.returnType.declaration?.isAnnotatedWith("androidx.room.Dao") == true }
            .mapNotNull inner@{ dao ->
                AndroidXRoomDatabaseModule.Dao(
                    type = dao.returnType.toTypeName(),
                    accessor = when (dao) {
                        is PropertyDeclaration<E, TypeName, ClassName> ->
                            AndroidXRoomDatabaseModule.Dao.Accessor.Property(dao.name)

                        is FunctionDeclaration<E, TypeName, ClassName> -> {
                            if (dao.isConstructor) {
                                return@inner null
                            }
                            AndroidXRoomDatabaseModule.Dao.Accessor.Function(dao.name)
                        }

                        else -> error("Unknown declaration type: $dao")
                    },
                    isPublic = dao.isFullyPublic,
                )
            }
            .distinctBy { it.type }

    private fun ClassDeclaration<E, TypeName, ClassName>.validateDatabase() {
        if (
            !isAnnotatedWith("androidx.room.Database") ||
            !asType().isAssignableTo("androidx.room.RoomDatabase")
        ) {
            logger.error(Errors.AndroidX.Room.notADatabase, node)
            return
        }

        if (superclass?.declaration?.className?.let(environment::qualifiedName) != "androidx.room.RoomDatabase") {
            logger.error(Errors.AndroidX.Room.typeMustDirectlyExtendRoomDatabase, node)
        }
    }
}