package se.ansman.dagger.auto

import android.content.pm.ProviderInfo
import androidx.startup.InitializationProvider
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest


@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class AutoDaggerStartupInitializerTest {

    @After
    fun teardown() {
        Repository.createCount = 0
        InitializableRepository.createCount = 0
        InitializableRepository.initCount = 0
        QualifiedThing.createCount = 0
    }

    @Test
    fun singletonComponents() {
        assertAll {
            assertThat(Repository::createCount).isEqualTo(0)
            assertThat(InitializableRepository::createCount).isEqualTo(0)
            assertThat(InitializableRepository::initCount).isEqualTo(0)
            assertThat(QualifiedThing::createCount).isEqualTo(0)
        }
        Robolectric.buildContentProvider(InitializationProvider::class.java)
            .create(ProviderInfo().apply { authority = "se.ansman.dagger.auto.androidx-startup" })
        assertAll {
            assertThat(Repository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::createCount).isEqualTo(1)
            assertThat(InitializableRepository::initCount).isEqualTo(1)
            assertThat(QualifiedThing::createCount).isEqualTo(1)
        }
    }
}