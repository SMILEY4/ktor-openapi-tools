package io.github.smiley4.ktorredoc

import io.github.smiley4.ktorredoc.config.RedocConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Registers the route for serving all redoc resources. The path to the OpenApi-file to use has to be given.
 * @param openApiUrl the url of the openapi spec
 * @param config the redoc configuration
 */
fun Route.redoc(openApiUrl: String, config: RedocConfig.() -> Unit = {}) {
    val redocConfig = RedocConfig().apply(config)
    markedRedoc {
        get {
            call.respondRedirect("${call.request.uri}/index.html")
        }
        get("index.html") {
            Redoc.serveIndexHtml(call, redocConfig, openApiUrl)
        }
        get("{filename}") {
            Redoc.serveStaticResource(call.parameters["filename"]!!, redocConfig, call)
        }
    }
}

internal object Redoc {

    internal suspend fun serveIndexHtml(call: ApplicationCall, config: RedocConfig, openApiUrl: String) {
        val properties = buildProperties(config, openApiUrl)
        val content = """
          <!DOCTYPE html>
          <html>
            <head>
              <title>${config.pageTitle}</title>
              <meta charset="utf-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <style>
                body {
                  margin: 0;
                  padding: 0;
                }
              </style>
            </head>
            <body>
              <redoc ${"\n"}${buildProperties(config, openApiUrl)}>
              </redoc>
              <script src="./redoc.standalone.js"> </script>
            </body>
          </html>
		""".trimIndent()
        call.respondText(ContentType.Text.Html, HttpStatusCode.OK) { content }
    }

    // see https://redocly.com/docs/redoc/confi
    @Suppress("CyclomaticComplexMethod")
    private fun buildProperties(config: RedocConfig, openApiUrl: String): String {
        return PropertyBuilder().also {properties ->
            properties["spec-url"] = openApiUrl
            properties["disable-search"] = config.disableSearch
            properties["min-character-length-to-init-search"] = null
            properties["key"] = config.minCharacterLengthToInitSearch
            properties["expand-default-server-variables"] = config.expandDefaultServerVariables
            properties["expand-responses"] = config.expandResponses?.let {
                if (it.any { v -> v.equals("all", ignoreCase = true) }) {
                    "all"
                } else {
                    it.joinToString(",")
                }
            }
            properties["expand-single-schema-field"] = config.expandSingleSchemaField
            properties["hide-download-button"] = config.hideDownloadButton
            properties["hide-hostname"] = config.hideHostname
            properties["hide-loading"] = config.hideLoading
            properties["hide-request-payload-sample"] = config.hideRequestPayloadSample
            properties["hide-one-of-description"] = config.hideOneOfDescription
            properties["hide-schema-pattern"] = config.hideSchemaPattern
            properties["hide-schema-titles"] = config.hideSchemaTitles
            properties["hide-security-section"] = config.hideSecuritySection
            properties["hide-single-request-sample-tab"] = config.hideSingleRequestSampleTab
            properties["html-template"] = config.htmlTemplate
            properties["json-sample-expand-level"] = config.jsonSampleExpandLevel
            properties["max-displayed-enum-values"] = config.maxDisplayedEnumValues
            properties["menu-toggle"] = config.menuToggle
            properties["native-scrollbars"] = config.nativeScrollbars
            properties["only-required-in-samples"] = config.onlyRequiredInSamples
            properties["path-in-middle-panel"] = config.pathInMiddlePanel
            properties["payload-sample-idx"] = config.payloadSampleIdx
            properties["required-props-first"] = config.requiredPropsFirst
            properties["schema-expansion-level"] = config.schemaExpansionLevel
            properties["show-object-schema-examples"] = config.showObjectSchemaExamples
            properties["show-webhook-verb"] = config.showWebhookVerb
            properties["simple-one-of-type-label"] = config.simpleOneOfTypeLabel
            properties["sort-enum-values-alphabetically"] = config.sortEnumValuesAlphabetically
            properties["sort-operations-alphabetically"] = config.sortOperationsAlphabetically
            properties["sort-props-alphabetically"] = config.sortPropsAlphabetically
            properties["sort-tags-alphabetically"] = config.sortTagsAlphabetically
            properties["untrusted-definition"] = config.untrustedDefinition
            properties["theme"] = config.theme
        }.render(
            prefix = "                ",
            separator = "\n",
            nullBehavior = "remove"
        )
    }

    internal suspend fun serveStaticResource(filename: String, config: RedocConfig, call: ApplicationCall) {
        val resourceName = "${config.staticResourcesPath}/$filename"
        val resource = Redoc::class.java.getResource(resourceName)
        if (resource != null) {
            call.respond(ResourceContent(resource))
        } else {
            call.respond(HttpStatusCode.NotFound, "'$filename' could not be found.")
        }
    }

}
