# OpenAPI

[Ktor](https://ktor.io/) plugin to automatically generate [OpenAPI](https://www.openapis.org/) specifications from routes. Additional information can be gradually added to existing routes without requiring major changes to existing code.


## Features

- extends existing ktor dsl
- no immediate change to existing code required
- supports [OpenAPI 3.1.0 Specification](https://swagger.io/specification/)
- automatically generates json schemas from kotlin types
    - out-of-the-box support for type parameters, inheritance, collections, etc
    - usable with reflection or [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
    - supports [Jackson](https://github.com/FasterXML/jackson), [Swagger](https://github.com/swagger-api/swagger-core), [Javax](https://mvnrepository.com/artifact/javax.validation/validation-api)
    and [Jakarta](https://github.com/jakartaee/validation/tree/main) annotations
    - highly configurable and customizable


## Example

```kotlin
install(OpenApi) //(1)

routing {
    
    route("api.json") {
        openApi() //(2)
    }
    
    get("example", {
        description = "An example route" //(3)
        response {
            HttpStatusCode.OK to {
                description = "A success response"
                body<String>()
            }
        }
    }) {
        call.respondText("Hello World!") //(4)
    }
}
```

1. Install and configure the OpenAPI plugin.
2. Create a route to expose the OpenAPI specification file at `/api.json`.
3. Add (optional) information to the route, e.g. a description and responses and response bodies.
4. Handle requests as usual.