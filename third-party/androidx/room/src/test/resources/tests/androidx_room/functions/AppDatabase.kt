package tests.androidx_room.functions

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos

@Dao
interface UserDao

@Database(entities = [], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): UserDao
}