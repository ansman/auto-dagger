package se.ansman.dagger.auto.compiler

import se.ansman.dagger.auto.compiler.common.testutils.KaptAutoDaggerCompilation
import se.ansman.dagger.auto.compiler.kapt.AutoDaggerAnnotationProcessor

class KaptTest : BaseTest(KaptAutoDaggerCompilation.Factory(::AutoDaggerAnnotationProcessor))