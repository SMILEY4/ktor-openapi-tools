package io.github.smiley4.ktorswaggerui.config

/**
 * Determines the order to sort the operations in the swagger-ui.
 */
enum class OperationsSort(val value: String) {
    /**
     * The order returned by the server unchanged
     */
    NONE("undefined"),
    /**
     * sort by paths alphanumerically
     */
    ALPHANUMERICALLY("alpha"),
    /**
     * sort by HTTP method
     */
    HTTP_METHOD("method")
}
