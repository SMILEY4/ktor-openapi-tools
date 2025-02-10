package io.github.smiley4.ktoropenapi.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
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
    fun internal(): GenericExampleEncoder = { _, example ->
        example
    }

    /**
     * [GenericExampleEncoder] using kotlinx-serialization to encode example objects.
     * @param json the kotlinx json serializer to use for encoding objects to json. Set `null` to use default kotlinx json serializer.
     */
    fun kotlinx(json: Json? = null): GenericExampleEncoder = { type, example ->
        when(type) {
            is KTypeDescriptor -> {
                val jsonEncoder = json ?: Json
                val jsonString = jsonEncoder.encodeToString(serializer(type.type), example)
                val jsonObj = jacksonObjectMapper().readValue(jsonString, object : TypeReference<Any>() {})
                jsonObj
            }
            else -> example
        }
    }

}

