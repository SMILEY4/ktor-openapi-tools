package io.github.smiley4.ktoropenapi.config.descriptors

import io.swagger.v3.oas.models.examples.Example

/**
 * Identifier and description of an example
 */
sealed class ExampleDescriptor(
    val name: String,
)


/**
 * Describes an example as an object.
 */
class ValueExampleDescriptor(
    name: String,
    val value: Any?,
    val summary: String? = null,
    val description: String? = null,
    val type: TypeDescriptor? = null,
) : ExampleDescriptor(name)


/**
 * Describes a reference to a shared example placed in the components section
 * @param name the name of the example in the operation
 * @param refName the name/id of the example to reference in the components section
 */
class RefExampleDescriptor(name: String, val refName: String) : ExampleDescriptor(name)


/**
 * Describes an example as a swagger [Example]-object
 */
class SwaggerExampleDescriptor(name: String, val example: Example) : ExampleDescriptor(name)
