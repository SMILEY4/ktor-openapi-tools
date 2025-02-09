# Plugin Configuration

???+ info "More Information"
    
    The complete list of all available plugin configuration options can be viewed in the [API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-open-api-plugin-config/index.html).


## Automatically Assigning Tags

Tags can either be assigned at the route documentation directly or via a mapping function in the plugin configuration.
The specified function is called for each route and the returned tags are added to the route's documentation.

```kotlin
install(OpenApi) {
    tags {
        tagGenerator = { url -> TODO() }
    }
}
```


## Ignoring Route Selectors

Other Ktor plugins may add additional route selectors to routes that show up the OpenAPI specification as unwanted parts in the urls.
These can be ignored by adding them to the `ignoredRouteSelectors` or `ignoredRouteSelectorClassNames` configuration.

???+ example "Ignoring Route Selector"

    ```kotlin
    install(OpenApi) {
        tags {
            ignoredRouteSelectors = ignoredRouteSelectors + RateLimitRouteSelector::class//(1)!
            ignoredRouteSelectorClassNames = ignoredRouteSelectorClassNames + "io.ktor.server.plugins.ratelimit.RateLimitRouteSelector"//(2)!
        }
    }
    ```

    1. Ignore the route selector from the Ktor [Rate Limiting Plugin](https://ktor.io/docs/server-rate-limit.html) by adding its class to the ignored selectors.
    2. Ignore the route selector from the Ktor [Rate Limiting Plugin](https://ktor.io/docs/server-rate-limit.html) by adding its qualified name to the ignored selector names. This can be useful when the actual class is internal and cannot be accessed.

## Excluding Routes

Routes can be completely excluded from the generated OpenAPI specification via the `hidden`-flag at the route documentation or the `pathFilter`-function in the plugin configuration.

???+ example "Excluding Routes"

    === "Route Documentation"
        ```kotlin
        routing {
            get({
                hidden = true //(1)!
            }) {
                call.respond(HttpStatusCode.NotImplemented, Unit)
            }
        }
        ```

        1. Mark this route as "hidden" and exclude it from the generated OpenAPI specification.

    === "Plugin Configuration"
    
        ```kotlin
        install(OpenApi) {
            pathFilter = { method, url -> url.firstOrNull() != "internal" }//(1)!
        }
        ```
    
        1. Filter out and exclude all routes with urls starting with "internal". The function receives the http method and url as inputs and returns `false` for excluded routes.

## Choosing the output format - JSON or YAML

The output format of the generated OpenAPI specification can be changed from `json` to `yaml` with the `outputFormat` option.

???+ example "Choosing the Output Format"

    === "JSON"
        ```kotlin
        install(OpenApi) {
            outputFormat = OutputFormat.JSON
        }
        ```

    === "YAML"
    
        ```kotlin
        install(OpenApi) {
            outputFormat = OutputFormat.YAML
        }
        ```

## Customizing Schemas

[:octicons-arrow-right-24: Schema Generation](schema_generation.md)

## Customizing Examples

[:octicons-arrow-right-24: Example Encoding](example_encoding.md)

## Configuring Multiple OpenAPI Specifications

[:octicons-arrow-right-24: Multiple API Specifications](multiple_specs.md)
