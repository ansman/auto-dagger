package se.ansman.dagger.auto.renderers

interface Renderer<in I, out F> {
    fun render(input: I): F
}