package se.ansman.dagger.auto.compiler.common.processing.model

interface FunctionDeclaration<N, TypeName, ClassName : TypeName> : ExecutableDeclaration<N, TypeName, ClassName> {
    val isConstructor: Boolean
}