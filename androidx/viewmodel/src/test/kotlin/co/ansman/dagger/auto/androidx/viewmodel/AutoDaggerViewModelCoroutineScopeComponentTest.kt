package co.ansman.dagger.auto.androidx.viewmodel

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.prop
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.lifecycle.RetainedLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import org.junit.Test

class AutoDaggerViewModelCoroutineScopeComponentTest {
    @Test
    fun `clearing the lifecycle cancels the scope`() {
        val lifecycle = ViewModelLifecycleImpl()
        val scope = AutoDaggerViewModelCoroutineScopeComponent.provideViewModelScope(lifecycle)
        assertThat(scope).prop(CoroutineScope::isActive).isTrue()
        lifecycle.clear()
        assertThat(scope).prop(CoroutineScope::isActive).isFalse()
    }
}

private class ViewModelLifecycleImpl : ViewModelLifecycle {
    private val listeners = mutableSetOf<RetainedLifecycle.OnClearedListener>()

    override fun addOnClearedListener(listener: RetainedLifecycle.OnClearedListener) {
        listeners += listener
    }

    override fun removeOnClearedListener(listener: RetainedLifecycle.OnClearedListener) {
        listeners -= listener
    }

    fun clear() {
        listeners.forEach(RetainedLifecycle.OnClearedListener::onCleared)
    }
}