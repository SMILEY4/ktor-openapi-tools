package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.GenericExampleEncoder

internal class ExampleConfigData(
    val sharedExamples: Map<String, ExampleDescriptor>,
    val securityExamples: SimpleBodyData?,
    val exampleEncoder: GenericExampleEncoder
) {

    companion object {
        val DEFAULT = ExampleConfigData(
            sharedExamples = emptyMap(),
            securityExamples = null,
            exampleEncoder = ExampleEncoder.internal()
        )
    }

}
