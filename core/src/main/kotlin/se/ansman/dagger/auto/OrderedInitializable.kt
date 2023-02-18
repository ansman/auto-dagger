package se.ansman.dagger.auto

internal class OrderedInitializable(
    val priority: Int,
    initializable: Initializable,
) : Initializable by initializable

internal inline fun OrderedInitializable(priority: Int, crossinline initialize: () -> Unit): OrderedInitializable =
    OrderedInitializable(
        priority = priority,
        initializable = object : Initializable {
            override fun initialize() {
                initialize()
            }
        }
    )