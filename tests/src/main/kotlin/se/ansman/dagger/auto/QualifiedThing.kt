package se.ansman.dagger.auto

class QualifiedThing {
    init {
        ++createCount
    }

    companion object {
        var createCount = 0
    }
}