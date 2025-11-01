# Global Schemas

Schemas can be defined globally in the plugin configuration and referenced by ID throughout route documentation. Global schemas can be defined manually or generated automatically.

## Defining Global Schemas

Global schemas are defined in the `schemas` configuration block:

```kotlin
install(OpenApi) {
    schemas {
        // From type - schema generated automatically
        schema<User>("user-schema")
        schema<Product>("product-schema")
        
        // From KType
        schema("user-list", typeOf<List<User>>())
        
        // Manual schema definition
        schema("error-schema", Schema<Any>().apply {
            type = "object"
            properties = mapOf(
                "code" to Schema<Any>().apply { type = "string" },
                "message" to Schema<Any>().apply { type = "string" }
            )
            required = listOf("code", "message")
        })
    }
}
```

## Referencing Global Schemas

Global schemas are referenced by their ID using the ref() function:

```kotlin
import io.github.smiley4.ktoropenapi.config.ref

get("users", {
    response {
        code(HttpStatusCode.OK) {
            body(ref("user-list"))
        }
        default {
            body(ref("error-schema"))
        }
    }
}) { }

post("users", {
    request {
        body(ref("user-schema"))
    }
    response {
        code(HttpStatusCode.Created) {
            body(ref("user-schema"))
        }
    }
}) { }
```
