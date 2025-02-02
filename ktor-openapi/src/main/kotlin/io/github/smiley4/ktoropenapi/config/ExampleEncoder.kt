package io.github.smiley4.ktoropenapi.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Encoder to produce the final example value.
 * Return the unmodified example to fall back to the default encoder.
 */
typealias GenericExampleEncoder = (type: TypeDescriptor?, example: Any?) -> Any?


object ExampleEncoder {

    /**
     * Default [GenericExampleEncoder] using internal swagger serializer to encode example object.
     */
    val internal: GenericExampleEncoder = { _, example ->
        example
    }

    /**
     * [GenericExampleEncoder] using kotlinx-serialization to encode example objects.
     */
    val kotlinx: GenericExampleEncoder = { type, example ->
        if (type is KTypeDescriptor) {
            val jsonString = Json.encodeToString(serializer(type.type), example)
            val jsonObj = jacksonObjectMapper().readValue(jsonString, object : TypeReference<Any>() {})
            jsonObj
        } else {
            example
        }
    }

}

