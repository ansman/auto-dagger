package se.ansman.dagger.auto.ksp

import com.google.devtools.ksp.symbol.KSNode

class KspProcessingError(
    override val message: String,
    val symbol: KSNode,
) : Exception()