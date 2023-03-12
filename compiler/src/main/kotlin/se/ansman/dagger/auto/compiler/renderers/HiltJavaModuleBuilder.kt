package se.ansman.dagger.auto.compiler.renderers

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.NameAllocator
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import se.ansman.dagger.auto.compiler.applyEach
import se.ansman.dagger.auto.compiler.rawType
import se.ansman.dagger.auto.compiler.generatedFileComment
import se.ansman.dagger.auto.compiler.models.HiltModule
import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder.ProviderMode
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

class HiltJavaModuleBuilder private constructor(
    private val info: HiltModule<Element, ClassName>,
) : HiltModuleBuilder<Element, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile> {
    private val nameAllocator = NameAllocator()
    private val typeSpec = TypeSpec.classBuilder(info.moduleName)
        .addOriginatingElement(info.originatingElement)
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
        )
        .addAnnotation(Module::class.java)
        .addAnnotation(
            when (val installation = info.installation) {
                is HiltModuleBuilder.Installation.InstallIn ->
                    AnnotationSpec.builder(InstallIn::class.java)
                        .applyEach(installation.components) { addMember("value", "\$T.class", it) }
                        .build()

                is HiltModuleBuilder.Installation.TestInstallIn ->
                    AnnotationSpec.builder(ClassName.get("dagger.hilt.testing", "TestInstallIn"))
                        .applyEach(installation.components) { addMember("components", "\$T.class", it) }
                        .applyEach(installation.replaces) { addMember("replaces", "\$T.class", it) }
                        .build()
            }
        )
        .addAnnotation(
            AnnotationSpec.builder(OriginatingElement::class.java)
                .addMember("topLevelClass", "\$T.class", info.originatingTopLevelClassName)
                .build()
        )

    override fun addProvider(
        name: String,
        parameters: List<HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>>,
        mode: ProviderMode<AnnotationSpec>,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        contents: (List<ParameterSpec>) -> CodeBlock,
    ) = apply {
        val parameterNameAllocator = NameAllocator()
        val params = parameters.map { it.toParameterSpec(parameterNameAllocator) }
        typeSpec.addMethod(
            MethodSpec.methodBuilder(nameAllocator.newName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(Provides::class.java)
                .addAnnotations(mode.asAnnotations())
                .addParameters(params)
                .addStatement(contents(params))
                .addAnnotations(returnType.qualifiers)
                .returns(returnType.type)
                .build()
        )
    }

    override fun addBinding(
        name: String,
        sourceType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        mode: ProviderMode<AnnotationSpec>,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
    ) = apply {
        typeSpec.addMethod(
            MethodSpec.methodBuilder(nameAllocator.newName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(Binds::class.java)
                .addAnnotations(mode.asAnnotations())
                .addParameter(sourceType.toParameterSpec(NameAllocator()))
                .addAnnotations(returnType.qualifiers)
                .returns(returnType.type)
                .build()
        )
    }

    override fun build(): JavaFile =
        JavaFile.builder(info.moduleName.packageName(), typeSpec.build())
            .addFileComment(generatedFileComment)
            .build()

    companion object {
        val Factory = HiltModuleBuilder.Factory(::HiltJavaModuleBuilder)
    }
}

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.toParameterSpec(
    nameAllocator: NameAllocator,
): ParameterSpec =
    ParameterSpec
        .builder(asTypeName(), nameAllocator.newName(asParameterName()))
        .addAnnotations(qualifiers)
        .build()

private fun ProviderMode<AnnotationSpec>.asAnnotations() =
    asAnnotations(AnnotationSpec::get)

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asParameterName(): String =
    asParameterName { rawType().simpleName() }

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asTypeName(): TypeName =
    asTypeName { rawType, arguments ->
        ParameterizedTypeName.get(ClassName.get(rawType.java), *arguments.map { it.box() }.toTypedArray())
    }