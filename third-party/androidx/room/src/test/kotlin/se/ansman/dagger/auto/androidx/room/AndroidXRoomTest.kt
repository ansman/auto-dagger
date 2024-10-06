package se.ansman.dagger.auto.androidx.room

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.compiler.AutoDaggerCompilationFactoryProvider
import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.Compilation
import java.io.File

class AndroidXRoomTest {
    @TempDir
    lateinit var tempDirectory: File

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must be annotated with @Database`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.androidx.room

                @androidx.room.Dao
                interface UserDao

                @AutoProvideDaos
                abstract class AppDatabase : androidx.room.RoomDatabase() {
                    abstract val users: UserDao
                }
                """
            )
            .assertFailedWithMessage(Errors.AndroidX.Room.notADatabase)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `type must implement RoomDatabase`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.androidx.room

                @androidx.room.Dao
                interface UserDao

                @androidx.room.Database(entities = [], version = 1)
                @AutoProvideDaos
                abstract class AppDatabase {
                    abstract val users: UserDao
                }
                """
            )
            .assertFailedWithMessage(Errors.AndroidX.Room.notADatabase)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `types must directly extend RoomDatabase`(compilationFactory: Compilation.Factory) {
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.androidx.room

                @androidx.room.Dao
                interface UserDao

                abstract class BaseDatabase : androidx.room.RoomDatabase()

                @androidx.room.Database(entities = [], version = 1)
                @AutoProvideDaos
                abstract class AppDatabase : BaseDatabase() {
                    abstract val users: UserDao
                }
                """
            )
            .assertFailedWithMessage(Errors.AndroidX.Room.typeMustDirectlyExtendRoomDatabase)
    }

    @ParameterizedTest
    @ArgumentsSource(AutoDaggerCompilationFactoryProvider::class)
    fun `non dao functions`(compilationFactory: Compilation.Factory) {
        // See https://github.com/ansman/auto-dagger/issues/224
        compilationFactory.create(tempDirectory)
            .compile(
                """
                package se.ansman.dagger.auto.androidx.room

                import androidx.room.withTransaction

                @androidx.room.Dao
                interface UserDao

                @androidx.room.Database(entities = [], version = 1)
                @AutoProvideDaos
                abstract class AppDatabase : androidx.room.RoomDatabase() {
                    abstract val users: UserDao
                    
                    suspend fun <R> withTransaction(block: suspend () -> R): R = 
                        (this as androidx.room.RoomDatabase).withTransaction(block)
                }
                """
            )
            .assertIsSuccessful()
    }
}