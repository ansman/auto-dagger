package se.ansman.dagger.auto.compiler.common.androidx.room

import se.ansman.dagger.auto.compiler.common.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer

abstract class AndroidXRoomDatabaseModuleRenderer<E, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<E, TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<E, TypeName, ClassName, AnnotationSpec, ParameterSpec, ProviderContext, CodeBlock, SourceFile>,
) : Renderer<AndroidXRoomDatabaseModule<E, TypeName, ClassName>, SourceFile> {

    final override fun render(input: AndroidXRoomDatabaseModule<E, TypeName, ClassName>): SourceFile =
        builderFactory
            .create(input)
            .applyEach(input.daos) { dao ->
                addProvider(
                    name = "provide${renderEngine.simpleName(dao.type)}",
                    parameters = listOf(HiltModuleBuilder.DaggerType(input.databaseType)),
                    returnType = HiltModuleBuilder.DaggerType(dao.type),
                    isPublic = dao.isPublic,
                ) { parameters ->
                    provideDao(dao, parameters.single())
                }
            }
            .build()

    protected abstract fun ProviderContext.provideDao(
        dao: AndroidXRoomDatabaseModule.Dao<TypeName>,
        databaseParameter: ParameterSpec
    ): CodeBlock
}