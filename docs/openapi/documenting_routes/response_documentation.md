# Response Documentation

Response documentation describes what an endpoint returns - status codes, response bodies, and headers. This information is defined in the response block of route documentation.

## The Response Block

Response documentation is provided within the response configuration block:

```kotlin
get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            description = "User found"
            body<User>()
        }
    }
}) { }
```


## Status Code Responses

Status codes indicate the outcome of a request. Multiple status codes can be documented in the same block, each with its own configuration.

```kotlin
get("users", {
    response {
        code(HttpStatusCode.OK) {
            description = "User found"
            body<User>()
        }
        code(HttpStatusCode.NotFound) {
            description = "User not found"
        }
        default {
            description = "Unexpected error"
            body<ErrorResponse>()
        }
    }
}) { }
```

**Alternate Syntax**

Two syntaxes are available for defining status codes. Both syntaxes are functionally identical.

```kotlin
response {
    // Using code() function
    code(HttpStatusCode.OK) {
        description = "Success"
    }
    
    // Using infix notation
    HttpStatusCode.OK to {
        description = "Success"
    }
}
```

## Response Body

Response bodies define the structure of data returned for each status code.

```kotlin
get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            description = "User found"
            body<User> {
                description = "The requested user"
            }
        }
    }
}) { }
```

The type specified (e.g., User) is automatically converted to an OpenAPI schema using the configured schema generator.

### Using Schema References

Global schemas can be referenced instead of inline types:

```kotlin
get("users", {
    response {
        code(HttpStatusCode.OK) {
            body(ref("user-list-schema")) {
                description = "List of users"
            }
        }
    }
}) { }
```

Global schemas are defined in plugin configuration. See Local vs Global Schemas for details.

## Response Headers

Document headers included in responses:

```kotlin
post("users", {
    response {
        code(HttpStatusCode.Created) {
            description = "User created"
            body<User>()
            headerParameter<String>("X-Request-ID") {
                description = "Request identifier for tracking"
                required = true
            }
        }
    }
}) { }
```