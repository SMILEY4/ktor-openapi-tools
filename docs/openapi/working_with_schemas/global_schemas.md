# Global Schemas

Schemas can be defined globally in the plugin configuration and referenced by ID throughout route documentation.

Global schemas can be automatically generated from types or manually defined with complete control over their structure. Once defined, they are placed in the `components/schemas` section of the OpenAPI specification and can be referenced from any route using their unique identifier.

While inline schemas (those defined directly with e.g. `body<Type>()`) are typically also placed in the specification's `components/schemas` section and automatically deduplicated, global schemas provide explicit control over schema definition and sharing, which is particularly valuable for manually defined Swagger `Schema` objects that need consistent reuse across multiple routes.

## Defining Global Schemas

Global schemas are defined in the schemas configuration block during plugin installation. Each schema must have a unique ID that is used to reference it from route documentation.

Schemas can be automatically generated from a kotlin type or be defined manually as a swagger schema:

```kotlin
install(OpenApi) {
    schemas {
        // Generated schemas
        schema<User>("user")
        schema<Product>("product")

        // Generic types
        schema("user-list", typeOf<List<User>>())

        // Manual schemas
        schema("error", Schema<Any>().apply {
            type = "object"
            properties = mapOf(
                "code" to Schema<Any>().apply { type = "string" },
                "message" to Schema<Any>().apply { type = "string" }
            )
        })
    }
}
```

## Referencing Global Schemas

Global schemas are referenced by their ID using the ref() function. References can be used anywhere a schema is accepted:

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
```
