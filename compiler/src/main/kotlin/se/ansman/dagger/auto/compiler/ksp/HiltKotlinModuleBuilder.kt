package se.ansman.dagger.auto.compiler.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.NameAllocator
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import se.ansman.dagger.auto.compiler.common.applyEach
import se.ansman.dagger.auto.compiler.common.applyIf
import se.ansman.dagger.auto.compiler.common.generatedFileComment
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rawType
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.DaggerType
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Factory
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Parameter
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.ProviderMode
import se.ansman.dagger.auto.compiler.common.rendering.asAnnotations
import se.ansman.dagger.auto.compiler.common.rendering.asParameterName
import se.ansman.dagger.auto.compiler.common.rendering.asTypeName
import javax.annotation.processing.Generated
import kotlin.reflect.KClass

class HiltKotlinModuleBuilder private constructor(
    private val info: HiltModule<KSDeclaration, ClassName>,
) : HiltModuleBuilder<KSDeclaration, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec> {
    private val nameAllocator = NameAllocator()
    private val bindings = mutableListOf<FunSpec>()
    private val providers = mutableListOf<FunSpec>()

    override fun addProvider(
        name: String,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        parameters: List<Parameter<TypeName, AnnotationSpec>>,
        mode: ProviderMode<AnnotationSpec>,
        contents: (List<ParameterSpec>) -> CodeBlock
    ) = apply {
        val parameterNameAllocator = NameAllocator()
        val params = parameters.map { it.toParameterSpec(parameterNameAllocator) }
        providers += FunSpec.builder(nameAllocator.newName(name))
            .addModifiers(if (isPublic) KModifier.PUBLIC else KModifier.INTERNAL)
            .addAnnotation(Provides::class)
            .addAnnotations(mode.asAnnotations())
            .addParameters(params)
            .addCode(contents(params))
            .addAnnotations(returnType.qualifiers)
            .returns(returnType.type)
            .build()
    }

    override fun addBinding(
        name: String,
        sourceType: DaggerType<TypeName, AnnotationSpec>,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        mode: ProviderMode<AnnotationSpec>
    ) = addBinding(
        bindingAnnotation = Binds::class,
        name = name,
        sourceType = sourceType,
        returnType = returnType,
        isPublic = isPublic,
        mode = mode,
    )

    override fun addOptionalBinding(
        name: String,
        type: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean
    ): HiltModuleBuilder<KSDeclaration, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec> =
        addBinding(
            bindingAnnotation = BindsOptionalOf::class,
            name = name,
            sourceType = null,
            returnType = type,
            isPublic = isPublic,
            mode = ProviderMode.Single
        )

    private fun addBinding(
        bindingAnnotation: KClass<out Annotation>,
        name: String,
        sourceType: DaggerType<TypeName, AnnotationSpec>?,
        returnType: DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        mode: ProviderMode<AnnotationSpec>
    ) = apply {
        bindings += FunSpec.builder(nameAllocator.newName(name))
            .addModifiers(if (isPublic) KModifier.PUBLIC else KModifier.INTERNAL, KModifier.ABSTRACT)
            .addAnnotation(bindingAnnotation)
            .addAnnotations(mode.asAnnotations())
            .apply {
                if (sourceType != null) {
                    addParameter(sourceType.toParameterSpec(nameAllocator))
                }
            }
            .addAnnotations(returnType.qualifiers)
            .returns(returnType.type)
            .build()
    }

    override fun build(): FileSpec {
        val typeSpec = when {
            bindings.isNotEmpty() ->
                TypeSpec.classBuilder(info.moduleName)
                    .addModifiers(KModifier.ABSTRACT)
                    .primaryConstructor(FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build())
                    .addFunctions(bindings)
                    .applyIf(providers.isNotEmpty()) {
                        addType(
                            TypeSpec.companionObjectBuilder()
                                .addFunctions(providers)
                                .build()
                        )
                    }

            else ->
                TypeSpec.objectBuilder(info.moduleName)
                    .addFunctions(providers)
        }

        typeSpec
            .apply { info.originatingElement.containingFile?.let(::addOriginatingKSFile) }
            .addAnnotation(
                AnnotationSpec.builder(Generated::class)
                .addMember("%S", info.processor.name)
                .build())
            .addAnnotation(Module::class)
            .addAnnotation(when (val installation = info.installation) {
                is Installation.InstallIn ->
                    AnnotationSpec.builder(InstallIn::class)
                        .applyEach(installation.components) { addMember("%T::class", it) }
                        .build()

                is Installation.TestInstallIn ->
                    AnnotationSpec.builder(ClassName("dagger.hilt.testing", "TestInstallIn"))
                        .addMemberClassArray("components", installation.components)
                        .addMemberClassArray("replaces", installation.replaces)
                        .build()
            })
            .addAnnotation(
                AnnotationSpec.builder(OriginatingElement::class)
                    .addMember("topLevelClass = %T::class", info.originatingTopLevelClassName)
                    .build()
            )

        return FileSpec.builder(info.moduleName.packageName, info.moduleName.simpleName)
            .addFileComment(generatedFileComment)
            .addType(typeSpec.build())
            .build()
    }

    companion object {
        val Factory = Factory(::HiltKotlinModuleBuilder)
    }
}

private fun Parameter<TypeName, AnnotationSpec>.toParameterSpec(
    nameAllocator: NameAllocator,
): ParameterSpec =
    ParameterSpec
        .builder(nameAllocator.newName(asParameterName()), asTypeName())
        .addAnnotations(qualifiers)
        .build()

@OptIn(DelicateKotlinPoetApi::class)
private fun ProviderMode<AnnotationSpec>.asAnnotations() =
    asAnnotations(AnnotationSpec::get)

private fun Parameter<TypeName, AnnotationSpec>.asParameterName(): String =
    asParameterName { rawType().simpleName }

private fun Parameter<TypeName, AnnotationSpec>.asTypeName(): TypeName =
    asTypeName { rawType, arguments -> rawType.asClassName().parameterizedBy(arguments) }