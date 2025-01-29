package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.*
import io.github.smiley4.schemakenerator.core.data.WildcardTypeData
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.steps.SwaggerSchemaUtils
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType

class SchemaContextImpl(private val schemaConfig: SchemaConfigData) : SchemaContext {

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

    private fun generateSchema(typeDescriptor: TypeDescriptor): CompiledSwaggerSchema {
        return when (typeDescriptor) {
            is KTypeDescriptor -> {
                if (schemaConfig.overwrite.containsKey(typeDescriptor.type)) {
                    generateSchema(schemaConfig.overwrite[typeDescriptor.type]!!)
                } else {
                    generateSchema(typeDescriptor.type)
                }
            }
            is SwaggerTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = typeDescriptor.schema,
                    componentSchemas = emptyMap()
                )
            }
            is ArrayTypeDescriptor -> {
                val itemSchema = generateSchema(typeDescriptor.type)
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().arraySchema(
                        itemSchema.swagger
                    ),
                    componentSchemas = itemSchema.componentSchemas
                )
            }
            is AnyOfTypeDescriptor -> {
                val optionSchemas = typeDescriptor.types.map { generateSchema(it) }
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
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
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().anyObjectSchema(),
                    componentSchemas = emptyMap()
                )
            }
            is RefTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().referenceSchema(typeDescriptor.schemaId, true),
                    componentSchemas = emptyMap()
                )
            }
        }
    }

    private fun generateSchema(type: KType): CompiledSwaggerSchema {
        return schemaConfig.generator(type)
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
                            is OpenApiSimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is OpenApiMultipartBodyData -> {
                                body.parts.forEach { part ->
                                    descriptors.add(part.type)
                                    part.headers.forEach { (_, header) ->
                                        header.type?.also { descriptors.add(it) }
                                    }
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
                            is OpenApiSimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is OpenApiMultipartBodyData -> {
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
