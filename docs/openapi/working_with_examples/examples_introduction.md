# Examples Introduction

Examples provide sample data for requests and responses in the OpenAPI specification. They help API consumers understand data structures and test endpoints with realistic values.

## What Are Examples?

Examples are sample values that illustrate what data looks like for request parameters, request bodies, and response bodies. They appear in the OpenAPI specification and are displayed by documentation tools like Swagger UI and ReDoc.

## How Examples Are Used

Examples can be provided for various API elements:

**Request Body Examples**

```kotlin
post("users", {
    request {
        body<CreateUserRequest>() {
            example("Basic") {
                value = CreateUserRequest(name = "John", email = "john@example.com")
            }
        }
    }
}) { }
```

**Response Body Examples**

```kotlin
get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            body<User>() {
                example("Success") {
                    value = User(id = "123", name = "John")
                }
            }
        }
    }
}) { }
```

**Parameter Examples Examples**

```kotlin
get("search", {
    request {
        queryParameter<String>("query") {
            example("Search Example") {
                value = "kotlin"
            }
        }
    }
}) { }
```

**Header Examples**

```kotlin
get("data", {
    request {
        headerParameter<String>("X-Request-ID") {
            example("ID Example") {
                value = "req-123456"
            }
        }
    }
}) { }
```

The plugin supports three ways to define examples in route documentation.

### As Kotlin Objects

The most straightforward approach is using Kotlin objects directly:

```kotlin
body<User>() {
    example("Example User") {
        value = User(
            id = "user-123",
            name = "John Doe",
            email = "john@example.com"
        )
    }
}
```

The plugin automatically encodes the Kotlin object using the configured example encoder.

### As Swagger Example Objects

For complete control over example structure, Swagger Example objects can be used directly:

```kotlin
import io.swagger.v3.oas.models.examples.Example

body<User>() {
    example("Custom Example", Example().apply {
        summary = "Custom user example"
        description = "Manually constructed example"
        value = mapOf(
            "id" to "custom-123",
            "name" to "Custom User"
        )
    })
}
```

### As References to Global Examples

Examples can be defined globally in plugin configuration and referenced by ID:

```kotlin
// Define once in plugin configuration
install(OpenApi) {
    examples {
        example("standard-user") {
            value = User(id = "1", name = "John")
            description = "Standard user example"
        }
    }
}

// Reference in routes
body<User>() {
    exampleRef("Standard User", "standard-user")
}
```

Detailed information on global examples can be found in Global Examples.

## Example Encoding

Kotlin objects used as example values are automatically encoded for inclusion in the OpenAPI specification. The encoding process converts Kotlin objects to a format suitable for the specification (typically JSON).
The default encoder uses Swagger's internal serialization. Custom encoders can be configured to customize or match the application serialization behavior:

```kotlin
install(OpenApi) {
    examples {
        encoder = ExampleEncoder.kotlinx() // Use kotlinx.serialization
    }
}
```

Detailed information on configuring example encoding can be found in Configuring Example Encoding.