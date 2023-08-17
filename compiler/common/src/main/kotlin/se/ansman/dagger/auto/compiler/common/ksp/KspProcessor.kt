package se.ansman.dagger.auto.compiler.common.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import se.ansman.dagger.auto.compiler.common.Processor

typealias KspProcessor = Processor<KSDeclaration, TypeName, ClassName, AnnotationSpec>