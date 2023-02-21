package se.ansman.dagger.auto.ksp.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import se.ansman.dagger.auto.ksp.unwrapTypeAlias

fun KSType.unwrapTypeAlias(): KSType =
    (declaration as? KSTypeAlias)?.type?.resolve()?.unwrapTypeAlias() ?: this