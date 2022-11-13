package se.ansman.deager.tests

class QualifiedThing {
    init {
        ++createCount
    }

    companion object {
        var createCount = 0
    }
}