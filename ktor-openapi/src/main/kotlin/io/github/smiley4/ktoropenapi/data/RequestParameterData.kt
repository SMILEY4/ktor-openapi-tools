package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.github.smiley4.ktoropenapi.config.ParameterLocation
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Information about a request (query, path or header) parameter.
 */
internal data class RequestParameterData(
    val name: String,
    val type: TypeDescriptor,
    val location: ParameterLocation,
    val description: String?,
    val example: ExampleDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val allowEmptyValue: Boolean?,
    val explode: Boolean,
    val allowReserved: Boolean?,
    val style: Parameter.StyleEnum?,
    val hidden: Boolean,
)
