package tests.androidx_room.duplicate_accessors

import androidx.room.Database
import androidx.room.RoomDatabase
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos

interface UserDao

@Database(entities = [], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract fun users1(): UserDao
    abstract fun users2(): UserDao
    abstract val users3: UserDao
}