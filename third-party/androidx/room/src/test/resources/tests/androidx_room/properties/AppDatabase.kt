package tests.androidx_room.properties

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import se.ansman.dagger.auto.androidx.room.AutoProvideDaos

@Dao
interface UserDao

@Dao
interface BookDao

@Database(entities = [], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract val users: UserDao
    abstract fun books(): BookDao
}