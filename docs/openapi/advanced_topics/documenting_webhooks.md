# Documenting Webhooks

Webhooks are HTTP callbacks that your API sends to external systems. While webhooks are not part of your server's request handling, they can be documented in the OpenAPI specification to help API consumers understand what data your API will send to their endpoints.

??? warning "Documentation Only"

**Important:** Webhook documentation is purely informational. It does not create callable Ktor routes and has no impact on your application's routing or request handling.

Webhooks appear only in the generated OpenAPI specification under the `webhooks` section. They document HTTP requests your API makes to external systems, not requests it receives.

## Describing Webhooks

Webhooks are documented using the webhook function:

```kotlin
routing {
    route("webhooks") {
        webhook(HttpMethod.Post, "concertAlert") {
            description = "Notify the registered URL with details of an upcoming concert"
            request {
                body<String> {
                    mediaTypes(ContentType.Text.Plain)
                    required = true
                }
            }
        }
    }
}
```

Parameters:

- httpMethod: HTTP method for the webhook (typically HttpMethod.Post)
- eventName: Unique identifier for the webhook event (e.g., "newOrder", "userCreated")

All standard route documentation options are available. See [Basic Route Documentation]() for complete documentation options.