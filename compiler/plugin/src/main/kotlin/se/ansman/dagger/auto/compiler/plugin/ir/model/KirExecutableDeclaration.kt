package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.IrElement
import se.ansman.dagger.auto.compiler.common.processing.model.ExecutableDeclaration

sealed class KirExecutableDeclaration : KirDeclaration(), ExecutableDeclaration<IrElement, KirTypeName, KirClassName>