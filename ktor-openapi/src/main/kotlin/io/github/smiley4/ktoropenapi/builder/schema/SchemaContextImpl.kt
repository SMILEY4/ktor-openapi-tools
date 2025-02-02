package io.github.smiley4.ktoropenapi.builder.schema

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.config.AnyOfTypeDescriptor
import io.github.smiley4.ktoropenapi.config.ArrayTypeDescriptor
import io.github.smiley4.ktoropenapi.config.EmptyTypeDescriptor
import io.github.smiley4.ktoropenapi.config.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.RefTypeDescriptor
import io.github.smiley4.ktoropenapi.config.SerialTypeDescriptor
import io.github.smiley4.ktoropenapi.config.SwaggerTypeDescriptor
import io.github.smiley4.ktoropenapi.config.TypeDescriptor
import io.github.smiley4.ktoropenapi.data.MultipartBodyData
import io.github.smiley4.ktoropenapi.data.SchemaConfigData
import io.github.smiley4.ktoropenapi.data.SimpleBodyData
import io.github.smiley4.schemakenerator.core.data.KTypeInput
import io.github.smiley4.schemakenerator.core.data.TypeData
import io.github.smiley4.schemakenerator.serialization.data.SerialDescriptorInput
import io.github.smiley4.schemakenerator.swagger.SwaggerSchemaUtils
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.reflect.KType

internal class SchemaContextImpl(private val schemaConfig: SchemaConfigData) : SchemaContext {

    private val rootSchemas = mutableMapOf<TypeDescriptor, Schema<*>>()
    private val componentSchemas = mutableMapOf<String, Schema<*>>()

    fun addGlobal(config: SchemaConfigData) {
        config.securitySchemas.forEach { typeDescriptor ->
            val schema = generateSchema(typeDescriptor)
            rootSchemas[typeDescriptor] = schema.swagger
            schema.componentSchemas.forEach { (k, v) ->
                componentSchemas[k] = v
            }
        }
        config.schemas.forEach { (schemaId, typeDescriptor) ->
            val schema = generateSchema(typeDescriptor)
            componentSchemas[schemaId] = schema.swagger
            schema.componentSchemas.forEach { (k, v) ->
                componentSchemas[k] = v
            }
        }
    }

    fun add(routes: Collection<RouteMeta>) {
        collectTypeDescriptor(routes).forEach { typeDescriptor ->
            val schema = generateSchema(typeDescriptor)
            rootSchemas[typeDescriptor] = schema.swagger
            schema.componentSchemas.forEach { (k, v) ->
                componentSchemas[k] = v
            }
        }
    }

    @Suppress("LongMethod")
    private fun generateSchema(typeDescriptor: TypeDescriptor): CompiledSwaggerSchema {
        return when (typeDescriptor) {
            is KTypeDescriptor -> {
                if (schemaConfig.overwrite.containsKey(typeDescriptor.type)) {
                    generateSchema(schemaConfig.overwrite[typeDescriptor.type]!!)
                } else {
                    generateSchema(typeDescriptor.type)
                }
            }
            is SerialTypeDescriptor -> {
                // todo: support schemaConfig.overwrite
                generateSchema(typeDescriptor.descriptor)
            }
            is SwaggerTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = TypeData.createWildcard(),
                    swagger = typeDescriptor.schema,
                    componentSchemas = emptyMap()
                )
            }
            is ArrayTypeDescriptor -> {
                val itemSchema = generateSchema(typeDescriptor.type)
                CompiledSwaggerSchema(
                    typeData = TypeData.createWildcard(),
                    swagger = SwaggerSchemaUtils().arraySchema(
                        itemSchema.swagger
                    ),
                    componentSchemas = itemSchema.componentSchemas
                )
            }
            is AnyOfTypeDescriptor -> {
                val optionSchemas = typeDescriptor.types.map { generateSchema(it) }
                CompiledSwaggerSchema(
                    typeData = TypeData.createWildcard(),
                    swagger = SwaggerSchemaUtils().subtypesSchema(
                        optionSchemas.map { it.swagger },
                        null,
                        emptyMap()
                    ),
                    componentSchemas = buildMap {
                        optionSchemas.forEach { optionSchema ->
                            this.putAll(optionSchema.componentSchemas)
                        }
                    }
                )
            }
            is EmptyTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = TypeData.createWildcard(),
                    swagger = SwaggerSchemaUtils().anyObjectSchema(),
                    componentSchemas = emptyMap()
                )
            }
            is RefTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = TypeData.createWildcard(),
                    swagger = SwaggerSchemaUtils().referenceSchema(typeDescriptor.schemaId, true),
                    componentSchemas = emptyMap()
                )
            }
        }
    }

    private fun generateSchema(type: KType): CompiledSwaggerSchema {
        return schemaConfig.generator(KTypeInput(type))
    }

    private fun generateSchema(descriptor: SerialDescriptor): CompiledSwaggerSchema {
        return schemaConfig.generator(SerialDescriptorInput(descriptor))
    }

    private fun collectTypeDescriptor(routes: Collection<RouteMeta>): List<TypeDescriptor> {
        val descriptors = mutableListOf<TypeDescriptor>()
        routes
            .filter { !it.documentation.hidden }
            .forEach { route ->
                route.documentation.request.also { request ->
                    request.parameters.forEach { parameter ->
                        descriptors.add(parameter.type)
                    }
                    request.body?.also { body ->
                        when (body) {
                            is SimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is MultipartBodyData -> {
                                body.parts.forEach { part ->
                                    descriptors.add(part.type)
                                }
                            }
                        }
                    }
                }
                route.documentation.responses.forEach { response ->
                    response.headers.forEach { (_, header) ->
                        header.type?.also { descriptors.add(it) }
                    }
                    response.body?.also { body ->
                        when (body) {
                            is SimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is MultipartBodyData -> {
                                body.parts.forEach { part ->
                                    descriptors.add(part.type)
                                }
                            }
                        }
                    }
                }
            }
        return descriptors
    }

    override fun getSchema(typeDescriptor: TypeDescriptor): Schema<*> {
        return rootSchemas[typeDescriptor] ?: throw NoSuchElementException("no root-schema for given type-descriptor")
    }

    override fun getComponentSection(): Map<String, Schema<*>> {
        return componentSchemas
    }

}
