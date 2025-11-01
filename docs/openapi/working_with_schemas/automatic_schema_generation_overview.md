# Automatic Schema Generation

The plugin uses the schema-kenerator library to automatically generate schemas from Kotlin types. This library provides a flexible, multi-step pipeline for analyzing types and producing OpenAPI-compliant schemas.



## How schema-kenerator Works

Schema generation with schema-kenerator operates as a configurable pipeline:

1. *Type Analysis:* Examines the Kotlin type structure (properties, generics, nullability)
2. *Schema Generation:* Converts type information into schema data structures
3. *Schema Compilation:* Produces final OpenAPI schemas with references

Each step in the pipeline is highly configurable, allowing fine-grained control over schema generation behavior.



## Schema Generation Configuration

The schema generation pipeline can be configured in two ways:

### Simplified Wrapper

The plugin provides two pre-built generators that wrap the schema-kenerator pipeline with sensible defaults:

- [`SchemaGenerator.reflection()`]() - Uses Kotlin reflection for type analysis
- [`SchemaGenerator.kotlinx()`]() - Uses kotlinx.serialization descriptors for type analysis

Both generators handle the pipeline automatically but differ in how they extract type information from Kotlin classes.

```kotlin
install(OpenApi) {
    schemas {
        // Pre-configured generator with simplified options
        generator = SchemaGenerator.kotlinx {
            nullables = RequiredHandling.NON_REQUIRED
            optionals = RequiredHandling.REQUIRED
            title = TitleType.SIMPLE
            explicitNullTypes = false
        }
    }
}
```

### Custom Pipeline

The pipeline can be configured manually for complete control:

```kotlin
install(OpenApi) {
    schemas {
        // Completely custom pipeline configuration
        generator = { type ->
            type
                .analyzeTypeUsingKotlinxSerialization()
                .generateSwaggerSchema {
                    nullables = RequiredHandling.NON_REQUIRED
                    optionals = RequiredHandling.REQUIRED
                }
                .withTitle(TitleType.SIMPLE)
                .compileReferencingRoot(
                    explicitNullTypes = false
                )
        }
    }
}
```

This approach provides direct access to the schema-kenerator pipeline, allowing customization of each step.

The schema-kenerator library provides extensive documentation on pipeline configuration and complete customization options.



## Reflection-Based Generation

The pre-built reflection-based generator (default) uses Kotlin reflection to analyze types:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection()
    }
}
```
**How It Works**

This generator works with any Kotlin class without requiring special annotations or setup, making it the most flexible option for schema generation. It examines class structures using the Kotlin reflection API, extracting property information from constructors, handling nullability from the type system, and generating schemas that closely match how Jackson and other reflection-based serialization libraries would serialize the types.

**Supported Third-Party Annotations**

It respects annotations from widely-used libraries, allowing schemas to reflect existing serialization configurations without additional work. Jackson annotations like @JsonProperty for renaming fields or @JsonIgnore for excluding properties are automatically recognized. Similarly, validation annotations from javax.validation or jakarta.validation (such as @Min, @Max, @Size, @Pattern) are processed and converted into corresponding OpenAPI schema constraints. Swagger annotations like @Schema can add descriptions, examples, and format information directly to the generated schemas.

**schema-kenerator Annotations**

The generator supports schema-kenerator's own annotations for schema customization. These annotations can rename properties, add descriptions to fields and classes, specify formats, and control schema generation behavior. This provides a consistent way to enhance schema generation regardless of which serialization library is used in the project.

### Configuration

The reflection generator accepts configuration to customize its behavior:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            // more configuration
        }
    }
}
```

Detailed configuration options, including custom schemas for specific types, annotation processing control and naming strategies are covered in [Customizing Schema Generation]().


## Kotlinx.Serialization-Based Generation

The pre-built [kotlinx.serialization]()-based generator uses serialization descriptors from `@Serializable` classes:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx()
    }
}
```
**How It Works**

This generator provides the higher accuracy when using kotlinx.serialization for JSON handling, as it generates schemas based on exactly how kotlinx.serialization would serialize the types. When a type is annotated with `@Serializable`, the generator reads the serialization descriptor that kotlinx.serialization creates at compile time, capturing all serialization-specific behavior including custom serializers, naming strategies, and default values.

**Supported Third-Party Annotations**

It automatically respects kotlinx.serialization annotations like `@SerialName` for property renaming, applies Json configuration settings like naming strategies (snake_case, camelCase, etc.), and handles polymorphic serialization configurations. 

Annotations not supported by kotlinx.serialization are also not supported by the schema generator. This also includes `@Swagger` annotations.

**schema-kenerator Annotations**

Like the reflection generator, kotlinx.serialization also supports schema-kenerator annotations for adding descriptions, customizing property names, and controlling schema generation. These annotations work alongside kotlinx.serialization's own annotations, providing additional documentation capabilities without affecting serialization behavior.


### Configuration

The kotlinx.serialization generator can be configured with a (optional) Json instance to match the application's serialization settings:


```kotlin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

val json = Json {
    prettyPrint = true
    encodeDefaults = true
    namingStrategy = JsonNamingStrategy.SnakeCase
}

install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx(json) {
            // more configuration
        }
    }
}
```

Detailed configuration options, including custom schemas for specific types, annotation processing control and naming strategies are covered in [Customizing Schema Generation]().
