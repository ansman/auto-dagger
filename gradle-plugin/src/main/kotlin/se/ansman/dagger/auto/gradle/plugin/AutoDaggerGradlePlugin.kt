package se.ansman.dagger.auto.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.InternalSubpluginOption
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.tooling.core.Extras
import org.jetbrains.kotlin.tooling.core.extrasKeyOf
import se.ansman.dagger.auto.AutoDaggerGradlePluginBuildConfig.group
import se.ansman.dagger.auto.AutoDaggerGradlePluginBuildConfig.version

public class AutoDaggerGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create("autoDagger", AutoDaggerExtension::class.java).apply {
            enableLogging.convention(false)
        }
        target.plugins.withType(KotlinBasePlugin::class.java) { kotlin ->
            when (kotlin) {
                is KotlinPluginWrapper,
                is KotlinAndroidPluginWrapper -> {
                    target.dependencies.add("implementation", "$group:api:$version")
                }

                else -> error("Auto Dagger only supports JVM and Android projects")
            }
        }
        target.afterEvaluate {
            check(target.plugins.hasPlugin(KotlinBasePlugin::class.java)) {
                "Auto Dagger must be applied to a project with the Kotlin plugin"
            }
        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
        kotlinCompilation.project.objects.listProperty(SubpluginOption::class.java).apply {
            val extension = kotlinCompilation.project.extensions.getByType(AutoDaggerExtension::class.java)
            add(
                extension.enableLogging.map { enableLogging ->
                    InternalSubpluginOption(
                        key = "enableLogging",
                        value = enableLogging.toString()
                    )
                }
            )
        }

    override fun getCompilerPluginId(): String = "auto-dagger"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = group,
        artifactId = "compiler-plugin",
        version = version
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.project.plugins.hasPlugin(javaClass) &&
                kotlinCompilation.extras[enabled] != false

    public companion object {
        public val enabled: Extras.Key<Boolean> = extrasKeyOf<Boolean>("auto-dagger-enabled")
    }
}