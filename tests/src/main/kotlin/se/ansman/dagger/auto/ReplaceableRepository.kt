package se.ansman.dagger.auto

import javax.inject.Inject
import javax.inject.Singleton

interface ReplaceableRepository

@AutoBind
@AutoInitialize
@Singleton
class RealReplaceableRepository @Inject constructor() : ReplaceableRepository {
    init {
        isInitialized = true
    }

    companion object {
        var isInitialized: Boolean = false
    }
}