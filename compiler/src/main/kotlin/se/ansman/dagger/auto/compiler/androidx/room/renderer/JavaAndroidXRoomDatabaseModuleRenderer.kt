package se.ansman.dagger.auto.compiler.androidx.room.renderer

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.androidx.room.models.AndroidXRoomDatabaseModule
import se.ansman.dagger.auto.compiler.kapt.JavaPoetRenderEngine
import se.ansman.dagger.auto.compiler.kapt.HiltJavaModuleBuilder
import javax.lang.model.element.Element

object JavaAndroidXRoomDatabaseModuleRenderer :
    AndroidXRoomDatabaseModuleRenderer<Element, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile>(
        JavaPoetRenderEngine,
        HiltJavaModuleBuilder.Factory
    ) {

    override fun provideDao(
        dao: AndroidXRoomDatabaseModule.Dao<TypeName>,
        databaseParameter: ParameterSpec
    ): CodeBlock =
        CodeBlock.builder()
            .add("return \$N.", databaseParameter)
            .add(
                when (dao.accessor) {
                    is AndroidXRoomDatabaseModule.Dao.Accessor.Function -> CodeBlock.of("\$N()", dao.accessor.name)
                    is AndroidXRoomDatabaseModule.Dao.Accessor.Property -> CodeBlock.of("get\$N())", dao.accessor.name.replaceFirstChar(Char::uppercaseChar))
                }
            )
            .build()
}