package io.github.smiley4.ktoropenapi.data

internal object DataUtils {

    /**
     * Merges the two boolean values.
     * @return true if "value" is true, value of "base" otherwise
     */
    fun mergeBoolean(base: Boolean, value: Boolean) = if (value) true else base

    /**
     * Merges the two values.
     * @return "value" if "value" is different from the given default value, "base" otherwise
     */
    fun <T> mergeDefault(base: T, value: T, default: T) = if (value != default) value else base

    /**
     * Merges the two values.
     * @return "value" if "value" is not null, "base" otherwise
     */
    fun <T> merge(base: T?, value: T?) = value ?: base

}
