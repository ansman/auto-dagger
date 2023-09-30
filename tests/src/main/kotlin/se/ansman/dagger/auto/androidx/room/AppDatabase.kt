package se.ansman.dagger.auto.androidx.room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity("users")
data class User(@PrimaryKey val id: String, val name: String)

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<User>
}

@Database(entities = [User::class], version = 1, exportSchema = false)
@AutoProvideDaos
abstract class AppDatabase : RoomDatabase() {
    abstract val users: UserDao
}