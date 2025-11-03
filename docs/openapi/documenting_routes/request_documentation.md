# Request Documentation

Request documentation describes what data an endpoint accepts - parameters, headers, and request bodies.
This information is defined in the request block of route documentation.




## The Request Block

Request documentation is provided within the request configuration block:

```kotlin
post("users", {
    request {
        queryParameter<String>("source")
        headerParameter<String>("X-Request-ID")
        body<CreateUserRequest> {
            required = true
        }
    }
}) { }
```

??? info "API Reference"

    The full list of available configuration options for documenting requests can be found in the API reference:

    [:octicons-arrow-right-24: API Reference](../../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-request-config/index.html)




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

All parameter types support similar configuration options.

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

The type specified (e.g., CreateUserRequest) is automatically converted to an OpenAPI schema using the configured schema generator.


### Using Schema References

Global schemas defined in the plugin configuration can be referenced instead of inline types:

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

??? info "More Information"

    More information on handling request bodies can be found here:

    [:octicons-arrow-right-24: Schema Introduction](../working_with_schemas/schema_introduction.md)

    [:octicons-arrow-right-24: Global Schemas](../working_with_schemas/global_schemas.md)




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
post("multipart", {
    request {
        multipartBody {
            mediaTypes(ContentType.MultiPart.FormData)
            part<ByteArray>("first-image") {
                mediaTypes(
                    ContentType.Image.PNG,
                    ContentType.Image.JPEG,
                    ContentType.Image.SVG
                )
            }
            part<ByteArray>("second-image") {
                mediaTypes(
                    ContentType.Image.PNG,
                    ContentType.Image.JPEG,
                    ContentType.Image.SVG
                )
            }
        }
    }
}) { }
```