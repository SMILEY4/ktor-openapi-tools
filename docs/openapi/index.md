# OpenApi

[Ktor](https://ktor.io/) plugin to automatically generate [OpenAPI](https://www.openapis.org/) specifications from routes. Documentation can be added gradually to existing routes without requiring major changes to code.

```kotlin
install(OpenApi) {
    info {
        title = "My API"
        version = "1.0.0"
    }
}

routing {
    get("users/{id}", {
        description = "Get user by ID"
        response {
            code(HttpStatusCode.OK) {
                body<User>()
            }
        }
    }) {
        // Handler implementation
    }
}
```

**Features**

- Extends existing Ktor routing DSL with minimal code changes
- Automatic schema generation from Kotlin types using [schema-kenerator](https://github.com/SMILEY4/schema-kenerator)
- Support for reflection or kotlinx.serialization-based generation
- Type-safe routing integration via Resources plugin
- Complete OpenAPI 3.1.0 specification support
- Multiple API specifications from a single application
- Supports Jackson, Swagger, validation, and schema-kenerator annotations
- Document webhooks and server-sent events