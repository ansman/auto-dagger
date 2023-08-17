package se.ansman.dagger.auto.compiler.common.rendering

interface Renderer<in I, out F> {
    fun render(input: I): F
}