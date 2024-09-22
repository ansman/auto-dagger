package se.ansman.dagger.auto.compiler.kapt

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import se.ansman.dagger.auto.compiler.common.Processor
import javax.lang.model.element.Element

typealias KaptProcessor = Processor<Element, TypeName, ClassName, AnnotationSpec>