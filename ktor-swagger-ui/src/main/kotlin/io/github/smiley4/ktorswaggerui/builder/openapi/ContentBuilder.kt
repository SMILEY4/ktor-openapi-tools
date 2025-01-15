package io.github.smiley4.ktorswaggerui.builder.openapi

import io.github.smiley4.ktorswaggerui.builder.example.ExampleContext
import io.github.smiley4.ktorswaggerui.builder.schema.SchemaContext
import io.github.smiley4.ktorswaggerui.data.OpenApiBaseBodyData
import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartBodyData
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
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
class ContentBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
    private val headerBuilder: HeaderBuilder
) {

    fun build(body: OpenApiBaseBodyData): Content =
        when (body) {
            is OpenApiSimpleBodyData -> buildSimpleBody(body)
            is OpenApiMultipartBodyData -> buildMultipartBody(body)
        }

    private fun buildSimpleBody(body: OpenApiSimpleBodyData): Content =
        Content().also { content ->
            buildSimpleMediaTypes(body, schemaContext.getSchema(body.type)).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }

    private fun buildMultipartBody(body: OpenApiMultipartBodyData): Content {
        return Content().also { content ->
            buildMultipartMediaTypes(body).forEach { (contentType, mediaType) ->
                content.addMediaType(contentType.toString(), mediaType)
            }
        }
    }

    private fun buildSimpleMediaTypes(body: OpenApiSimpleBodyData, schema: Schema<*>?): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { schema?.let { setOf(chooseMediaType(schema)) } ?: setOf() }
        return mediaTypes.associateWith { buildSimpleMediaType(schema, body) }
    }

    private fun buildSimpleMediaType(schema: Schema<*>?, body: OpenApiSimpleBodyData): MediaType {
        return MediaType().also {
            it.schema = schema
            body.examples.forEach { descriptor ->
                it.addExamples(descriptor.name, exampleContext.getExample(descriptor))
            }
        }
    }

    private fun buildMultipartMediaTypes(body: OpenApiMultipartBodyData): Map<ContentType, MediaType> {
        val mediaTypes = body.mediaTypes.ifEmpty { setOf(ContentType.MultiPart.FormData) }
        return mediaTypes.associateWith { buildMultipartMediaType(body) }
    }

    private fun buildMultipartMediaType(body: OpenApiMultipartBodyData): MediaType {
        return MediaType().also { mediaType ->
            mediaType.schema = Schema<Any>().also { schema ->
                schema.types = setOf("object")
                schema.properties = mutableMapOf<String?, Schema<*>?>().also { props ->
                    body.parts.forEach { part ->
                        props[part.name] = schemaContext.getSchema(part.type)
                    }
                }
            }
            mediaType.encoding = buildMultipartEncoding(body)
        }
    }

    private fun buildMultipartEncoding(body: OpenApiMultipartBodyData): MutableMap<String, Encoding>? {
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
