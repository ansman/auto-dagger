/**
 * Precompiled [dokka-common.gradle.kts][Dokka_common_gradle] script plugin.
 *
 * @see Dokka_common_gradle
 */
public
class DokkaCommonPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Dokka_common_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
