package se.ansman.dagger.auto.compiler.renderers

object NoOpRenderer : Renderer<Any, Nothing> {
    override fun render(input: Any): Nothing {
        throw UnsupportedOperationException("This renderer cannot be used")
    }
}