package se.ansman.dagger.auto

fun StringBuilder.deleteSuffix(suffix: CharSequence) {
    if (endsWith(suffix)) {
        delete(length - suffix.length, length)
    }
}