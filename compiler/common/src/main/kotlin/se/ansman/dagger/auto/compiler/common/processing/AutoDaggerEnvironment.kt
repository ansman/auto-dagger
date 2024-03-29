package se.ansman.dagger.auto.compiler.common.processing

interface AutoDaggerEnvironment<in N, TypeName, ClassName : TypeName, AnnotationSpec, F> : RenderEngine<TypeName, ClassName, AnnotationSpec> {
    val logger: AutoDaggerLogger<N>
    fun write(file: F)
}