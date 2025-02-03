# Basic Information

The `ktor-openapi` plugin provides functions to create documented `get`, `post`, `put`, `delete`, `patch`, `options`, `head`, `route` and `method` routes.
These work exactly the same as their respective base Ktor functions, but allow OpenAPI documentation to be added.
Functions to create routes provided by the plugin and those from Ktor can be mixed and allow for a gradual enhancement of the api with documentation.

!!! note
    Documentation can be added at any level and documentation of parent and child routes are merged together, with priority given to the deepest route.

    ???+ example

        the `route` and a nested `get` have a `description` set.
        When the two documentations are merged, the description of the `get` takes priority
    
        ```kotlin
        routing {
        
            route("api", {
                // possible to add api-documentation here...
            }) {
                
                // a documented route
                get("hello", {
                    
                    // description of the route
                    description = "A Hello-World route"
                    
                    // information about the request
                    request {
                        // information about the query-parameter "name" of type "string"
                        queryParameter<String>("name") {
                            description = "the name to greet"
                        }
                    }
                    
                    // information about possible responses
                    response {
                        
                        // information about a "200 OK" response
                        HttpStatusCode.OK to {
                        
                            // a description of the response
                            description = "successful request - always returns 'Hello World!'"
                            
                            // information about the response bofy of type "string"
                            body<String>() {
                                description = "successful response body"
                            }
                        }
                    }
                }) {
                    // handle the request ...
                    call.respondText("Hello ${call.request.queryParameters["name"]}")
                }
                
            }
        }
        ```


## Request

Information about a request is added in the request-block of a route-documentation.

```kotlin
get("hello", {

    //...

    request {

        pathParameter<String>("paramname") {
            description = "A request parameter"
            example("Example #1") {
                value = "example value"
                summary = "an example"
                description = "An example value for the parameter"
            }
        }
        queryParameter<String>("paramname") { /*...*/ }
        headerParameter<String>("paramname") { /*...*/ }

        body<MyResponse>() {
            description = "The response body"
            required = true
            mediaTypes = setOf(ContentType.Application.Json, ContentType.Application.Xml)
            example("Example #1") {
                value = "example value"
                summary = "an example"
                description = "An example value for the body"
            }
        }

        multipartBody {
            mediaTypes = setOf(ContentType.MultiPart.FormData)
            part<String>("metadata") {
                mediaTypes = setOf(
                    ContentType.Text.Plain
                )
            }
            part<String>("image",) {
                header<Long>("size") {
                    description = "the size of the file in bytes"
                    required = true
                    deprecated = false
                    explode = false
                }
                mediaTypes = setOf(
                    ContentType.Image.PNG,
                    ContentType.Image.JPEG,
                )
            }
        }

    }

}) {
    // handle the request ...
}
```

## Responses
Adding information about the possible responses of a route.

```kotlin
get("hello", {

    //...

    response {
        HttpStatusCode.OK to {
            description = "The operation was successful"
            header<String>("Content-Length") {
                description = "The length of the returned content"
                required = false
                deprecated = true
            }
            body {
                // See Request ...
            }
            multipartBody {

            }
        }
        HttpStatusCode.InternalServerError to { /*...*/ }
        default { /*...*/ }
        "CustomStatus" to { /*...*/ }
    }
}) {
    // handle the request ...
}
```

## Request and Response Bodies
Information about request and response bodies can be added in the respective blocks.

```kotlin
get("hello", {

    //...

    request {

        body<MyResponse>() {
            description = "The response body"
            required = true
            mediaTypes = setOf(ContentType.Application.Json, ContentType.Application.Xml)
            example("Example #1") {
                value = "example value"
                summary = "an example"
                description = "An example value for the body"
            }
        }

        multipartBody {
            mediaTypes = setOf(ContentType.MultiPart.FormData)
            part<String>("metadata") {
                mediaTypes = setOf(
                    ContentType.Text.Plain
                )
            }
            part<String>("image",) {
                header<Long>("size") {
                    description = "the size of the file in bytes"
                    required = true
                    deprecated = false
                    explode = false
                }
                mediaTypes = setOf(
                    ContentType.Image.PNG,
                    ContentType.Image.JPEG,
                )
            }
        }

    }

    response {
        HttpStatusCode.OK to {
            body {
                // ...
            }
            multipartBody {
                // ...
            }
        }
    }
}) {
    // handle the request ...
}
```
