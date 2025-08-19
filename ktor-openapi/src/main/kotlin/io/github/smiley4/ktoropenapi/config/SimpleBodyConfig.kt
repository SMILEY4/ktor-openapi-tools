package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.RefExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.SwaggerExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.ValueExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.github.smiley4.ktoropenapi.data.SimpleBodyData
import io.swagger.v3.oas.models.examples.Example
import kotlin.reflect.typeOf


/**
 * Describes the base of a single request/response body.
 */
@OpenApiDslMarker
class SimpleBodyConfig internal constructor(
    /**
     * The type defining the schema used for the body.
     */
    val type: TypeDescriptor,
) : BaseBodyConfig() {

    /**
     * Examples for this body
     */
    private val examples = mutableListOf<ExampleDescriptor>()

    /**
     * Add the given example as an example to this body
     */
    fun example(example: ExampleDescriptor) {
        examples.add(example)
    }

    /**
     * Add the given example as an example to this body
     */
    fun example(name: String, example: Example) = example(SwaggerExampleDescriptor(name, example))

    /**
     * Add the given example as an example to this body
     */
    inline fun <reified T> example(name: String, example: ValueExampleDescriptorConfig<T>.() -> Unit) = example(
        ValueExampleDescriptorConfig<T>()
            .apply(example)
            .let { result ->
                ValueExampleDescriptor(
                    name = name,
                    value = result.value,
                    summary = result.summary,
                    description = result.description,
                    type = KTypeDescriptor(typeOf<T>())
                )
            }
    )


    /**
     * Add the given example as an example to this body
     * @param name the name of the example to display at this body
     * @param refName the name of the referenced example
     */
    fun exampleRef(name: String, refName: String) = example(RefExampleDescriptor(name, refName))

    /**
     * Add the given example as an example to this body
     * @param name the name of the example
     */
    fun exampleRef(name: String) = example(RefExampleDescriptor(name, name))

    override fun build() = SimpleBodyData(
        description = description,
        required = required ?: false,
        mediaTypes = mediaTypes.toSet(),
        type = type,
        examples = examples,
    )

}
