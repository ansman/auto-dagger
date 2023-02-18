package se.ansman.dagger.auto.kapt

import javax.lang.model.element.Element

class KaptProcessingError(
    override val message: String,
    val element: Element,
) : Exception()