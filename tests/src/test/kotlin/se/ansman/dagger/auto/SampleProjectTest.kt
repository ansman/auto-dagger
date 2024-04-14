package se.ansman.dagger.auto

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameInstanceAs
import assertk.assertions.isTrue
import assertk.assertions.prop
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.ansman.dagger.auto.ktorfit.ExampleKtorfitApiService
import se.ansman.dagger.auto.retrofit.ExampleRetrofitApiService
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class SampleProjectTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initializableProvider: Provider<AutoDaggerInitializer>

    @Inject
    lateinit var repository: Provider<Repository>

    @Suppress("unused")
    @Inject
    lateinit var retrofitService: Provider<ExampleRetrofitApiService>

    @Suppress("unused")
    @Inject
    lateinit var ktorfitService: Provider<ExampleKtorfitApiService>

    @Inject
    lateinit var replaceableRepository: Provider<ReplaceableRepository>

    @Inject
    lateinit var closeable: Provider<Closeable>

    @Inject
    lateinit var closeables: Provider<Set<@JvmSuppressWildcards Closeable>>

    @Inject
    lateinit var bindingKeys: Provider<Map<String, @JvmSuppressWildcards Closeable>>

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun teardown() {
        Repository.createCount = 0
        InitializableRepository.createCount = 0
        InitializableRepository.initCount = 0
        QualifiedThing.createCount = 0
        RealReplaceableRepository.isInitialized = false
    }

    @Test
    fun `initializing should set isInitialized`() {
        val initializer = initializableProvider.get()
        assertThat(initializer).prop(AutoDaggerInitializer::isInitialized).isFalse()
        initializer.initialize()
        assertThat(initializer).prop(AutoDaggerInitializer::isInitialized).isTrue()
    }

    @Test
    fun `the repository is only initialized once`() {
        assertThat(Repository.Companion::createCount).isEqualTo(0)

        val initializer = initializableProvider.get()
        assertThat(Repository.Companion::createCount).isEqualTo(0)

        initializer.initialize()
        assertThat(Repository.Companion::createCount).isEqualTo(1)

        initializer.initialize()
        assertThat(Repository.Companion::createCount).isEqualTo(1)

        initializableProvider.get().initialize()
        assertThat(Repository.Companion::createCount).isEqualTo(1)
    }

    @Test
    fun `the initializable repository is only initialized once`() {
        assertAll {
            assertThat(InitializableRepository.Companion::createCount).isEqualTo(0)
            assertThat(InitializableRepository.Companion::initCount).isEqualTo(0)
        }

        val initializer = initializableProvider.get()
        assertAll {
            assertThat(InitializableRepository.Companion::createCount).isEqualTo(1)
            assertThat(InitializableRepository.Companion::initCount).isEqualTo(0)
        }

        initializer.initialize()
        assertAll {
            assertThat(InitializableRepository.Companion::createCount).isEqualTo(1)
            assertThat(InitializableRepository.Companion::initCount).isEqualTo(1)
        }

        initializer.initialize()
        assertAll {
            assertThat(InitializableRepository.Companion::createCount).isEqualTo(1)
            assertThat(InitializableRepository.Companion::initCount).isEqualTo(1)
        }

        initializableProvider.get().initialize()
        assertAll {
            assertThat(InitializableRepository.Companion::createCount).isEqualTo(1)
            assertThat(InitializableRepository.Companion::initCount).isEqualTo(2)
        }
    }

    @Test
    fun `qualified things are initialized too`() {
        assertThat(QualifiedThing.Companion::createCount).isEqualTo(0)

        val initializer = initializableProvider.get()
        assertThat(QualifiedThing.Companion::createCount).isEqualTo(0)

        initializer.initialize()
        assertThat(QualifiedThing.Companion::createCount).isEqualTo(1)
    }

    @Test
    fun `auto bind works`() {
        assertAll {
            assertThat(closeable.get()).isSameInstanceAs(repository.get())
            assertThat(closeables.get()).isEqualTo(setOf(repository.get()))
            assertThat(bindingKeys.get()).isEqualTo(mapOf("test" to repository.get()))
        }
    }

    @Test
    fun `replacement rebinds object`() {
        assertThat(replaceableRepository.get()).isInstanceOf(FakeReplaceableRepository::class)
    }

    @Test
    fun `replacement removes auto initialize`() {
        initializableProvider.get().initialize()
        assertThat(RealReplaceableRepository::isInitialized).isFalse()
    }
}