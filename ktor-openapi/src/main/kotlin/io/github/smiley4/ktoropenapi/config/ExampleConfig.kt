package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.SwaggerExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.ValueExampleDescriptor
import io.github.smiley4.ktoropenapi.data.ExampleConfigData
import io.github.smiley4.ktoropenapi.data.MultipartBodyData
import io.github.smiley4.ktoropenapi.data.SecurityData
import io.github.smiley4.ktoropenapi.data.SimpleBodyData
import io.swagger.v3.oas.models.examples.Example


/**
 * Configuration for examples
 */
@OpenApiDslMarker
class ExampleConfig internal constructor() {

    /**
     * The list of global / shared examples.
     */
    private val sharedExamples = mutableMapOf<String, ExampleDescriptor>()


    /**
     * Add a shared example that can be referenced by all routes.
     * The name of the example has to be unique among all shared examples and acts as its id.
     * @param example the example data.
     */
    fun example(example: ExampleDescriptor) {
        sharedExamples[example.name] = example
    }


    /**
     * Add a shared example that can be referenced by all routes by the given name.
     * The provided name has to be unique among all shared examples and acts as its id.
     */
    fun example(name: String, example: Example) = example(SwaggerExampleDescriptor(name, example))


    /**
     * Add a shared example that can be referenced by all routes by the given name.
     * The provided name has to be unique among all shared examples and acts as its id.
     */
    fun example(name: String, example: ValueExampleDescriptorConfig.() -> Unit) = example(
        ValueExampleDescriptorConfig()
            .apply(example)
            .let { result ->
                ValueExampleDescriptor(
                    name = name,
                    value = result.value,
                    summary = result.summary,
                    description = result.description
                )
            }
    )


    /**
     * The [GenericExampleEncoder] responsible for encoding all example values.
     */
    var exampleEncoder: GenericExampleEncoder = ExampleConfigData.DEFAULT.exampleEncoder


    /**
     * Specify a custom encoder for example objects
     */
    fun encoder(exampleEncoder: GenericExampleEncoder) {
        this.exampleEncoder = exampleEncoder
    }


    /**
     * Build the data object for this config.
     * @param securityConfig the data for security config that might contain additional examples
     */
    internal fun build(securityConfig: SecurityData) = ExampleConfigData(
        sharedExamples = sharedExamples,
        securityExamples = securityConfig.defaultUnauthorizedResponse?.body?.let {
            when (it) {
                is SimpleBodyData -> it
                is MultipartBodyData -> null
            }
        },
        exampleEncoder = exampleEncoder
    )
}
