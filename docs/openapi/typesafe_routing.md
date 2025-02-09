# Type-safe Routing

This project supports documenting Ktor routes defined using [Type-safe Routing](https://ktor.io/docs/server-resources.html).

???+ example "Documenting Routes"

    ```kotlin
    @Resource("{id}")
    class Entity( //(1)!
        val id: String
    )
    ```
    
    1. Define resources classes as usual. 
    
    ```kotlin
    import io.github.smiley4.ktoropenapi.resources.get //(1)!
    
    routing {
        get<Entity>({ //(2)!
            description = "Returns the entity with the requested id."
        }) {
            call.respond(HttpStatusCode.NotImplemented, Unit) //(3)!
        }
    }
    ```
    
    1. Replace `io.ktor.server.resources.get` with `io.github.smiley4.ktoropenapi.resources.get`. Same for other http methods.
    2. Define the route using the resource class and add additional documentation.
    3. Handle requests as usual.

??? info "More Information"

    See [Documenting Routes](documenting_routes.md) for more general information.

???+ warning "Schema Generation"

    When using Type-safe routing, schemas must be generated using **kotlinx.serialization**.
    See [Schema Generation](schema_generation.md) for more information on how to change the default generator.

??? tip "Extracting Information from Resource Classes"

    When using type-safe routing, information about the routes can be extracted from the resources classes.
    This includes available query and path parameters with their names, types and whether they are optional.
    All other information still needs to be added manually and at the routes.

    To enable automatic extraction, set the `autoDocumentResourcesRoutes` option in the plugin configuration.

    ```kotlin
    install(OpenApi) {
        autoDocumentResourcesRoutes = true
    }
    ```