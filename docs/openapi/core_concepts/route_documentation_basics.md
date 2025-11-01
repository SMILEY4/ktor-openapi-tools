# Route Documentation Basics

Route documentation is added through documented route functions that extend standard Ktor routing with OpenAPI specification capabilities. These functions accept the same parameters as their standard counterparts, plus an additional lambda for documentation configuration.

Documented routes function identically to standard Ktor routes at runtime. The documentation block only affects specification generation and has no impact on request handling or application behavior.

## Basic Syntax

Documented route functions must be imported from `io.github.smiley4.ktoropenapi`:

```kotlin
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.patch
import io.github.smiley4.ktoropenapi.options
import io.github.smiley4.ktoropenapi.head
import io.github.smiley4.ktoropenapi.route
```

These functions replace their standard Ktor counterparts from `io.ktor.server.routing`.


```kotlin
// Standard Ktor route - not documented
import io.ktor.server.routing.get

get("minimal") {
    call.respondText("OK")
}

// Documented version
import io.github.smiley4.ktoropenapi.get

get("documented", {
    description = "A documented endpoint"
}) {
    call.respondText("OK")
}
```



## The Documentation Block

The documentation block is the configuration lambda that defines the API contract for a route. It supports various properties for describing the route.

Example documentation block for a route:

```kotlin
get("users/{id}", {
    operationId = "getUserById"
    summary = "Get user by ID"
    description = "Retrieves detailed information for a specific user"
    tags = listOf("users")
    deprecated = false
    
    request {
        pathParameter<String>("id") {
            description = "User identifier"
        }
    }
    
    response {
        code(HttpStatusCode.OK) {
            body<User>()
        }
        code(HttpStatusCode.NotFound) {
            description = "User not found"
        }
    }
}) {
    val userId = call.parameters["id"]
    // Handler implementation
}
```

## Mixing Documented and Undocumented Routes

Documented and standard Ktor routes coexist in the same application. Both routes register with Ktor normally, but only documented routes appear in the specification, while standard ktor-routes are completely excluded.

## Documentation Inheritance

Routes inherit documentation from parent routes, allowing common configuration to be defined once:

```kotlin
route("api", {
    tags = listOf("api")
    description = "API endpoints"
}) {
    
    get("users") {
        // Specification:
        //   tags = ["api"]
        //   description = "API endpoints"
    }
    
    get("products", {
        tags = listOf("products")
        description = "Product listing"
    }) {
        // Specification:
        //   tags = ["api", "products"] (merged with parent tags)
        //   description = "Product listing" (overwrites parent description)
    }
    
}
```