package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Information about a request (query, path or header) parameter.
 */
data class OpenApiRequestParameterData(
    val name: String,
    val type: TypeDescriptor,
    val format: String?,
    val location: ParameterLocation,
    val description: String?,
    val example: ExampleDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val allowEmptyValue: Boolean?,
    val explode: Boolean,
    val allowReserved: Boolean?,
    val style: Parameter.StyleEnum?,
)
