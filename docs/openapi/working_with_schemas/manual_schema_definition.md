# Manual Schema Definition

Schemas can be defined manually using OpenAPI schema objects. This provides complete control over the schema structure:

```kotlin
import io.swagger.v3.oas.models.media.Schema

post("data", {
    request {
        body(Schema<Any>().apply {
            type = "object"
            title = "CustomData"
            properties = mapOf(
                "id" to Schema<Any>().apply {
                    type = "string"
                    format = "uuid"
                },
                "value" to Schema<Any>().apply {
                    type = "number"
                    minimum = BigDecimal.ZERO
                    maximum = BigDecimal(100)
                }
            )
            required = listOf("id", "value")
        })
    }
}) { }
```

