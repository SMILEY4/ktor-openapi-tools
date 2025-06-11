package io.github.smiley4.ktorswaggerui.config

/**
 * Configuration for the swagger ui.
 * See [Configuration | Swagger Docs](https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration)
 */
class SwaggerUIConfig internal constructor() {

    /**
     * Path to the static resources for swagger-ui in the jar-file.
     * Version must match the version of the swagger-ui-webjars dependency.
     */
    internal val staticResourcesPath: String = "/META-INF/resources/webjars/swagger-ui/5.17.14"

    // DISPLAY ========================================

    /**
     *  If set to true, enables deep linking for tags and operations.
     */
    var deepLinking: Boolean = true


    /**
     * Controls the display of operationId in operations list.
     */
    var displayOperationId: Boolean = false


    /**
     * The default expansion depth for models (set to -1 completely hide the models).
     */
    var defaultModelsExpandDepth: Number = 1


    /**
     *The default expansion depth for the model on the model-example section.
     */
    var defaultModelExpandDepth: Number = 1


    /**
     *Controls the display of the request duration (in milliseconds) for "Try it out" requests.
     */
    var displayRequestDuration: Boolean = false


    /**
     * If set, enables filtering. The top bar will show an edit box that you can use to filter the tagged operations that are shown.
     * Filtering is case-sensitive matching the filter expression anywhere inside the tag.
     */
    var filter: Boolean = false


    /**
     * If set, limits the number of tagged operations displayed to at most this many. The default is to show all operations.
     */
    var maxDisplayedTags: Number? = null


    /**
     * Apply a sort to the operation list of each API.
     */
    var operationsSorter: OperationsSort = OperationsSort.NONE


    /**
     * Controls the display of vendor extension (x-) fields and values for Operations, Parameters, Responses, and Schema.
     */
    var showExtensions: Boolean = false


    /**
     * Controls the display of extensions (pattern, maxLength, minLength, maximum, minimum) fields and values for Parameters.
     */
    var showCommonExtensions: Boolean = false


    /**
     * Apply a sort to the tag list of each API.
     */
    var tagsSorter: TagSort = TagSort.NONE


    /**
     * Syntax coloring theme to use
     */
    var syntaxHighlight: SwaggerUISyntaxHighlight = SwaggerUISyntaxHighlight.AGATE


    /**
     * Controls whether the "Try it out" section should be enabled by default.
     */
    var tryItOutEnabled: Boolean = false


    /**
     * Enables the request snippet section. When disabled, the legacy curl snippet will be used.
     */
    var requestSnippetsEnabled: Boolean = true

    /**
     *  `String=["list"*, "full", "none"]`.
     *  Controls the default expansion setting for the operations and tags.
     *  It can be 'list' (expands only the tags),
     *  'full' (expands the tags and operations) or 'none' (expands nothing).
     */
    var docExpansion: String? = null

    // NETWORK ========================================

    /**
     * OAuth redirect URL.
     */
    var oauth2RedirectUrl: String? = null


    /**
     * MUST be a valid javascript function. Function to intercept remote definition, "Try it out", and OAuth 2.0 requests.
     * Accepts one argument requestInterceptor(request) and
     * must return the modified request, or a Promise that resolves to the modified request.
     * Example: "req => { alert(JSON.stringify(req)); return req; }"
     */
    var requestInterceptor: String? = null


    /**
     * MUST be a function. Function to intercept remote definition, "Try it out", and OAuth 2.0 responses.
     * Accepts one argument responseInterceptor(response) and
     * must return the modified response, or a Promise that resolves to the modified response.
     * Example: "res => { alert(JSON.stringify(res)); return res; }"
     */
    var responseInterceptor: String? = null

    /**
     * List of HTTP methods that have the "Try it out" feature enabled. An empty array disables "Try it out" for all operations.
     * This does not filter the operations from the display.
     */
    var supportedSubmitMethods: Collection<String> =
        setOf("get", "put", "post", "delete", "options", "head", "patch", "trace")


    /**
     * Swagger UI can attempt to validate specs against swagger.io's online validator.
     * You can use this parameter to set a different validator URL, for example for locally deployed validators.
     * Set to "null" to disable validation.
     * Validation is disabled when the url of the api-spec-file contains localhost.
     * (see https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md#network)
     */
    var validatorUrl: String? = null


    /**
     * Set the [validatorUrl] to an online validator.
     */
    fun onlineSpecValidator() {
        validatorUrl = "https://validator.swagger.io/validator"
    }


    /**
     * Bring cookies when initiating network requests.
     * If set to true, enables passing credentials, as defined in the Fetch standard, in CORS requests that are sent by the browser.
     * Note that Swagger UI cannot currently set cookies cross-domain - as a result, you will have to rely on browser-supplied cookies
     * (which this setting enables sending) that Swagger UI cannot control.
     */
    var withCredentials: Boolean = false


    // AUTHORIZATION ==================================

    /**
     * If set to true, it persists authorization data and it would not be lost on browser close/refresh
     */
    var persistAuthorization: Boolean = false

}
