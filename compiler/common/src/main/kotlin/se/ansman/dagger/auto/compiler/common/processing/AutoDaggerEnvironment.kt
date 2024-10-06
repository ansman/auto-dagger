package se.ansman.dagger.auto.compiler.common.processing

interface AutoDaggerEnvironment<N, TypeName, ClassName : TypeName, AnnotationSpec, Output> : RenderEngine<N, TypeName, ClassName, AnnotationSpec> {
    val logger: AutoDaggerLogger<N>
    fun write(output: Output)
}