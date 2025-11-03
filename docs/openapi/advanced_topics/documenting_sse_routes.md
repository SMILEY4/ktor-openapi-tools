# Documenting SSE Routes

Server-Sent Events (SSE) routes can be documented for discoverability in the OpenAPI specification.
While SSE functionality works normally in Ktor, documentation is achieved by wrapping SSE routes in a documented parent route.

!!! warning "Limited OpenAPI Support"

    OpenAPI has limited support for Server-Sent Events. The specification does not officially define SSE endpoints,
    so not all documentation features work as expected or may not render correctly in documentation UIs.
    
    SSE documentation is primarily for discoverability and basic information. Advanced SSE features (event types, reconnection behavior,
    stream format) are not well-represented in OpenAPI.


## Describing SSE-Routes

SSE routes are documented by wrapping them in a parent route with documentation:

```kotlin
import io.ktor.server.sse.sse

route({
    description = "Server-sent events stream for real-time updates"
    tags = listOf("events", "sse")
}) {
    sse("/events") {
        send(ServerSentEvent(data = "Hello"))
    }
}
```

??? info "More Information"

    All standard route documentation options are available (in theory). For more information, see:

    [:octicons-arrow-right-24: Basic Route Documentation](./../documenting_routes/basic_route_documentation.md)