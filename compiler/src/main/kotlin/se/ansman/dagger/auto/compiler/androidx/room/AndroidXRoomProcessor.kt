package se.ansman.dagger.auto.compiler.androidx.room

import dagger.hilt.components.SingletonComponent
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.androidx.room.renderer.AndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration
import se.ansman.dagger.auto.compiler.common.processing.Function
import se.ansman.dagger.auto.compiler.common.processing.Property
import se.ansman.dagger.auto.compiler.common.processing.getAnnotation
import se.ansman.dagger.auto.compiler.common.processing.getValue
import se.ansman.dagger.auto.compiler.common.processing.isAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.isFullyPublic
import se.ansman.dagger.auto.compiler.common.processing.lookupType
import se.ansman.dagger.auto.compiler.common.processing.nodesAnnotatedWith
import se.ansman.dagger.auto.compiler.common.processing.rootPeerClass
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.utils.validateComponent

class AndroidXRoomProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    override val environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    private val renderer: AndroidXRoomDatabaseModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, F>
) : Processor<N, TypeName, ClassName, AnnotationSpec> {
    override val logger = environment.logger.withTag("androidx.room")
    override val annotations: Set<String> = setOf(
        AutoProvideDaos::class.java.canonicalName,
    )

    override fun process(resolver: AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec>) {
        logger.info("@AutoProvideDaos processing started")
        resolver.nodesAnnotatedWith(AutoProvideDaos::class)
            .map { it as ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> }
            .map { database ->
                logger.info("Processing ${database.className}")
                val targetComponent = database
                    .getAnnotation(AutoProvideDaos::class)!!
                    .getValue<ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>>("inComponent")
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

    private val ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.daos: Sequence<AndroidXRoomDatabaseModule.Dao<TypeName>>
        get() = declaredNodes
            .asSequence()
            .filter { it.returnType.declaration?.isAnnotatedWith("androidx.room.Dao") == true }
            .mapNotNull inner@{ dao ->
                AndroidXRoomDatabaseModule.Dao(
                    type = dao.returnType.toTypeName(),
                    accessor = when (dao) {
                        is Property<N, TypeName, ClassName, AnnotationSpec> ->
                            AndroidXRoomDatabaseModule.Dao.Accessor.Property(dao.name)

                        is Function<N, TypeName, ClassName, AnnotationSpec> -> {
                            if (dao.isConstructor) {
                                return@inner null
                            }
                            AndroidXRoomDatabaseModule.Dao.Accessor.Function(dao.name)
                        }

                        else -> {
                            logger.error("Unknown node type ${dao.javaClass}", dao.node)
                            return@inner null
                        }
                    },
                    isPublic = dao.isFullyPublic,
                )
            }
            .distinctBy { it.type }

    private fun ClassDeclaration<N, TypeName, ClassName, AnnotationSpec>.validateDatabase() {
        if (
            !isAnnotatedWith("androidx.room.Database") ||
            !asType().isAssignableTo("androidx.room.RoomDatabase")
        ) {
            logger.error(Errors.AndroidX.Room.notADatabase, node)
            return
        }

        if (superclass?.declaration?.className?.let(environment::qualifierName) != "androidx.room.RoomDatabase") {
            logger.error(Errors.AndroidX.Room.typeMustDirectlyExtendRoomDatabase, node)
        }
    }
}
