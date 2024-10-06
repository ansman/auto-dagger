package se.ansman.dagger.auto.compiler.common.processing.model

interface ExecutableDeclaration<N, TypeName, ClassName : TypeName> : Declaration<N, TypeName, ClassName> {
    val receiver: Type<N, TypeName, ClassName>?
    val returnType: Type<N, TypeName, ClassName>
    val valueParameters: List<ValueParameter<N, TypeName, ClassName>>
}