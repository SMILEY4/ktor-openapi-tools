package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.*
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Configuration for schemas
 */
@OpenApiDslMarker
class SchemaConfig {

    /**
     * The json-schema generator for all schemas. See https://github.com/SMILEY4/schema-kenerator/wiki for more information.
     */
    var generator: GenericSchemaGenerator = SchemaConfigData.DEFAULT.generator

    private val schemas = mutableMapOf<String, TypeDescriptor>()

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, descriptor: TypeDescriptor) {
        schemas[schemaId] = descriptor
    }

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, schema: Schema<*>) = schema(schemaId, SwaggerTypeDescriptor(schema))

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    fun schema(schemaId: String, schema: KType) = schema(schemaId, KTypeDescriptor(schema))

    /**
     * Add a shared schema that can be referenced by all routes by the given id.
     */
    inline fun <reified T> schema(schemaId: String) = schema(schemaId, KTypeDescriptor(typeOf<T>()))

    /**
     * Build the data object for this config.
     * @param securityConfig configuration that might contain additional schemas
     */
    internal fun build(securityConfig: SecurityData) = SchemaConfigData(
        generator = generator,
        schemas = schemas,
        securitySchemas = securityConfig.defaultUnauthorizedResponse?.body?.let { body ->
            when (body) {
                is SimpleBodyData -> listOf(body.type)
                is MultipartBodyData -> body.parts.map { it.type }
            }
        } ?: emptyList()
    )

}
