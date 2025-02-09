# Documenting Routes

The `ktor-openapi` plugin provides functions to create documented `get`, `post`, `put`, `delete`, `patch`, `options`, `head`, `route` and `method` routes.
These work exactly the same as their respective base Ktor functions, but allow OpenAPI documentation to be added.
Routes provided by the plugin and routes from Ktor can be mixed and allow for a gradual enhancement of the api with documentation.

???+ example "Adding Documentation to Routes"

    ```kotlin
    import io.github.smiley4.ktoropenapi.get //(1)!
    
    get("hello", {
        description = "A Hello-World route" //(2)!
        request { //(3)!
            queryParameter<String>("name") { //(4)!
                description = "the name to greet"
            }
        }
        response { //(5)!
            code(HttpStatusCode.OK) { //(6)!
                description = "successful request"
            }
        }
    }) {
        call.respond(HttpStatusCode.NotImplemented, Unit) //(7)!
    }
    ```
    
    1. Replace `io.ktor.server.routing.get` with `io.github.smiley4.ktoropenapi.get`. Same for other http methods.
    2. Add a description to the route.
    3. Document request information, e.g. parameters, headers, bodies, etc.
    4. Specify a query parameter `name` of type `String` and add a description.
    5. Document possible responses, response bodies, headers, etc.
    6. Specify a possible response with the status code `200 OK` and add a description.
    7. Handle requests as usual.



??? tip "Nested Documentation"

    Documentation can be added at any level and documentation of parent and child routes are merged together, with priority given to the deepest route.

    ```kotlin
    route("parent", {
        description = "Common description for all routes." //(1)!
    }) {
        
        get("child1") { //(2)!
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }
    
        get("child2", {
            description = "Specific description of child 2." //(3)!
        }) {
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }
        
    }
    ```

    1. The route `/parent` has a description added to it. This description applies to this route and all child routes, as long as it is not overwritten by any deeper route.
    2. `/parent/child1` does not have any own documentation and only the information of the parent routes applies.
    3. `/parent/child2` specifies its own description and overwrites the description of the parent route.

??? info "API Reference"

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-route-config/index.html) for more information on available documentation options for routes.


## Documenting Requests

Information about a request is added in the `request`-block of a route-documentation.

???+ example "Adding Information About a Request"

    ```kotlin
    post("entity/{id}", {
        description = "Create a new entity."
        request { //(1)!
            pathParameter<String>("id") { //(2)!
                description = "The id of the entity to create."
                example("default") {
                    value = UUID.randomUUID().toString()
                }
            }
            body<NewEntity> { //(3)!
                description = "The data of the new entity to create."
                required = true
                example("default") {
                    value = NewEntity()
                }
            }
        }
    }) {
        call.respond(HttpStatusCode.NotImplemented, Unit)
    }
    ```

    1. Add all information about http requests in the `request`-block..
    2. Add documentation about the path parameter with the name `id` of type `String`.
    3. Add documentation about the request body. The body must be present (`required = true`) and be of type `NewEntity`.

??? info "API Reference"

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-request-config/index.html) for more information on available documentation options for requests.

## Documenting Responses

Information about possible responses is added in the `response`-block of a route-documentation.

???+ example "Adding Information About Responses"

    ```kotlin
    get("entities", {
        description = "Returns all entities from the system that the user has access to."
        response {
            code(HttpStatusCode.OK) { //(1)!
                body<List<Entity>> { //(2)!
                    required = true
                    description = "The list of available entities."
                }
            }
            code(HttpStatusCode.Unauthorized) {
                description = "The user is not authorized to view the list of entities."
            }
            default { //(3)!
                body<ErrorModel> {
                    description = "An unexpected error occurred"
                }
            }
        }
    }) {
        call.respond(HttpStatusCode.NotImplemented, Unit)
    }
    ```

    1. Add information for a response with a `200 OK` status code.
    2. Specify the body of this response. The body must be present (`required = true`) and be of type `List<Entity>`.
    3. Document the default response."

??? tip "Alternative Syntax for Status Codes"

    There are two alternative ways of specifying response codes. Both are valid options and functionally the same.

    === "code(...) {}"
        ```kotlin
        response {
            code(HttpStatusCode.OK) {
                //...
            }
        }
        ```
    
    === "... to {}"
        ```kotlin
        response {
            HttpStatusCode.OK to {
                //...
            }
        }
        ```

??? info "API Reference"

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-responses-config/index.html) for more information on available documentation options for requests.
