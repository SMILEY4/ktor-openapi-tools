package io.github.smiley4.ktoropenapi.builder.example

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.swagger.v3.oas.models.examples.Example

/**
 * Provides examples for an openapi-spec
 */
internal interface ExampleContext {

    /**
     * Get an [Example] (or a ref to an example) by its [ExampleDescriptor].
     */
    fun getExample(descriptor: ExampleDescriptor): Example


    /**
     * Get all examples placed in the components-section of the spec.
     */
    fun getComponentSection(): Map<String, Example>
}
