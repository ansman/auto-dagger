package se.ansman.deager.tests

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
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
import se.ansman.deager.EagerInitializer
import javax.inject.Inject
import javax.inject.Provider


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class SampleProjectTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var eagerInitializerProvider: Provider<EagerInitializer>

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
    }

    @Test
    fun `the repository is only initialized once`() {
        assertThat(Repository::createCount).isEqualTo(0)

        val eagerInitializer = eagerInitializerProvider.get()
        assertThat(Repository::createCount).isEqualTo(0)

        eagerInitializer.initialize()
        assertThat(Repository::createCount).isEqualTo(1)

        eagerInitializer.initialize()
        assertThat(Repository::createCount).isEqualTo(1)

        eagerInitializerProvider.get().initialize()
        assertThat(Repository::createCount).isEqualTo(1)
    }

    @Test
    fun `the initializable repository is only initialized once`() {
        assertAll {
            assertThat(InitializableRepository::createCount).isEqualTo(0)
            assertThat(InitializableRepository::initCount).isEqualTo(0)
        }

        val eagerInitializer = eagerInitializerProvider.get()
        assertAll {
            assertThat(InitializableRepository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::initCount).isEqualTo(0)
        }

        eagerInitializer.initialize()
        assertAll {
            assertThat(InitializableRepository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::initCount).isEqualTo(1)
        }

        eagerInitializer.initialize()
        assertAll {
            assertThat(InitializableRepository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::initCount).isEqualTo(1)
        }

        eagerInitializerProvider.get().initialize()
        assertAll {
            assertThat(InitializableRepository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::initCount).isEqualTo(2)
        }
    }

    @Test
    fun `qualified things are initialized too`() {
        assertThat(QualifiedThing::createCount).isEqualTo(0)

        val eagerInitializer = eagerInitializerProvider.get()
        assertThat(QualifiedThing::createCount).isEqualTo(0)

        eagerInitializer.initialize()
        assertThat(QualifiedThing::createCount).isEqualTo(1)
    }
}