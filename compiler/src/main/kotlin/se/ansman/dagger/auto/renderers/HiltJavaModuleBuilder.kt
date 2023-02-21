package se.ansman.dagger.auto.renderers

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
import se.ansman.dagger.auto.asClassName
import se.ansman.dagger.auto.generatedFileComment
import se.ansman.dagger.auto.renderers.HiltModuleBuilder.ProviderMode
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

fun buildJavaHiltModule(
    moduleName: ClassName,
    installInComponent: ClassName,
    originatingTopLevelClassName: ClassName,
    builder: HiltModuleBuilder<Element, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile>.() -> Unit,
): JavaFile =
    HiltJavaModuleBuilder(moduleName, installInComponent, originatingTopLevelClassName)
        .apply(builder)
        .build()

class HiltJavaModuleBuilder(
    private val moduleName: ClassName,
    installInComponent: ClassName,
    originatingTopLevelClassName: ClassName,
) : HiltModuleBuilder<Element, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, JavaFile> {
    private val nameAllocator = NameAllocator()
    private val typeSpec = TypeSpec.classBuilder(moduleName)
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build()
        )
        .addAnnotation(Module::class.java)
        .addAnnotation(
            AnnotationSpec.builder(InstallIn::class.java)
                .addMember("value", "\$T.class", installInComponent)
                .build()
        )
        .addAnnotation(
            AnnotationSpec.builder(OriginatingElement::class.java)
                .addMember("topLevelClass", "\$T.class", originatingTopLevelClassName)
                .build()
        )

    override fun addProvider(
        name: String,
        parameters: List<HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>>,
        mode: ProviderMode<AnnotationSpec>,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        originatingElement: Element,
        contents: (List<ParameterSpec>) -> CodeBlock
    ) {
        typeSpec.addOriginatingElement(originatingElement)
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
        originatingElement: Element,
    ) {
        typeSpec.addOriginatingElement(originatingElement)
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
        JavaFile.builder(moduleName.packageName(), typeSpec.build())
            .addFileComment(generatedFileComment)
            .build()
}

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.toParameterSpec(
    nameAllocator: NameAllocator
): ParameterSpec =
    ParameterSpec
        .builder(asTypeName(), nameAllocator.newName(asParameterName()))
        .addAnnotations(qualifiers)
        .build()

private fun ProviderMode<AnnotationSpec>.asAnnotations() =
    asAnnotations(AnnotationSpec::get)

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asParameterName(): String =
    asParameterName { asClassName().simpleName() }

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asTypeName(): TypeName =
    asTypeName { rawType, arguments ->
        ParameterizedTypeName.get(ClassName.get(rawType.java), *arguments.toTypedArray())
    }