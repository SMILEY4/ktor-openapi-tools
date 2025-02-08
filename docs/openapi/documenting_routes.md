# Documenting Routes

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



## Specifying Types and Schemas Directly
Usually, schemas for request and response bodies, headers and parameters are generated automatically from types specified directly at the route.

### As generic types

```kotlin
body<MyExampleData>(/*...*/)

queryParameter<String>(/*...*/)

header<List<String>>(/*...*/) 

//...
```

### As KTypes

```kotlin
body(typeof<MyExampleData>(), /*...*/)

queryParameter(typeof<String>(), /*...*/)

header(typeof<List<String>>(), /*...*/)

//...
```

### As a Swagger Schema
```kotlin
body(Schema<Any>().also {
    it.type = "object"
    //...
}, /*...*/)

queryParameter(Schema<Any>().also {
    it.type = "string"
}, /*...*/)

header(Schema<Any>().also {
    it.type = "array"
    //...
}, /*...*/)

//...
```

## Global Types and Schemas
In addition, **custom** and **global** schemas can be defined in the schema section of the plugin configuration and then be referenced by multiple routes by their schema ids.

### Defining global schemas

```kotlin
install(OpenApi) {
    schemas {
        // register new schema with the id "example-schema-1" and a generic type
        schema<MyExampleData("example-schema-1")

        // register new schema with the id "example-schema-2" and a KType
        schema("example-schema-2", typeof<String>())

        // register new schema with the id "example-schema-3" and a swagger-schema
        schema("example-schema-3", Schema<Any>().also {
            it.type = "array"
            //...
        })
    }
}
```

### Referencing global schemas

```kotlin
import io.github.smiley4.ktoropenapi.config.ref

//...

body(ref("example-schema-1"), /*...*/)

queryParameter(ref("example-schema-2"), /*...*/)

header(ref("example-schema-2"), /*...*/)

//...
```


## Composite Schemas
Schemas and types can be combined to created variations without having to create new classes.
Available additional composite operations are `array` and `anyOf`

### array
Creates a schema of an array containing items of the specified type or schema.

```kotlin
import io.github.smiley4.ktoropenapi.config.array

//... 

body(array<MyExampleData>())

//...
```

### anyOf
Creates a schema that describes any one of the specified types or schemas.

```kotlin
import io.github.smiley4.ktoropenapi.config.anyOf

//...

body(anyOf(typeOf<MyExampleData>(), typeOf<MyOtherData>() /*, ...*/))

//...
```

### nesting
The operations array and anyOf as well as ref and type can be combined and nested to create more complex variations.

```kotlin
body(
    anyOf(
        array<String>(),
        anyOf(
            ref("my-schema"),
            type<MyExampleData>()
        ),        
    )
)
```

## Overwriting Types
Types can be overwritten with own provided schemas or other types.

```kotlin
install(OpenApi) {
    schemas {
        // overwrite type "List<Int>" with an array of either floats or integer 
        overwrite<List<Int>(
            array(
                anyOf(
                    typeof<Float>(),
                    typeof<Int>()
                )
            )
        ), 

        // overwrite type "File" with custom schema for binary data
        overwrite<File>(Schema<Any>().also {
            it.type = "string"
            it.format = "binary"
        })

    }
}
```

!!! note
When a body or parameter in a request or response defines a schema of a type that is overwritten in the config, the other given type or schema will be used instead.

!!! warning
Overwriting only work for top-level types and not nested structures (e.g. fields in other types).
See [Customizing Schema Generation](schema_generation.md) and [schema-kenerator Type Redirects](https://github.com/SMILEY4/schema-kenerator/wiki/Type-Redirects) instead.
