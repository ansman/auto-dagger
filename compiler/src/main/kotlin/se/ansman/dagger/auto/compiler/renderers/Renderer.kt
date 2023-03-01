package se.ansman.dagger.auto.compiler.renderers

interface Renderer<in I, out F> {
    fun render(input: I): F
}