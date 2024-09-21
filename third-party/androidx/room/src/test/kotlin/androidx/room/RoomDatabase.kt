package androidx.room

abstract class RoomDatabase

suspend fun <R> RoomDatabase.withTransaction(block: suspend () -> R): R = block()