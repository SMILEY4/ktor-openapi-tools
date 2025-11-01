# Request Documentation

Request documentation describes what data an endpoint accepts - parameters, headers, and request bodies. This information is defined in the request block of route documentation.

## The Request Block

Request documentation is provided within the request configuration block:

```kotlin
post("users", {
    // Documentation block - defines API contract
    request {
        // Request block - documents requests
        queryParameter<String>("source")
        headerParameter<String>("X-Request-ID")
        body<CreateUserRequest> {
            required = true
        }
    }
}) {
    // Handler block - implements functionality
}
```

## Path, Query, and Header Parameters

Parameters are documented using type-specific functions that specify the parameter location, name, and type.

### Path Parameters

Path parameters are extracted from the route path.

```kotlin
get("users/{id}", {
    request {
        pathParameter<String>("id") {
            description = "Unique user identifier"
        }
    }
}) { }
```

Path Parameters must match the names in the route path and are always marked as required. 

### Query Parameters

Query parameters are optional URL parameters:

```kotlin
get("users", {
    request {
        queryParameter<String>("search") {
            description = "Search term for filtering users"
            required = false
        }
    }
}) { }
```

### Header Parameters

Document expected or required headers:

```kotlin
post("users", {
    request {
        headerParameter<String>("X-Request-ID") {
            description = "Unique request identifier for tracing"
            required = false
        }
    }
}) { }
```

### Cookie Parameters

Document cookies used by the endpoint.

```kotlin
get("profile", {
    request {
        cookieParameter<String>("session") {
            description = "Session identifier"
            required = true
        }
    }
}) { }
```

### Parameter Configuration

All parameter types support similar configuration options. All available options can be found in the api reference.

```kotlin
queryParameter<String>("search") {
    description = "Search query"
    required = false
    deprecated = false
    example("Example 1") {
        value = "john"
    }
}
```

## Request Body

Request bodies define the structure of data sent in POST, PUT, PATCH, and other requests.

```kotlin
post("users", {
    request {
        body<CreateUserRequest> {
            description = "User creation data"
            required = true
        }
    }
}) { }
```

The type specified (e.g., CreateUserRequest) is automatically converted to an OpenAPI schema using the configured schema generator. A list of all available configuration options can be found in the api reference.

### Using Schema References

Global schemas can be referenced instead of inline types:

```kotlin
post("users", {
    request {
        body(ref("create-user-schema")) {
            description = "User creation data"
            required = true
        }
    }
}) { }
```

Global schemas are defined in plugin configuration. See Local vs Global Schemas for details.

## Multiple Content Types

Endpoints can accept multiple content types for the same request body:

```kotlin
import io.ktor.http.ContentType

post("data", {
    request {
        body<DataImport> {
            description = "Data to import"
            mediaTypes(
                ContentType.Application.Json,
                ContentType.Application.Xml
            )
        }
    }
}) { }
```

This documents that the endpoint accepts the same data structure in either JSON or XML format.

For endpoints that accept fundamentally different structures based on content type:

```kotlin
post("upload", {
    request {
        body<JsonUpload> {
            description = "JSON upload format"
            mediaTypes(ContentType.Application.Json)
        }
        body<ByteArray> {
            description = "Binary upload format"
            mediaTypes(ContentType.Application.OctetStream)
        }
    }
}) { }
```

## File Uploads

File uploads can be documented using appropriate content types.

### Single File Upload

```kotlin
post("upload", {
    request {
        body<ByteArray> {
            description = "File content"
            mediaTypes(ContentType.Application.OctetStream)
            required = true
        }
    }
}) { }
```

### Multipart Form Data

```kotlin
//todo
```

### Image Upload

```kotlin
post("profile/avatar", {
    request {
        body<ByteArray> {
            description = "Avatar image (JPEG or PNG)"
            mediaTypes(
                ContentType.Image.JPEG,
                ContentType.Image.PNG
            )
        }
    }
}) { }
```