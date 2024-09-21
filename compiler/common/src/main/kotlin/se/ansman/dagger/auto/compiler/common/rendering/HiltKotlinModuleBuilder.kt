package se.ansman.dagger.auto.compiler.common.rendering

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
import se.ansman.dagger.auto.compiler.common.ksp.addMemberClassArray
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rawType
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.ProviderMode
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
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
        parameters: List<HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>>,
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
        sourceType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
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
        type: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
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
        sourceType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>?,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
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
            .addAnnotation(AnnotationSpec.builder(Generated::class)
                .addMember("%S", info.processor.name)
                .build())
            .addAnnotation(Module::class)
            .addAnnotation(when (val installation = info.installation) {
                is HiltModuleBuilder.Installation.InstallIn ->
                    AnnotationSpec.builder(InstallIn::class)
                        .applyEach(installation.components) { addMember("%T::class", it) }
                        .build()

                is HiltModuleBuilder.Installation.TestInstallIn ->
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
        val Factory = HiltModuleBuilder.Factory(::HiltKotlinModuleBuilder)
    }
}

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.toParameterSpec(
    nameAllocator: NameAllocator,
): ParameterSpec =
    ParameterSpec
        .builder(nameAllocator.newName(asParameterName()), asTypeName())
        .addAnnotations(qualifiers)
        .build()

@OptIn(DelicateKotlinPoetApi::class)
private fun ProviderMode<AnnotationSpec>.asAnnotations() =
    asAnnotations(AnnotationSpec::get)

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asParameterName(): String =
    asParameterName { rawType().simpleName }

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asTypeName(): TypeName =
    asTypeName { rawType, arguments -> rawType.asClassName().parameterizedBy(arguments) }