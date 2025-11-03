# Global Examples

Global examples are defined once in the plugin configuration and can be referenced throughout route documentation. Global examples are placed in the `components/examples` section of the OpenAPI specification and referenced using their unique identifier.

While inline examples (those defined directly in the route documentation) are typically also placed in the specification's `components/examples` section and automatically deduplicated, global examples provide an easy, single source of truth for commonly used examples across routes.


## Defining Global Examples

Global examples are defined in the `examples` configuration block during plugin installation:

```kotlin
install(OpenApi) {
    examples {
        example("success-user") {
            value = User(
                id = "user-123",
                name = "John Doe",
                email = "john@example.com",
                role = "user"
            )
            summary = "Regular user"
            description = "Example of a standard user account"
        }
        
        example("admin-user") {
            value = User(
                id = "admin-1",
                name = "Admin User",
                email = "admin@example.com",
                role = "admin"
            )
            summary = "Administrative user"
            description = "Example of an admin user with elevated privileges"
        }
        
        example("error-not-found") {
            value = ErrorResponse(
                code = "NOT_FOUND",
                message = "The requested resource was not found"
            )
            summary = "Not found error"
        }
    }
}
```

Each example defined this way requires a unique identifier by which it is referenced later.

## Referencing Global Examples

Global examples are referenced by their identifier using the `exampleRef()` function:

```kotlin
import io.github.smiley4.ktoropenapi.config.exampleRef

get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            body<User>() {
                exampleRef("Success Response", "success-user")
            }
        }
        
        code(HttpStatusCode.NotFound) {
            body<ErrorResponse>() {
                exampleRef("Not Found Error", "error-not-found")
            }
        }
    }
}) { }
```

The exampleRef() function accepts a name for the example that is shown in documentation UIs and the identifier of the global example to use.

Local and global examples can be used together, providing the flexibility to use shared examples where appropriate while still allowing route-specific examples when needed.