package se.ansman.dagger.auto.compiler.common.processing.model

interface PropertyDeclaration<N, TypeName, ClassName : TypeName> : ExecutableDeclaration<N, TypeName, ClassName> {
    override val valueParameters: List<ValueParameter<N, TypeName, ClassName>>
        get() = emptyList()
}