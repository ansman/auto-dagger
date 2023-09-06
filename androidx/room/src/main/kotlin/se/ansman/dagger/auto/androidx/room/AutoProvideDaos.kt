package se.ansman.dagger.auto.androidx.room

import dagger.hilt.GeneratesRootInput
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

/**
 * A marker for [Room Databases](https://developer.android.com/training/data-storage/room) that will automatically
 * contribute all DAOs in your database to the dependency graph.
 *
 * Simply add this annotation to a Database and auto-data will provide all DAOs.
 *
 * Example:
 * ```
 * @AutoProvideDaos
 * @Database(entities = [User::class], version = 1)
 * abstract class AppDatabase : RoomDatabase() {
 *     abstract fun userDao(): UserDao
 * }
 *
 * // You'll also need to provide the Database instance to the component you want to inject the service into:
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object DatabaseModule {
 *   @Provides
 *   @Singleton
 *   fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
 *     Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()
 * }
 * ```
 *
 * ## Changing the target component
 * By default, the DAOs are provided in the [SingletonComponent]. If you'd like to change this you can do so by
 * specifying the `inComponent` parameter:
 * ```
 * @AutoProvideDaos
 * @Database(entities = [User::class], version = 1)
 * abstract class AppDatabase : RoomDatabase() {
 *   abstract fun userDao(): UserDao
 * }
 * ```
 *
 * @param inComponent The component to provide the DAOs in. Defaults to [SingletonComponent].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
public annotation class AutoProvideDaos(
    val inComponent: KClass<*> = SingletonComponent::class,
)