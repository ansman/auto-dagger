package se.ansman.dagger.auto.compiler.androidx.room.renderer

import se.ansman.dagger.auto.compiler.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.Renderer

abstract class AndroidXRoomDatabaseModuleRenderer<Node, TypeName, ClassName : TypeName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>(
    private val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>,
    private val builderFactory: HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile>,
) : Renderer<AndroidXRoomDatabaseModule<Node, TypeName, ClassName>, SourceFile> {

    final override fun render(input: AndroidXRoomDatabaseModule<Node, TypeName, ClassName>): SourceFile =
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

    protected abstract fun provideDao(
        dao: AndroidXRoomDatabaseModule.Dao<TypeName>,
        databaseParameter: ParameterSpec
    ): CodeBlock
}