# Schema Generation

Schemas are automatically generated from types using a schema generation function configured in the plugin configuration and uses [schema-kenerator](https://github.com/SMILEY4/schema-kenerator) by default.

## Pre-Defined Schema Generators

=== "Reflection"
    The (default) schema generator using reflection to analyze types and produce schemas. The result is closest to the result produced by serializers like [Jackson](https://github.com/FasterXML/jackson).

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection { //(1)!
                //...(2)
            }
        }
    }
    ```
    
    1. Use the pre-defined schema generator using reflection to automatically generate schemas from types.
    2. Configure the schema generator (optional).

    ??? info "More Information"

        [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-generator/reflection.html)

=== "Kotlinx.Serialization"
    Schema generator using [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) to analyze types and produce schemas. 

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx { //(1)!
                //...(2)
            }
        }
    }
    ```
    
    1. Use the pre-defined schema generator using kotlinx.serialization to automatically generate schemas from types.
    2. Configure the schema generator (optional).

    ??? info "More Information"

        [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-generator/kotlinx.html)

## Defining Custom Schemas for Types

Sometimes the schema generator does not produce the exact desired result for some types. See [schema-kenerator](https://github.com/SMILEY4/schema-kenerator) for detailed information on how to modify schemas for types.

When using the pre-build schema generators, "Type Overwrites" can be used to completely replace the schema for a type with another schema.

=== "Reflection"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection {
                overwrite(SchemaGenerator.TypeOverwrites.LocalDateTime()) //(1)!
                overwrite(SchemaGenerator.TypeOverwrites.File()) //(2)!
                //...
            }
        }
    }
    ```
    
    1. Use a pre-defined schema for `java.time.LocalDateTime` instead of the auto-generated one.
    2. Use a pre-defined schema for `java.io.File` instead of the auto-generated one.

=== "Kotlinx.Serialization"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx {
                overwrite(SchemaGenerator.TypeOverwrites.LocalDateTime()) //(1)!
                overwrite(SchemaGenerator.TypeOverwrites.File()) //(2)!
                //...
            }
        }
    }
    ```
    
    1. Use a pre-defined schema for `java.time.LocalDateTime` instead of the auto-generated one.
    2. Use a pre-defined schema for `java.io.File` instead of the auto-generated one.

??? info "More Information"

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-generator/-type-overwrites/index.html) for available type overwrites.

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-overwrite-module/index.html) for interface to implement own type overwrites.




## Custom Schema Generators

The schema generator can be completely replaced by an own implementation or by a different, non pre-built configuration.

```kotlin
schemas {
    generator = { type -> //(1)!
        TODO() //(2)!
    }
}
```

1. Input of the schema generation function is the type either as a (wrapped) `KType` or `SerialDescriptor`.
2. Generate the schema and return the result as a `io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema`

???+ example "Custom schema-kenerator "pipeline""

    Using [schema-kenerator](https://github.com/SMILEY4/schema-kenerator) to build a custom schema generation "pipeline".

    ```kotlin
    schemas {
        generator = { type ->
            type
                .processReflection()
                .generateSwaggerSchema()
                .withTitle(TitleType.SIMPLE)
                .compileReferencingRoot()
        }
    }
    ```

??? info "More Information"
    More information can be found in the [wiki of the schema-kenerator project](https://github.com/SMILEY4/schema-kenerator/wiki) together with an overview of the additional required dependencies.
