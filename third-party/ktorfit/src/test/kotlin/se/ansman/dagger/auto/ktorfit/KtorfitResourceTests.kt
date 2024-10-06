package se.ansman.dagger.auto.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.configureKsp
import de.jensklingenberg.ktorfit.KtorfitProcessorProvider
import se.ansman.dagger.auto.compiler.Compilation
import se.ansman.dagger.auto.compiler.KaptCompilation
import se.ansman.dagger.auto.compiler.ResourceBasedTests

class KtorfitResourceTests : ResourceBasedTests() {
    override val factories: Sequence<Compilation.Factory>
        get() = super.factories.filter { it != KaptCompilation.Factory }

    override fun KotlinCompilation.configure() {
        configureKsp(useKsp2 = true) {
            symbolProcessorProviders.add(KtorfitProcessorProvider())
        }
    }
}