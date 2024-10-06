package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.types.isAny
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.isInterface
import se.ansman.dagger.auto.compiler.common.processing.model.ClassDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment

data class KirClassDeclaration(
    override val node: IrClass,
    override val environment: KirEnvironment,
) : KirDeclaration(),
    ClassDeclaration<IrElement, KirTypeName, KirClassName> {
    override val kind: ClassDeclaration.Kind
        get() = when (node.kind) {
            ClassKind.CLASS -> ClassDeclaration.Kind.Class
            ClassKind.INTERFACE -> ClassDeclaration.Kind.Interface
            ClassKind.ENUM_CLASS -> ClassDeclaration.Kind.EnumClass
            ClassKind.ENUM_ENTRY -> ClassDeclaration.Kind.EnumEntry
            ClassKind.ANNOTATION_CLASS -> ClassDeclaration.Kind.AnnotationClass
            ClassKind.OBJECT -> if (node.isCompanion) {
                ClassDeclaration.Kind.CompanionObject
            } else {
                ClassDeclaration.Kind.Object
            }
        }

    override val enclosingDeclaration: KirClassDeclaration?
        get() = super.enclosingDeclaration as KirClassDeclaration?

    override val className: KirClassName by lazy(LazyThreadSafetyMode.NONE) {
        KirClassName(node.classId!!)
    }

    override val superTypes: List<KirType> by lazy(LazyThreadSafetyMode.NONE) {
        node.superTypes
            .filterNot { it.isAny() }
            .map { it.toKirType() }
    }

    override val declarations: List<KirDeclaration> by lazy(LazyThreadSafetyMode.NONE) {
        node.declarations
            .filter { it.origin == IrDeclarationOrigin.DEFINED }
            .mapNotNull { it.toKirDeclaration() }
    }

    override val isAbstract: Boolean
        get() = when (node.modality) {
            Modality.FINAL,
            Modality.OPEN -> false

            Modality.SEALED,
            Modality.ABSTRACT -> true
        }

    override val isSealedClass: Boolean
        get() = node.modality == Modality.SEALED

    override val superclass: KirType? by lazy(LazyThreadSafetyMode.NONE) {
        superTypes.find { !it.type.isInterface() }
    }

    override fun asType(): KirType = node.symbol.starProjectedType.toKirType()

    override fun isType(qualifiedName: String): Boolean =
        node.classId?.asFqNameString()?.replace('/', '.')?.equals(qualifiedName) ?: false

    override fun isType(className: KirClassName): Boolean = node.classId == className.classId


}