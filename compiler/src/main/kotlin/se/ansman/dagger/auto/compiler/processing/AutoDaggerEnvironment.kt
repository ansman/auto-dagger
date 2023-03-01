package se.ansman.dagger.auto.compiler.processing

interface AutoDaggerEnvironment<in N, TypeName, ClassName : TypeName, AnnotationSpec, F> {
    val renderEngine: RenderEngine<TypeName, ClassName, AnnotationSpec>
    val logger: AutoDaggerLogger<N>
    fun write(file: F)
}