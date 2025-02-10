package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor

/**
 * See [OpenAPI Specification - Header Object](https://swagger.io/specification/#header-object).
 */
internal data class HeaderData(
    val description: String?,
    val type: TypeDescriptor?,
    val required: Boolean,
    val deprecated: Boolean,
    val explode: Boolean?,
)
