package se.ansman.deager.ksp

import com.google.devtools.ksp.symbol.KSNode

class KspProcessingError(
    override val message: String,
    val symbol: KSNode,
) : Exception()