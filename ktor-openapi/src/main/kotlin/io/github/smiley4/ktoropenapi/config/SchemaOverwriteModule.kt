@file:OptIn(ExperimentalSerializationApi::class)

package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.schemakenerator.core.data.TypeData
import io.github.smiley4.schemakenerator.core.data.TypeName
import io.github.smiley4.schemakenerator.core.data.WrappedTypeData
import io.github.smiley4.schemakenerator.reflection.analyzer.MinimalTypeData
import io.github.smiley4.schemakenerator.reflection.analyzer.ReflectionTypeAnalyzerModule
import io.github.smiley4.schemakenerator.serialization.analyzer.SerializationTypeAnalyzerModule
import io.github.smiley4.schemakenerator.serialization.analyzer.fullName
import io.github.smiley4.schemakenerator.swagger.data.SwaggerSchema
import io.github.smiley4.schemakenerator.swagger.generator.SwaggerSchemaGenerationModule
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nonNullOriginal
import kotlin.reflect.KClass
import kotlin.reflect.KType

open class SchemaOverwriteModule(
    val identifier: String,
    val schema: () -> Schema<*>
) : ReflectionTypeAnalyzerModule, SerializationTypeAnalyzerModule, SwaggerSchemaGenerationModule {

    override fun applies(type: KType, clazz: KClass<*>): Boolean {
        return (clazz.qualifiedName ?: clazz.java.name) == identifier
    }

    override fun applies(descriptor: SerialDescriptor): Boolean {
        return descriptor.nonNullOriginal.serialName == identifier
    }

    override fun preAnalyze(context: ReflectionTypeAnalyzerModule.Context): MinimalTypeData {
        return MinimalTypeData(
            identifyingName = context.clazz.toTypeName(),
            descriptiveName = context.clazz.toTypeName(),
            typeParameters = mutableListOf()
        )
    }

    override fun analyze(context: ReflectionTypeAnalyzerModule.Context, minimalTypeData: MinimalTypeData): WrappedTypeData {
        return WrappedTypeData(
            typeData = TypeData(
                id = context.id,
                identifyingName = context.clazz.toTypeName(),
                descriptiveName = context.clazz.toTypeName(),
                typeParameters = mutableListOf(),
                annotations = mutableListOf(),
                subtypes = mutableListOf(),
                supertypes = mutableListOf(),
                members = mutableListOf(),
                isInlineValue = false,
                enumData = null,
                collectionData = null,
                mapData = null,
            ),
            nullable = context.type.isMarkedNullable,
        )
    }

    override fun analyze(context: SerializationTypeAnalyzerModule.Context): WrappedTypeData {
        return WrappedTypeData(
            typeData = TypeData(
                id = context.id,
                identifyingName = context.descriptor.toTypeName(),
                descriptiveName = context.descriptor.toTypeName(),
                typeParameters = mutableListOf(),
                annotations = mutableListOf(),
                subtypes = mutableListOf(),
                supertypes = mutableListOf(),
                members = mutableListOf(),
                isInlineValue = false,
                enumData = null,
                collectionData = null,
                mapData = null
            ),
            nullable = context.nullable || context.descriptor.isNullable,
        )
    }

    override fun applies(typeData: TypeData): Boolean {
        return typeData.identifyingName.full == identifier
    }

    override fun generate(context: SwaggerSchemaGenerationModule.Context): SwaggerSchema {
        return SwaggerSchema(
            typeData = context.typeData,
            swagger = schema()
        )
    }

    private fun KClass<*>.toTypeName() = TypeName(
        full = this.qualifiedName ?: this.java.name,
        short = this.simpleName ?: this.java.name
    )

    private fun SerialDescriptor.toTypeName() = TypeName(
        full = this.fullName(),
        short = this.serialName.split(".").last().replace("?", "")
    )

}
