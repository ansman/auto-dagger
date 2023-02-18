package se.ansman.dagger.auto

import assertk.assertThat
import assertk.assertions.isTrue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.BeforeTest


@HiltAndroidTest
class AutoDaggerStartupInitializerTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BeforeTest
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun singletonComponents() {
        assertThat(TestInitializable.Companion::isInitialized).isTrue()
    }
}