package se.ansman.deager.tests

import assertk.assertThat
import assertk.assertions.isTrue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.BeforeTest


@HiltAndroidTest
class DeagerInitializerTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BeforeTest
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun singletonComponents() {
        assertThat(TestInitializable::isInitialized).isTrue()
    }
}