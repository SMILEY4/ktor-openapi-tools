# Manual Schema Definition

Schemas can be defined manually using OpenAPI schema objects, providing complete control over the schema structure. This approach is useful when automatic schema generation doesn't produce the desired result.

Manual schemas use the io.swagger.v3.oas.models.media.Schema class from the OpenAPI specification library. This class provides all properties defined in the OpenAPI specification, allowing schemas to be built exactly as they should appear in the final specification.

```kotlin
import io.swagger.v3.oas.models.media.Schema

post("data", {
    request {
        body(Schema<Any>().apply {
            type = "object"
            title = "CustomData"
            properties = mapOf(
                "id" to Schema<Any>().apply {
                    type = "string"
                    format = "uuid"
                },
                "value" to Schema<Any>().apply {
                    type = "number"
                    minimum = BigDecimal.ZERO
                    maximum = BigDecimal(100)
                }
            )
            required = listOf("id", "value")
        })
    }
}) { }
```

This creates a schema with two properties: a UUID-formatted string id and a numeric value constrained between 0 and 100.

## Composite Schemas

Composite schemas combine multiple schemas using logical operators. This can also include combining global schemas defined in the plugin configuration.

```kotlin
install(OpenApi) {
    schemas {
        schema<User>("user")
    }
}

get("users", {
    description = "Retrieves all users"
    response {
        code(HttpStatusCode.OK) {
            body(
                array(
                    anyOf(
                        ref("type-schema"),
                        type<Admin>()
                    )
                )
            )
        }
    }
}) { }
```

### anyOf

The anyOf operator indicates that a value can match any one of the provided schemas:

```kotlin
import io.github.smiley4.ktoropenapi.config.anyOf

body(anyOf(
    Schema<Any>().apply {
        type = "string"
        format = "email"
    },
    Schema<Any>().apply {
        type = "string"
        format = "uri"
    }
)) { }
```

This accepts either an email address or a URI. The value must be valid according to at least one of the schemas.

### array

Composite schemas can be used as array items:

```kotlin
import io.github.smiley4.ktoropenapi.config.array

body(
    array(
        anyOf(
            Schema<Any>().apply {
                type = "string"
            },
            Schema<Any>().apply {
                type = "number"
            }
        )
    )
) { }
```

### type

This can be used to use schemas automatically generated from a given Kotlin type with other operations:

```kotlin
import io.github.smiley4.ktoropenapi.config.type

body(
    array(
        type<User>(),
    )
) { }
```


### ref

This can be used to use globally defined schemas with other operations:

```kotlin
import io.github.smiley4.ktoropenapi.config.ref

body(
    array(
        ref("product"),
    )
) { }
```

### empty

The empty function is a shortcut to create an empty schema: 

```kotlin
import io.github.smiley4.ktoropenapi.config.empty

body(
    anyOf(
        type<SuccessResponse>(),
        empty()
    )
) { }
```