package io.github.smiley4.ktorswaggerui.data

import io.github.smiley4.schemakenerator.core.connectSubTypes
import io.github.smiley4.schemakenerator.core.handleNameAnnotation
import io.github.smiley4.schemakenerator.reflection.collectSubTypes
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.handleCoreAnnotations
import io.github.smiley4.schemakenerator.swagger.withTitle
import kotlin.reflect.KType

/**
 * Common configuration for schemas.
 */
data class SchemaConfigData(
    val schemas: Map<String, TypeDescriptor>,
    val generator: (type: KType) -> CompiledSwaggerSchema,
    val overwrite: Map<KType, TypeDescriptor>,
    val securitySchemas: List<TypeDescriptor>
) {
    companion object {
        val DEFAULT = SchemaConfigData(
            schemas = emptyMap(),
            generator = { type ->
                type
                    .collectSubTypes()
                    .processReflection()
                    .connectSubTypes()
                    .handleNameAnnotation()
                    .generateSwaggerSchema()
                    .handleCoreAnnotations()
                    .withTitle(TitleType.SIMPLE)
                    .compileReferencingRoot()
            },
            overwrite = emptyMap(),
            securitySchemas = emptyList()
        )
    }
}
