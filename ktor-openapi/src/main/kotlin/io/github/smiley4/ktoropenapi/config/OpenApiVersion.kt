package io.github.smiley4.ktoropenapi.config

/**
 * The OpenAPI specification version to use when generating the spec.
 */
enum class OpenApiVersion {
    /**
     * OpenAPI 3.0.x - broadly supported by most tools (Postman, AWS API Gateway, many SDKs).
     * Nullable types are represented as `type + nullable: true` instead of a type array.
     */
    V3_0,

    /**
     * OpenAPI 3.1.0 - latest specification, fully aligned with JSON Schema draft 2020-12.
     */
    V3_1
}
