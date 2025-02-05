package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.GenericSchemaGenerator
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.config.TypeDescriptor
import kotlin.reflect.KType

/**
 * Common configuration for schemas.
 */
internal data class SchemaConfigData(
    val schemas: Map<String, TypeDescriptor>,
    val generator: GenericSchemaGenerator,
    val securitySchemas: List<TypeDescriptor>
) {
    companion object {
        val DEFAULT = SchemaConfigData(
            schemas = emptyMap(),
            generator = SchemaGenerator.reflection(),
            securitySchemas = emptyList()
        )
    }
}
