package se.ansman.dagger.auto

import assertk.assertThat
import assertk.assertions.isEqualTo
import dagger.internal.DoubleCheck
import org.junit.jupiter.api.Test
import se.ansman.dagger.auto.Initializable.Companion.asInitializable
import javax.inject.Provider

class InitializableTest {
    private val provider = object : Provider<Unit> {
        var callCount = 0
            private set

        override fun get() {
            ++callCount
        }
    }
    private val lazy = DoubleCheck.lazy(provider)

    @Test
    fun `asInitializable returns an initializable that calls get`() {
        assertThat(provider::callCount).isEqualTo(0)

        val initializable = lazy.asInitializable()
        assertThat(provider::callCount).isEqualTo(0)

        initializable.initialize()
        assertThat(provider::callCount).isEqualTo(1)

        initializable.initialize()
        assertThat(provider::callCount).isEqualTo(1)
    }

    @Test
    fun `asInitializable respects the priority`() {
        assertThat(lazy.asInitializable().priority).isEqualTo(AutoInitialize.defaultPriority)
        assertThat(lazy.asInitializable(4711).priority).isEqualTo(4711)
    }
}