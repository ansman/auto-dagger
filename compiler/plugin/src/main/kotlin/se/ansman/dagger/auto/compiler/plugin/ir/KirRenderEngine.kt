package se.ansman.dagger.auto.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationNode
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName.Companion.toClassId
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName.Companion.toKirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirParameterizedTypeName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirStar
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName
import kotlin.reflect.KClass

class KirRenderEngine(
    private val context: IrPluginContext,
    val typeLookup: TypeLookup<ClassId, IrClassSymbol>
) : RenderEngine<IrElement, KirTypeName, KirClassName, KirAnnotationSpec> {
    override fun className(packageName: String, simpleName: String): KirClassName =
        KirClassName(packageName, simpleName)

    override fun className(qualifiedName: String): KirClassName = KirClassName.bestGuess(qualifiedName)
    override fun className(type: KClass<*>): KirClassName = type.java.toKirClassName()

    override fun qualifiedName(className: KirClassName): String = className.toString()
    override fun simpleName(typeName: KirTypeName): String = typeName.simpleName
    override fun simpleNames(className: KirClassName): List<String> = className.simpleNames

    override fun packageName(className: KirClassName): String = className.packageName

    override fun topLevelClassName(className: KirClassName): KirClassName = className.topLevelClassName()

    override fun rawType(typeName: KirTypeName): KirClassName =
        when (typeName) {
            is KirClassName -> typeName
            is KirParameterizedTypeName -> typeName.rawType
        }

    override fun asWildcard(typeName: KirTypeName): KirTypeName = when (typeName) {
        is KirClassName -> typeName
        is KirParameterizedTypeName -> KirParameterizedTypeName(
            typeName.rawType,
            typeName.arguments.map { KirStar }
        )
    }

    override fun AnnotationNode<IrElement, KirTypeName, KirClassName>.toAnnotationSpec(): KirAnnotationSpec =
        (this as KirAnnotationNode).node.deepCopyWithSymbols()


    fun <R> buildIrDeclaration(
        symbol: IrSymbol,
        builder: DeclarationIrBuilder.() -> R
    ): R = DeclarationIrBuilder(context, symbol, symbol.owner.startOffset, symbol.owner.endOffset)
        .run(builder)

    fun createAnnotation(classId: ClassId, arguments: AnnotationArgumentBuilder.() -> Unit = {}): IrConstructorCall {
        val annotation = typeLookup[classId]
        val constructor = annotation.constructors.single()
        return IrConstructorCallImpl.fromSymbolOwner(
            type = annotation.defaultType,
            constructorSymbol = constructor,
        ).apply {
            AnnotationArgumentBuilderImpl(constructor.owner, this).apply(arguments)
        }
    }

    private inner class AnnotationArgumentBuilderImpl(
        private val constructor: IrConstructor,
        private val call: IrConstructorCall
    ) : AnnotationArgumentBuilder {

        override fun addArgument(name: String, value: String) {
            addArgument(name, value.toIrConst(context.irBuiltIns.stringType))
        }

        override fun addArgument(name: String, value: KirClassName) {
            val valueParameter = constructor.valueParameters.first { it.name.asString() == name }
            call.putArgument(valueParameter, value.toClassReference())
        }

        override fun addArgument(name: String, value: Collection<KirClassName>) {
            val valueParameter = constructor.valueParameters.first { it.name.asString() == name }
            call.putArgument(
                valueParameter,
                IrVarargImpl(
                    startOffset = SYNTHETIC_OFFSET,
                    endOffset = SYNTHETIC_OFFSET,
                    type = valueParameter.type,
                    elements = value.map { it.toClassReference() },
                    varargElementType = context.irBuiltIns.arrayClass.defaultType
                )
            )
        }

        private fun KirClassName.toClassReference(): IrClassReference {
            val irClass = typeLookup[classId]
            val irType = irClass.defaultType
            return IrClassReferenceImpl(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                type = context.irBuiltIns.kClassClass.typeWith(irType),
                symbol = irClass,
                classType = irType
            )
        }

        private fun addArgument(name: String, value: IrExpression) {
            val valueParameter = constructor.valueParameters.first { it.name.asString() == name }

            call.putArgument(valueParameter, value)
        }
    }
}

interface AnnotationArgumentBuilder {
    fun addArgument(name: String, value: String)
    fun addArgument(name: String, value: KirClassName)
    fun addArgument(name: String, value: Collection<KirClassName>)
}

inline fun <reified A : Annotation> KirRenderEngine.createAnnotation(noinline arguments: AnnotationArgumentBuilder.() -> Unit = {}): IrConstructorCall =
    createAnnotation(A::class, arguments)

fun KirRenderEngine.createAnnotation(
    annotation: KClass<out Annotation>,
    arguments: AnnotationArgumentBuilder.() -> Unit = {}
): IrConstructorCall =
    createAnnotation(annotation.toClassId(), arguments)

fun KirRenderEngine.createAnnotation(
    annotation: KirClassName,
    arguments: AnnotationArgumentBuilder.() -> Unit = {}
): IrConstructorCall =
    createAnnotation(annotation.classId, arguments)