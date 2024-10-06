package se.ansman.dagger.auto.compiler.common.processing.model

interface ValueParameter<N, TypeName, ClassName : TypeName> : Node<N, TypeName, ClassName> {
    val name: String
    val type: Type<N, TypeName, ClassName>
}