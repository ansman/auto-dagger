package se.ansman.dagger.auto.compiler.plugin.ir

import org.jetbrains.kotlin.renderer.KeywordStringsGenerated
import java.util.UUID

class NameAllocator private constructor(
  private val allocatedNames: MutableSet<String>,
  private val tagToName: MutableMap<Any, String>,
) {
  constructor(preallocateKeywords: Boolean = true) : this(
    allocatedNames = if (preallocateKeywords) KeywordStringsGenerated.KEYWORDS.toMutableSet() else mutableSetOf(),
    tagToName = mutableMapOf(),
  )

  fun newName(
    suggestion: String,
    tag: Any = UUID.randomUUID().toString(),
  ): String {
    var result = toJavaIdentifier(suggestion)
    while (!allocatedNames.add(result)) {
      result += "_"
    }

    val replaced = tagToName.put(tag, result)
    if (replaced != null) {
      tagToName[tag] = replaced // Put things back as they were!
      throw IllegalArgumentException("tag $tag cannot be used for both '$replaced' and '$result'")
    }

    return result
  }

  /** Retrieve a name created with [NameAllocator.newName]. */
  operator fun get(tag: Any): String = requireNotNull(tagToName[tag]) { "unknown tag: $tag" }
}

private fun toJavaIdentifier(suggestion: String) = buildString {
  var i = 0
  while (i < suggestion.length) {
    val codePoint = suggestion.codePointAt(i)
    if (i == 0 &&
      !Character.isJavaIdentifierStart(codePoint) &&
      Character.isJavaIdentifierPart(codePoint)
    ) {
      append("_")
    }

    val validCodePoint: Int = if (Character.isJavaIdentifierPart(codePoint)) {
      codePoint
    } else {
      '_'.code
    }
    appendCodePoint(validCodePoint)
    i += Character.charCount(codePoint)
  }
}