package se.ansman.dagger.auto.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.provider.Provider
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream

internal fun Project.execWithOutput(configure: ExecSpec.() -> Unit): Provider<String> =
    project.providers
        .exec(configure)
        .run {
            result.flatMap {
                it.assertNormalExitValue()
                it.rethrowFailure()
                standardOutput.asText
            }
        }

internal fun ExtraPropertiesExtension.getOrPut(name: String, block: () -> String): String =
    if (has(name)) get(name) as String else block().also { set(name, it) }

@Suppress("TYPE_MISMATCH")
fun <T : Any, R : Any> Provider<T>.mapNullable(mapper: (T) -> R?): Provider<R> = map(mapper)