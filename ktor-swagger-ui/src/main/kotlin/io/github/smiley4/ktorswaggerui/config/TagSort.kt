package io.github.smiley4.ktorswaggerui.config

/**
 * Determines the order to sort the tags in the swagger-ui.
 */
enum class TagSort(val value: String) {
    /**
     * The order returned by the server unchanged
     */
    NONE("undefined"),
    /**
     * sort by paths alphanumerically
     */
    ALPHANUMERICALLY("alpha"),
}
