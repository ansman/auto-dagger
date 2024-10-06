package se.ansman.dagger.auto.compiler.common.processing.model

interface Node<N, TypeName, ClassName : TypeName> {
    val node: N
}