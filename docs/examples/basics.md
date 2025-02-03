[//]: # (# Basics)

[//]: # ()
[//]: # (This example shows a basic usage of the plugin)

[//]: # ()
[//]: # (## Complete Example)

[//]: # (```kotlin title="Basics.kt" linenums="1")

[//]: # (--8<-- "Basics.kt")

[//]: # (```)

[//]: # ()
[//]: # (## Breaking it down)

[//]: # (### Install and configure the plugin)

[//]: # ()
[//]: # (```kotlin title="Basics.kt")

[//]: # (--8<-- "Basics.kt:24:44")

[//]: # (```)

[//]: # ()
[//]: # (### Configure routes)

[//]: # ()
[//]: # (#### Spec routes)

[//]: # ()
[//]: # (=== "OpenAPI")

[//]: # ()
[//]: # (    Configure the OpenAPI spec file to be served from `/api.json`)

[//]: # (    )
[//]: # (    ```kotlin)

[//]: # (    --8<-- "Basics.kt:50:52")

[//]: # (    ```)

[//]: # ()
[//]: # (=== "Swagger UI")

[//]: # ()
[//]: # (    Configure Swagger UI to be served from `/swagger` with the OpenAPI spec file at `/api.json`)

[//]: # ()
[//]: # (    ```kotlin)

[//]: # (    --8<-- "Basics.kt:55:57")

[//]: # (    ```)

[//]: # ()
[//]: # (=== "ReDoc")

[//]: # ()
[//]: # (    Configure Swagger UI to be served from `/redoc` with the OpenAPI spec file at `/api.json`)

[//]: # ()
[//]: # (    ```kotlin)

[//]: # (    --8<-- "Basics.kt:55:57")

[//]: # (    ```)

[//]: # ()
[//]: # (!!! note)

[//]: # ()
[//]: # (    The spec routes will not be included in the final OpenAPI spec file)

[//]: # ()
[//]: # ()
[//]: # (#### Document a route)

[//]: # ()
[//]: # (We add information about the route itself)

[//]: # ()
[//]: # (```kotlin title="Basics.kt" hl_lines="3")

[//]: # (--8<-- "Basics.kt:65:86")

[//]: # (```)

[//]: # ()
[//]: # (We add information about the request)

[//]: # ()
[//]: # (```kotlin title="Basics.kt" hl_lines="5-10")

[//]: # (--8<-- "Basics.kt:65:86")

[//]: # (```)

[//]: # ()
[//]: # (And finally, the response)

[//]: # ()
[//]: # (```kotlin title="Basics.kt" hl_lines="12-15")

[//]: # (--8<-- "Basics.kt:65:86")

[//]: # (```)

# Basic

This example shows a basic usage of the plugin

```kotlin title="Basics.kt" linenums="1"
--8<---- "Basics.kt"
```
