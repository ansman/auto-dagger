package se.ansman.dagger.auto.compiler.androidx.room.renderer

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.androidx.room.AndroidXRoomDatabaseModuleRenderer
import se.ansman.dagger.auto.compiler.common.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.ksp.HiltKotlinModuleBuilder
import se.ansman.dagger.auto.compiler.ksp.KotlinPoetRenderEngine

object KotlinAndroidXRoomDatabaseModuleRenderer :
    AndroidXRoomDatabaseModuleRenderer<KSNode, TypeName, ClassName, AnnotationSpec, ParameterSpec, FunSpec.Builder, CodeBlock, FileSpec>(
        KotlinPoetRenderEngine,
        HiltKotlinModuleBuilder.Factory
    ) {

    override fun FunSpec.Builder.provideDao(
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