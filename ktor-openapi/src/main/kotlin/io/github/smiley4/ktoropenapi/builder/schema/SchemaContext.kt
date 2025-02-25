package io.github.smiley4.ktoropenapi.builder.schema

import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.swagger.v3.oas.models.media.Schema

/**
 * Provides schemas for an openapi-spec
 */
internal interface SchemaContext {

    /**
     * Get a [Schema] (or a ref to a schema) by its [TypeDescriptor]
     */
    fun getSchema(typeDescriptor: TypeDescriptor): Schema<*>

    /**
     * Get all schemas placed in the components-section of the spec.
     */
    fun getComponentSection(): Map<String, Schema<*>>
}
