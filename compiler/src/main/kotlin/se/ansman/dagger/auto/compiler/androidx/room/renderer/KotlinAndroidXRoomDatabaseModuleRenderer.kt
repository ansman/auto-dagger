package se.ansman.dagger.auto.compiler.androidx.room.renderer

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.common.ksp.KotlinPoetRenderEngine
import se.ansman.dagger.auto.compiler.common.rendering.HiltKotlinModuleBuilder

object KotlinAndroidXRoomDatabaseModuleRenderer :
    AndroidXRoomDatabaseModuleRenderer<KSDeclaration, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    ) {

    override fun provideDao(
        dao: AndroidXRoomDatabaseModule.Dao<TypeName>,
        databaseParameter: ParameterSpec
    ): CodeBlock =
        CodeBlock.builder()
            .add("return·%N.", databaseParameter)
            .add(
                when (dao.accessor) {
                    is AndroidXRoomDatabaseModule.Dao.Accessor.Function -> CodeBlock.of("%N()", dao.accessor.name)
                    is AndroidXRoomDatabaseModule.Dao.Accessor.Property -> CodeBlock.of("%N", dao.accessor.name)
                }
            )
            .build()
}