package se.ansman.dagger.auto.compiler.ksp.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias

fun KSType.unwrapTypeAlias(): KSType =
    (declaration as? KSTypeAlias)?.type?.resolve()?.unwrapTypeAlias() ?: this