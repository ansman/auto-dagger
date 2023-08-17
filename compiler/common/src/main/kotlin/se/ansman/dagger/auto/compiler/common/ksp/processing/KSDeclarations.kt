package se.ansman.dagger.auto.compiler.common.ksp.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import se.ansman.dagger.auto.compiler.common.ksp.unwrapTypeAlias

fun KSType.unwrapTypeAlias(): KSType =
    (declaration as? KSTypeAlias)?.type?.resolve()?.unwrapTypeAlias() ?: this