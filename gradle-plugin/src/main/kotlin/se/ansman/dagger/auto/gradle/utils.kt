package se.ansman.dagger.auto.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.provider.Provider
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream

internal fun Project.execWithOutput(configure: ExecSpec.() -> Unit): String {
    val output = ByteArrayOutputStream()
    project
        .exec {
            configure()
            standardOutput = output
        }
        .assertNormalExitValue()
        .rethrowFailure()
    return output.toByteArray().toString(Charsets.UTF_8)
}

internal fun <T> Project.cachedProvider(producer: () -> T): Provider<T> =
    provider(lazy(producer)::value)

internal fun ExtraPropertiesExtension.getOrPut(name: String, block: () -> String): String =
    if (has(name)) get(name) as String else block().also { set(name, it) }

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "TYPE_MISMATCH")
fun <T : Any, R : Any> Provider<T>.mapNullable(mapper: (T) -> R?): Provider<R> = map(mapper)