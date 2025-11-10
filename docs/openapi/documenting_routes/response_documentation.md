# Response Documentation

Response documentation describes what an endpoint returns - status codes, response bodies, and headers.
This information is defined in the response block of route documentation.

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

??? info "API Reference"

    The full list of available configuration options for documenting responses can be found in the API reference:

    [:octicons-arrow-right-24: API Reference](../../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-response-config/index.html)




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
code(HttpStatusCode.OK) {
    description = "Success"
}

HttpStatusCode.OK to {
    description = "Success"
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

Global schemas defined in the plugin configuration can be referenced instead of inline types:

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

??? info "More Information"

    More information on handling response bodies can be found here:

    [:octicons-arrow-right-24: Schema Introduction](../working_with_schemas/schema_introduction.md)

    [:octicons-arrow-right-24: Global Schemas](../working_with_schemas/global_schemas.md)




## Response Headers

Document headers included in responses:

```kotlin
post("users", {
    response {
        code(HttpStatusCode.Created) {
            description = "User created"
            body<User>()
            header<String>("X-Request-ID") {
                description = "Request identifier for tracking"
                required = true
            }
        }
    }
}) { }
```