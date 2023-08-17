package se.ansman.dagger.auto.compiler.common.rendering

object NoOpRenderer : Renderer<Any, Nothing> {
    override fun render(input: Any): Nothing {
        throw UnsupportedOperationException("This renderer cannot be used")
    }
}