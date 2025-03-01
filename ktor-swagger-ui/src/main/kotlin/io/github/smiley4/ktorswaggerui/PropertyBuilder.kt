package io.github.smiley4.ktorswaggerui



internal class PropertyBuilder {

    data class Raw(val value: String)

    private val entries = mutableMapOf<String, Any?>()

    operator fun set(key: String, value: Any?) {
        entries[key] = value
    }


    /**
     * @param linePrefix a string to add before each line
     * @param prefixFirst whether to add the prefix to the first line
     * @param separator the separator string to join each line with
     * @param nullBehavior how to handle null values. Valid values are "include", "remove", "to_undefined"
     */
    fun render(linePrefix: String, prefixFirst: Boolean, separator: String, nullBehavior: String): String {
        return entries
            .mapValues { (_, value) ->
                when (value) {
                    null -> when(nullBehavior) {
                        "include" -> "null"
                        "remove" -> null
                        "to_undefined" -> "undefined"
                        else -> throw IllegalArgumentException("Invalid value for null behaviour: '$nullBehavior'")
                    }
                    is Raw -> value.value
                    is Number -> value.toString()
                    is Boolean -> value.toString()
                    is String -> "'$value'"
                    else -> "'$value'"
                }
            }
            .filterValues { it != null }
            .mapValues { (_, value) -> value!! }
            .map { (key, value) -> "$key: $value" }
            .mapIndexed { index, line ->
                if(!prefixFirst && index == 0) {
                    line
                } else {
                    linePrefix +  line
                }
            }
            .joinToString(separator)
    }

}
