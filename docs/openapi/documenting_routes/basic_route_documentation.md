# Basic Route Documentation

Route documentation is provided through a configuration block that defines the API contract for an endpoint. This documentation is collected during application initialization and used to generate the OpenAPI specification.

## The Documentation Block

The documentation block is the first lambda parameter in documented route functions:

```kotlin
import io.github.smiley4.ktoropenapi.get

import io.github.smiley4.ktoropenapi.get

get("users", {
    // Documentation block - defines API contract
    description = "Retrieves all users"
    tags = listOf("users")

    request {
        queryParameter<String>("search")
    }

    response {
        code(HttpStatusCode.OK) {
            body<List<User>>()
        }
    }
}) {
    // Handler block - implements functionality
    call.respond(userService.getAllUsers())
}
```

The documentation block is evaluated once during route registration. It configures the OpenAPI specification and does not interfere with request handling.

## Describing Routes

The full list of available properties can be found in the [api reference](/ktor-openapi-tools/dokka/ktor-openapi/index.html).

### Basic Route Information

Every route should clearly communicate its purpose. All properties are optional, allowing documentation to be as minimal or detailed as needed. Properties closely match [official OpenAPI Specification](https://swagger.io/specification/).

```kotlin
get("users/{id}", {
    operationId = "getUserById"
    summary = "Get user by ID"
    description = "Retrieves detailed information for a specific user"
}) { }
```

### Organizing with Tags

Tags group related operations in documentation UIs:

```kotlin
get("products", {
    tags = listOf("products")
}) { }

get("admin/products", {
    tags = listOf("products", "admin")
}) { }
```

Tags can be inherited from parent routes, allowing consistent organization:

```kotlin
route("api/v1", {
    tags = listOf("v1")
}) {
    get("users", {
        tags = listOf("users")
        // actual tags for /api/v1/users: ["v1", "users"]
    }) { }
}
```

### Documenting Requests and Responses

The `request` and `response` blocks define what the endpoint accepts and returns:

```kotlin
post("users", {
    request {
        body<CreateUserRequest> {
            description = "User data"
            required = true
        }
    }

    response {
        code(HttpStatusCode.Created) {
            description = "User created successfully"
            body<User>()
        }
        code(HttpStatusCode.BadRequest) {
            description = "Invalid input data"
        }
    }
}) { }
```

Request and response documentation is covered in detail in dedicated pages:

- [Request Documentation](/ktor-openapi-tools/openapi/documenting_routes/request_documentation)
- [Response Documentation](/ktor-openapi-tools/openapi/documenting_routes/response_documentation)

### Security and Access Control

Document authentication requirements directly on routes:

```kotlin
get("profile", {
    security {
        requirement("bearerAuth")
    }
}) { }
```

Security schemes must be defined in the plugin configuration:

```kotlin
install(OpenApi) {
    security {
        securityScheme("bearerAuth") {
            type = SecuritySchemeType.HTTP
            scheme = "bearer"
            bearerFormat = "JWT"
        }
    }
}
```

### Multiple Specifications

When using multiple API specifications, routes can be assigned to specific ones:

```kotlin
route("v1", {
    specName = "v1"
}) {
    get("users") { /* v1 implementation */ }
}

route("v2", {
    specName = "v2"
}) {
    get("users") { /* v2 implementation */ }
}
```

See [Multiple OpenAPI Specifications](.) for more information.

### Hiding Internal Routes

Exclude routes from all specifications using the hidden flag:

```kotlin
get("internal/metrics", {
    hidden = true
}) {
    // Route functions normally but doesn't appear in the documentation
}
```
