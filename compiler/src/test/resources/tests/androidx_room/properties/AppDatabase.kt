package tests.androidx_room.properties

import androidx.room.Database
import androidx.room.RoomDatabase
import co.ansman.dagger.auto.androidx.room.AutoProvideDaos

interface UserDao
interface BookDao

@Database(entities = [], version = 1)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract val users: UserDao
    abstract fun books(): BookDao
}