package se.ansman.dagger.auto.compiler.common

fun StringBuilder.deleteSuffix(suffix: CharSequence) {
    if (endsWith(suffix)) {
        delete(length - suffix.length, length)
    }
}