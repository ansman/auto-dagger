package se.ansman.dagger.auto.compiler

import java.io.File
import java.util.Locale

fun formatFile(name: String, contents: String, includeLineNumbers: Boolean = true) =
    buildString { formatFileTo(this, name, contents, includeLineNumbers) }

fun formatFileTo(
    appendable: Appendable,
    name: String,
    contents: String,
    includeLineNumbers: Boolean = true
) {
    appendable.append(name).append(":\n")
    if (includeLineNumbers) {
        var lineNumber = 1
        contents.lineSequence().joinTo(appendable, separator = "\n") {
            "%4d: %s".format(Locale.ROOT, lineNumber++, it)
        }
    } else {
        appendable.append(contents)
    }
}