# Schema Introduction

Schemas define the structure of data types used in API requests and responses. Schemas can be automatically generated from Kotlin types,
manually defined in OpenAPI format or declared globally for reuse across multiple routes.




## What Are Schemas?

Schemas define the shape, type, and constraints of data structures in the OpenAPI specification:

```json
{
  "type": "object",
  "properties": {
    "id": { "type": "string" },
    "name": { "type": "string" },
    "age": { "type": "integer" }
  },
  "required": ["id", "name"]
}
```

This schema describes an object with string `id` and `name` properties (both required) and an optional integer `age` property.
The matching kotlin class could look like this:

```kotlin
data class User(
    val id: String,
    val name: String,
    val age: Int?,
)
```




## How Schemas Are Used

Schemas are referenced in route documentation to describe request and response data structures:

```kotlin
get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            body<User>() // (1)!
        }
    }
}) { }
```

1. Schema is automatically generated from User type.

The `User` type is analyzed and an appropriate OpenAPI schema is generated. This schema is usually included in the specification's
components/schemas section and referenced by the response documentation.

The plugin can handle schemas in three ways. All three approaches produce the same result in the OpenAPI specification,
but differ in how and where schemas are defined.


### 1. Automatic generation from types

```kotlin
body<User>() // (1)!
```

1. Schema is automatically generated from User type.

??? info "More Information"

    More information on automatically generating schemas:

    [:octicons-arrow-right-24: Automatic Schema Generation](./automatic_schema_generation_overview.md)


### 2. Manual schema definition

```kotlin
body(Schema<Any>().apply { // (1)!
    type = "object"
    properties = mapOf(
        "id" to Schema<Any>().apply { type = "string" },
        "name" to Schema<Any>().apply { type = "string" }
    )
})
```

1. Define a Swagger `Schema` directly.

??? info "More Information"

    More information on manually defining schemas:

    [:octicons-arrow-right-24: Manual Schema Definition](./manual_schema_definition.md)


### 3. Global schema references (automatic generation or manual definition)

```kotlin
install(OpenApi) {
    schemas {
        schema<User>("user-schema") // (1)!
    }
}

body(ref("user-schema")) // (2)!
```

1. Define schema once in plugin configuration (same options as in routes).
2. Reference schema in routes by its id.

??? info "More Information"

    More information on globally defining schemas:

    [:octicons-arrow-right-24: Global Schemas](./global_schemas.md)
