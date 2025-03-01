package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.example.ExampleContext
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.github.smiley4.ktoropenapi.data.BaseBodyData
import io.github.smiley4.ktoropenapi.data.MultipartBodyData
import io.github.smiley4.ktoropenapi.data.SimpleBodyData
import io.ktor.http.*
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Encoding
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.set

/**
 * Builds the openapi [Content]-object for request and response bodies.
 * See [OpenAPI Specification - Request Body Object](https://swagger.io/specification/#request-body-object)
 * and [OpenAPI Specification - Response Object](https://swagger.io/specification/#response-object).
 */
internal class ContentBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
    private val headerBuilder: HeaderBuilder
) {

    fun build(body: BaseBodyData): Content =
        when (body) {
            is SimpleBodyData -> buildSimpleBody(body)
            is MultipartBodyData -> buildMultipartBody(body)
        }

    private fun buildSimpleBody(body: SimpleBodyData): Content =
        Content().also { content ->
            buildSimpleMediaTypes(body, schemaContext.getSchema(body.type)).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }

    private fun buildMultipartBody(body: MultipartBodyData): Content {
        return Content().also { content ->
            buildMultipartMediaTypes(body).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }
    }

    private fun buildSimpleMediaTypes(body: SimpleBodyData, schema: Schema<*>?): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { schema?.let { setOf(chooseMediaType(schema)) } ?: setOf() }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>?, body: SimpleBodyData): MediaType {
        return MediaType().also {
            it.schema = schema
            body.examples.forEach { descriptor ->
                it.addExamples(descriptor.name, exampleContext.getExample(descriptor))
            }
        }
    }

    private fun buildMultipartMediaTypes(body: MultipartBodyData): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { setOf(ContentType.MultiPart.FormData) }
        return mediaTypes.associateWith { buildMultipartMediaType(body) }
    }

    private fun buildMultipartMediaType(body: MultipartBodyData): MediaType {
        return MediaType().also { mediaType ->
            mediaType.schema = Schema<Any>().also { schema ->
                schema.type = "object"
                schema.properties = mutableMapOf<String?, Schema<*>?>().also { props ->
                    body.parts.forEach { part ->
                        props[part.name] = schemaContext.getSchema(part.type)
                    }
                }
                schema.required = body.parts.filter { it.required }.map { it.name }
            }
            mediaType.encoding = buildMultipartEncoding(body)
        }
    }

    private fun buildMultipartEncoding(body: MultipartBodyData): MutableMap<String, Encoding>? {
        return if (body.parts.flatMap { it.mediaTypes }.isEmpty()) {
            null
        } else {
            mutableMapOf<String, Encoding>().also { encodings ->
                body.parts
                    .filter { it.mediaTypes.isNotEmpty() || it.headers.isNotEmpty() }
                    .forEach { part ->
                        encodings[part.name] = Encoding().apply {
                            contentType = part.mediaTypes.joinToString(", ") { it.toString() }
                            headers = part.headers.mapValues { headerBuilder.build(it.value) }
                        }
                    }
            }
        }
    }

    private fun chooseMediaType(schema: Schema<*>): ContentType {
        return when (schema.type) {
            "integer" -> ContentType.Text.Plain
            "number" -> ContentType.Text.Plain
            "boolean" -> ContentType.Text.Plain
            "string" -> ContentType.Text.Plain
            "object" -> ContentType.Application.Json
            "array" -> ContentType.Application.Json
            null -> ContentType.Application.Json
            else -> ContentType.Text.Plain
        }
    }

}
