package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.schemakenerator.core.addMissingSupertypeSubtypeRelations
import io.github.smiley4.schemakenerator.core.data.InputType
import io.github.smiley4.schemakenerator.core.handleNameAnnotation
import io.github.smiley4.schemakenerator.reflection.analyseTypeUsingReflection
import io.github.smiley4.schemakenerator.reflection.collectSubTypes
import io.github.smiley4.schemakenerator.serialization.analyzeTypeUsingKotlinxSerialization
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.handleCoreAnnotations
import io.github.smiley4.schemakenerator.swagger.withTitle

/**
 * Function to generate swagger schemas for any given type
 */
typealias GenericSchemaGenerator = (type: InputType) -> CompiledSwaggerSchema

object SchemaGenerator {

    /**
     * A [GenericSchemaGenerator] using reflection to analyze types and generate the schemas
     */
    val reflection: GenericSchemaGenerator = { type ->
        type
            .collectSubTypes()
            .analyseTypeUsingReflection()
            .addMissingSupertypeSubtypeRelations()
            .handleNameAnnotation()
            .generateSwaggerSchema()
            .handleCoreAnnotations()
            .withTitle(TitleType.SIMPLE)
            .compileReferencingRoot()
    }

    /**
     * A [GenericSchemaGenerator] using kotlinx-serialization to analyze types and generate the schemas
     */
    val kotlinx: GenericSchemaGenerator = { type ->
        type
            .analyzeTypeUsingKotlinxSerialization()
            .generateSwaggerSchema()
            .withTitle(TitleType.SIMPLE)
            .compileReferencingRoot()
    }

}