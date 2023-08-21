package tests.androidx_room.functions

import androidx.room.Database
import androidx.room.RoomDatabase
import co.ansman.dagger.auto.androidx.room.AutoProvideDaos

interface UserDao

@Database(entities = [], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): UserDao
}