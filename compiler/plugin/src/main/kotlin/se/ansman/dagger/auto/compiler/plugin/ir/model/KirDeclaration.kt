package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithVisibility
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import se.ansman.dagger.auto.compiler.common.processing.model.AnnotationNode
import se.ansman.dagger.auto.compiler.common.processing.model.Declaration

sealed class KirDeclaration : KirNode(), Declaration<IrElement, KirTypeName, KirClassName> {
    abstract override val node: IrDeclarationWithName

    override val name: String get() = node.name.asString()
    override val annotations: List<AnnotationNode<IrElement, KirTypeName, KirClassName>> by lazy(LazyThreadSafetyMode.NONE) {
        node.annotations.map { KirAnnotationNode(it, environment) }
    }

    override val enclosingDeclaration: Declaration<IrElement, KirTypeName, KirClassName>? by lazy(LazyThreadSafetyMode.NONE) {
        node.parent.toKirDeclaration()
    }

    override val isPublic: Boolean
        get() = (node as IrDeclarationWithVisibility).visibility == DescriptorVisibilities.PUBLIC

    override val isPrivate: Boolean
        get() = (node as IrDeclarationWithVisibility).visibility == DescriptorVisibilities.PRIVATE ||
                (node as IrDeclarationWithVisibility).visibility == DescriptorVisibilities.PRIVATE_TO_THIS

    override val isGeneric: Boolean
        get() = (node as? IrTypeParametersContainer)?.typeParameters?.isNotEmpty() == true
}