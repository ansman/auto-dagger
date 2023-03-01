package se.ansman.dagger.auto.compiler.renderers

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.symbol.KSNode
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import se.ansman.dagger.auto.compiler.applyEach
import se.ansman.dagger.auto.compiler.applyIf
import se.ansman.dagger.auto.compiler.asClassName
import se.ansman.dagger.auto.compiler.generatedFileComment
import se.ansman.dagger.auto.compiler.ksp.addMemberClassArray
import se.ansman.dagger.auto.compiler.models.HiltModule
import se.ansman.dagger.auto.compiler.renderers.HiltModuleBuilder.ProviderMode

class HiltKotlinModuleBuilder private constructor(
    private val info: HiltModule<KSNode, ClassName>,
) : HiltModuleBuilder<KSNode, TypeName, AnnotationSpec, ParameterSpec, CodeBlock, FileSpec> {
    private val nameAllocator = NameAllocator()
    private val bindings = mutableListOf<FunSpec>()
    private val providers = mutableListOf<FunSpec>()

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
        mode: ProviderMode<AnnotationSpec>,
        returnType: HiltModuleBuilder.DaggerType<TypeName, AnnotationSpec>,
        isPublic: Boolean,
    ) = apply {
        bindings += FunSpec.builder(nameAllocator.newName(name))
            .addModifiers(if (isPublic) KModifier.PUBLIC else KModifier.INTERNAL, KModifier.ABSTRACT)
            .addAnnotation(Binds::class)
            .addAnnotations(mode.asAnnotations())
            .addAnnotations(sourceType.qualifiers.map {
                it.toBuilder().useSiteTarget(AnnotationSpec.UseSiteTarget.RECEIVER).build()
            })
            .receiver(sourceType.type)
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
    asParameterName { asClassName().simpleName }

private fun HiltModuleBuilder.Parameter<TypeName, AnnotationSpec>.asTypeName(): TypeName =
    asTypeName { rawType, arguments -> rawType.asClassName().parameterizedBy(arguments) }