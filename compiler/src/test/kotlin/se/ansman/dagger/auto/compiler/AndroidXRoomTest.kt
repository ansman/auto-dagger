package se.ansman.dagger.auto.compiler

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import se.ansman.dagger.auto.compiler.common.testutils.Compilation
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
                package co.ansman.dagger.auto.androidx.room

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
                package co.ansman.dagger.auto.androidx.room

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
                package co.ansman.dagger.auto.androidx.room

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
}