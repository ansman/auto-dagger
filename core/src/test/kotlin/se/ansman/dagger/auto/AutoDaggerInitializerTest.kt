package se.ansman.dagger.auto

import assertk.assertAll
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.each
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameInstanceAs
import assertk.assertions.isZero
import assertk.assertions.prop
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.test.assertFailsWith
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class AutoDaggerInitializerTest {
    private var nextInitOrder = 1
    private val i1 = TestInitializable()
    private val i2 = TestInitializable()
    private val i3 = TestInitializable()
    private val i4 = TestInitializable()
    private val i5 = TestInitializable()
    private val i6 = TestInitializable()
    private val initializables = listOf(i1, i2, i3, i4, i5, i6)
    private val initializable = AutoDaggerInitializer(
        setOf(
            i1,
            OrderedInitializable(priority = 2, i2),
            OrderedInitializable(priority = 3, i3),
            OrderedInitializable(priority = 0, i4),
            OrderedInitializable(priority = -1, i5),
            OrderedInitializable(priority = 1, i6),
        )
    )

    @Test
    fun `calling initialize should initialize all objects`() {
        assertThat(initializables).each { it.prop(TestInitializable::initializeCount).isZero() }
        initializable.initialize()
        assertThat(initializables).each { it.prop(TestInitializable::initializeCount).isEqualTo(1) }
    }

    @Test
    fun `calling initialize multiple times only initialized the objects once`() {
        assertThat(initializables).each { it.prop(TestInitializable::initializeCount).isZero() }

        initializable.initialize()
        assertThat(initializables).each { it.prop(TestInitializable::initializeCount).isEqualTo(1) }

        initializable.initialize()
        assertThat(initializables).each { it.prop(TestInitializable::initializeCount).isEqualTo(1) }
    }

    @Test
    fun `calling initialize initializes the objects in order`() {
        assertThat(initializables).each { it.prop(TestInitializable::initOrder).isEqualTo(-1) }

        initializable.initialize()
        assertAll {
            assertThat(i3::initOrder).isEqualTo(1)
            assertThat(i2::initOrder).isEqualTo(2)
            assertThat(i1::initOrder).isEqualTo(3)
            assertThat(i6::initOrder).isEqualTo(4)
            assertThat(i4::initOrder).isEqualTo(5)
            assertThat(i5::initOrder).isEqualTo(6)
        }
    }

    @Test
    fun `exceptions are rethrown`() {
        val e2 = IllegalStateException()
        val e4 = IllegalArgumentException()
        val i1 = TestInitializable()
        val i2 = TestInitializable { throw e2 }
        val i3 = TestInitializable()
        val i4 = TestInitializable { throw e4 }
        val i5 = TestInitializable()
        assertAll {
            assertThat(i1::initializeCount).isZero()
            assertThat(i2::initializeCount).isZero()
            assertThat(i3::initializeCount).isZero()
            assertThat(i4::initializeCount).isZero()
            assertThat(i5::initializeCount).isZero()
        }

        val initializable = AutoDaggerInitializer(
            setOf(
                OrderedInitializable(priority = 5, i1),
                OrderedInitializable(priority = 4, i2),
                OrderedInitializable(priority = 3, i3),
                OrderedInitializable(priority = 2, i4),
                OrderedInitializable(priority = 1, i5),
            )
        )
        val e = assertFailsWith(IllegalStateException::class) { initializable.initialize() }
        assertAll {
            assertThat(i1::initializeCount).isEqualTo(1)
            assertThat(i2::initializeCount).isEqualTo(1)
            assertThat(i3::initializeCount).isEqualTo(1)
            assertThat(i4::initializeCount).isEqualTo(1)
            assertThat(i5::initializeCount).isEqualTo(1)
            assertThat(e).isSameInstanceAs(e2)
            assertThat(e2.suppressedExceptions).containsExactly(e4)
        }
    }

    @Test
    fun `re-entrant calls are not allowed`() {
        lateinit var initializer: AutoDaggerInitializer
        val initializable = OrderedInitializable(1) {
            initializer.initialize()
        }
        initializer = AutoDaggerInitializer(setOf(initializable))
        assertFailure(initializer::initialize).isInstanceOf(IllegalStateException::class)
    }

    @Test
    fun `initialize blocks until complete`() {
        class ThreadLocks {
            private val started = CountDownLatch(1)
            private val beginInitialize = CountDownLatch(1)
            private val initializing = CountDownLatch(1)

            fun onStarted() = started.countDown()
            fun awaitStarted() = started.await(10, TimeUnit.SECONDS)
            fun beginInitialize() {
                beginInitialize.countDown()
                initializing.await(10, TimeUnit.SECONDS)
            }

            fun awaitBeginInitialize() = beginInitialize.await(10, TimeUnit.SECONDS)
            fun onInitializing() = initializing.countDown()
        }

        val completeInitialize = CountDownLatch(1)
        val isInitializing = CountDownLatch(1)
        val t1Lock = ThreadLocks()
        val t2Lock = ThreadLocks()

        val initializer = AutoDaggerInitializer(setOf(OrderedInitializable(1) {
            isInitializing.countDown()
            completeInitialize.await()
        }))

        fun ThreadLocks.startThread(): Thread =
            Thread {
                onStarted()
                awaitBeginInitialize()
                onInitializing()
                initializer.initialize()
            }.also { it.start() }

        val t1 = t1Lock.startThread()
        val t2 = t2Lock.startThread()

        t1Lock.awaitStarted()
        t2Lock.awaitStarted()
        t1Lock.beginInitialize()
        isInitializing.await()
        t2Lock.beginInitialize()
        await(5.seconds) {
            when (requireNotNull(t2.state)) {
                Thread.State.NEW,
                Thread.State.RUNNABLE -> false

                Thread.State.TERMINATED -> fail("Thread terminated")
                Thread.State.BLOCKED,
                Thread.State.WAITING,
                Thread.State.TIMED_WAITING -> true
            }
        }
        completeInitialize.countDown()
        t1.join(10.seconds.inWholeMilliseconds)
        t2.join(10.seconds.inWholeMilliseconds)
    }

    private inner class TestInitializable(private val onInitialize: () -> Unit = {}) : Initializable {
        var initializeCount = 0
            private set

        var initOrder = -1

        override fun initialize() {
            ++initializeCount
            initOrder = nextInitOrder++
            onInitialize()
        }
    }
}

private inline fun await(
    timeout: Duration,
    sleepAmount: Duration = 10.milliseconds,
    isDone: () -> Boolean
) {
    val start = System.nanoTime()
    while (System.nanoTime().minus(start).nanoseconds < timeout) {
        if (isDone()) {
            return
        }
        Thread.sleep(sleepAmount.inWholeMilliseconds)
    }
    throw TimeoutException("Wait timed out after $timeout")
}