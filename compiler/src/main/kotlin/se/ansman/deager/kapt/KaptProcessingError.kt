package se.ansman.deager.kapt

import javax.lang.model.element.Element

class KaptProcessingError(
    override val message: String,
    val element: Element,
) : Exception()