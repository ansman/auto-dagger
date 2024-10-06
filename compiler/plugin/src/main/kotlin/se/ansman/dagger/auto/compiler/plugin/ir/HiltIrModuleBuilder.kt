package se.ansman.dagger.auto.compiler.plugin.ir

import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildValueParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.name.Name
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.DaggerType
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Parameter
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.ProviderMode
import se.ansman.dagger.auto.compiler.common.rendering.asAnnotations
import se.ansman.dagger.auto.compiler.common.rendering.asParameterName
import se.ansman.dagger.auto.compiler.common.rendering.asTypeName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName.Companion.toKirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirSimpleTypeArgument
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName
import se.ansman.dagger.auto.internal.AutoDaggerGenerated
import kotlin.reflect.KClass

class HiltIrModuleBuilder private constructor(
    private val environment: KirEnvironment,
    private val info: HiltModule<IrElement, KirClassName>,
) : HiltModuleBuilder<IrElement, KirTypeName, KirAnnotationSpec, KirParameterSpec, IrFunction, KirCodeBlock, KirClass> {
    private val nameAllocator = NameAllocator()
    private val bindings = mutableListOf<IrClass.() -> Unit>()
    private val providers = mutableListOf<IrClass.() -> Unit>()

    override fun addProvider(
        name: String,
        returnType: DaggerType<KirTypeName, KirAnnotationSpec>,
        isPublic: Boolean,
        parameters: List<Parameter<KirTypeName, KirAnnotationSpec>>,
        mode: ProviderMode<KirAnnotationSpec>,
        contents: IrFunction.(List<KirParameterSpec>) -> KirCodeBlock
    ) = apply {
        val parameterNameAllocator = NameAllocator()
        providers += {
            addFunction {
                this.name = Name.identifier(nameAllocator.newName(name))
                visibility = if (isPublic) DescriptorVisibilities.PUBLIC else DescriptorVisibilities.INTERNAL
                this.returnType = returnType.type.toIrType(environment)
            }.apply {
                dispatchReceiverParameter = thisReceiver
                for (parameter in parameters) {
                    addValueParameter(parameter, parameterNameAllocator)
                }
                annotations = buildList {
                    add(environment.renderEngine.createAnnotation<Provides>())
                    addAll(mode.asAnnotations())
                    addAll(returnType.qualifiers)
                }
                body = contents(valueParameters)
            }
        }
    }

    override fun addBinding(
        name: String,
        sourceType: DaggerType<KirTypeName, KirAnnotationSpec>,
        returnType: DaggerType<KirTypeName, KirAnnotationSpec>,
        isPublic: Boolean,
        mode: ProviderMode<KirAnnotationSpec>
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
        type: DaggerType<KirTypeName, KirAnnotationSpec>,
        isPublic: Boolean
    ) = addBinding(
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
        sourceType: DaggerType<KirTypeName, KirAnnotationSpec>?,
        returnType: DaggerType<KirTypeName, KirAnnotationSpec>,
        isPublic: Boolean,
        mode: ProviderMode<KirAnnotationSpec>
    ) = apply {
        bindings += {
            addFunction {
                this.name = Name.identifier(nameAllocator.newName(name))
                visibility = if (isPublic) DescriptorVisibilities.PUBLIC else DescriptorVisibilities.INTERNAL
                modality = Modality.ABSTRACT
                this.returnType = returnType.type.toIrType(environment)
            }.apply {
                dispatchReceiverParameter = thisReceiver
                if (sourceType != null) {
                    addValueParameter(sourceType, nameAllocator)
                }
                annotations = buildList {
                    add(environment.renderEngine.createAnnotation(bindingAnnotation))
                    addAll(mode.asAnnotations())
                    addAll(returnType.qualifiers)
                }
            }
        }
    }

    override fun build(): KirClass {
        val typeSpec = when {
            bindings.isNotEmpty() -> buildClass(info.moduleName.simpleName) {
                visibility = DescriptorVisibilities.PUBLIC
                modality = Modality.ABSTRACT
                thisReceiver = toReceiver()
                addPrimaryConstructor(DescriptorVisibilities.PRIVATE)
                for (binding in bindings) {
                    binding()
                }
                if (providers.isNotEmpty()) {
                    addChild(buildClass("Companion") {
                        isCompanion = true
                        visibility = DescriptorVisibilities.PUBLIC
                        kind = ClassKind.OBJECT
                        addPrimaryConstructor(DescriptorVisibilities.PRIVATE)
                        for (provider in providers) {
                            provider()
                        }
                    })
                }
            }

            else -> buildClass(info.moduleName.simpleName) {
                visibility = DescriptorVisibilities.PUBLIC
                kind = ClassKind.OBJECT
                addPrimaryConstructor(DescriptorVisibilities.PRIVATE)
                for (provider in providers) {
                    provider()
                }
            }
        }
        typeSpec.annotations += buildList {
            add(environment.renderEngine.createAnnotation<AutoDaggerGenerated>())
            add(
                when (val installation = info.installation) {
                    is HiltModuleBuilder.Installation.InstallIn ->
                        environment.renderEngine.createAnnotation<InstallIn> {
                            addArgument("value", installation.components)
                        }

                    is HiltModuleBuilder.Installation.TestInstallIn ->
                        environment.renderEngine.createAnnotation(
                            KirClassName(
                                "dagger.hilt.testing",
                                "TestInstallIn"
                            )
                        ) {
                            addArgument("components", installation.components)
                            addArgument("replaces", installation.replaces)
                        }
                }
            )
            add(environment.renderEngine.createAnnotation<OriginatingElement> {
                addArgument("topLevelClass", info.originatingTopLevelClassName)
            })
        }
        typeSpec.parent = (info.originatingElement as IrDeclaration).file
        return typeSpec
    }

    private fun buildClass(name: String, builder: IrClass.() -> Unit): IrClass {
        return environment.irFactory.buildClass {
            this.name = Name.identifier(name)
        }.apply {
            thisReceiver = toReceiver()
            builder()
        }
    }

    private fun IrClass.addPrimaryConstructor(
        visibility: DescriptorVisibility,
        body: IrBlockBodyBuilder.() -> Unit = {}
    ) {
        addConstructor(visibility, primary = true, body)
    }

    private fun IrClass.addConstructor(
        visibility: DescriptorVisibility,
        primary: Boolean,
        body: IrBlockBodyBuilder.() -> Unit = {}
    ) {
        addConstructor {
            isPrimary = primary
            this.visibility = visibility
        }.apply {
            thisReceiver = toReceiver()
            this.body = environment.renderEngine.buildIrDeclaration(symbol) {
                irBlockBody(body = body)
            }
        }
    }

    private fun IrClass.toReceiver() = buildValueParameter(this) {
        name = Name.special("<this>")
        type = IrSimpleTypeImpl(
            classifier = symbol,
            hasQuestionMark = false,
            arguments = emptyList(),
            annotations = emptyList(),
        )
        origin = IrDeclarationOrigin.INSTANCE_RECEIVER
    }

    private fun ProviderMode<KirAnnotationSpec>.asAnnotations() =
        asAnnotations { environment.renderEngine.createAnnotation(it) }

    private fun IrFunction.addValueParameter(
        parameter: Parameter<KirTypeName, KirAnnotationSpec>,
        nameAllocator: NameAllocator,
    ) {
        addValueParameter {
            name = Name.identifier(nameAllocator.newName(parameter.asParameterName()))
            type = parameter.asTypeName().toIrType(environment)
        }.apply {
            annotations = parameter.qualifiers.toList()
        }
    }

    companion object {
        fun Factory(environment: KirEnvironment) = HiltModuleBuilder.Factory { HiltIrModuleBuilder(environment, it) }
    }
}


private fun Parameter<KirTypeName, KirAnnotationSpec>.asParameterName() = asParameterName { simpleName }

private fun Parameter<KirTypeName, KirAnnotationSpec>.asTypeName(): KirTypeName =
    asTypeName { rawType, arguments -> rawType.toKirClassName()
        .parameterizedBy(arguments.map { KirSimpleTypeArgument(it) }) }