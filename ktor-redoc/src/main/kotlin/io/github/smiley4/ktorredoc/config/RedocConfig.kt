package io.github.smiley4.ktorredoc.config

import kotlin.jvm.java

/**
 * Configuration for redoc.
 * See https://redocly.com/docs/redoc/config
 */
class RedocConfig internal constructor() {

    companion object {
        private val defaultStaticResourcesPath: String by lazy {
            val stream = RedocConfig::class.java.classLoader
                .getResourceAsStream("META-INF/maven/org.webjars/redoc/pom.properties")
                ?: error("Could not find META-INF/maven/org.webjars/swagger-ui/pom.properties in classpath")
            val props = java.util.Properties().apply { load(stream) }
            val version = props.getProperty("version")
                ?: error("Could not extract redoc version from pom.properties")

            val path = "/META-INF/resources/webjars/redoc/$version"

            RedocConfig::class.java.getResource("$path/redoc.lib.js")
                ?: error("Could not find $path/redoc.lib.js in classpath. Check your redoc webjars dependency.")

            path
        }
    }

    /**
     * Path to the static resources for redoc in the jar-file.
     * Version must match the version of the redoc-webjars dependency.
     */
    internal val staticResourcesPath: String = defaultStaticResourcesPath


    /**
     * The title of the page
     */
    var pageTitle: String = "Redoc"

    /**
     * Disables search indexing and hides the search box from the API documentation page.
     * https://redocly.com/docs/redoc/config#disablesearch
     */
    var disableSearch: Boolean? = null


    /**
     * Sets the minimum amount of characters that need to be typed into the search dialog to initiate the search.
     * https://redocly.com/docs/redoc/config#mincharacterlengthtoinitsearch
     */
    var minCharacterLengthToInitSearch: Int? = null


    /**
     * Enables or disables expanding default server variables.
     * https://redocly.com/docs/redoc/config#expanddefaultservervariables
     */
    var expandDefaultServerVariables: Boolean? = null


    /**
     * Controls which responses to expand by default.
     * Specify one or more responses by providing their response codes as a list, for example expandResponses=["200","201"].
     * Special value 'all' expands all responses by default.
     * Be careful: this option can slow down documentation rendering time.
     * https://redocly.com/docs/redoc/config#expandresponses
     */
    var expandResponses: Collection<String>? = null


    /**
     * Automatically expands the single field in a schema.
     * https://redocly.com/docs/redoc/config#expandsingleschemafield
     */
    var expandSingleSchemaField: Boolean? = null


    /**
     * Hides the 'Download' button for saving the API definition source file.
     * This setting does not make the API definition private; it just hides the button.
     * https://redocly.com/docs/redoc/config#hidedownloadbutton
     */
    var hideDownloadButton: Boolean? = null


    /**
     * If set to true, the protocol and hostname are not shown in the operation definition.
     * https://redocly.com/docs/redoc/config#hidehostname
     */
    var hideHostname: Boolean? = null


    /**
     * Hides the loading animation.
     * https://redocly.com/docs/redoc/config#hideloading
     */
    var hideLoading: Boolean? = null


    /**
     * Hides request payload examples.
     * https://redocly.com/docs/redoc/config#hiderequestpayloadsample
     */
    var hideRequestPayloadSample: Boolean? = null


    /**
     * If set to true, the description for oneOf/anyOf object is not shown in the schema.
     * https://redocly.com/docs/redoc/config#hideoneofdescription
     */
    var hideOneOfDescription: Boolean? = null


    /**
     * If set to true, the pattern is not shown in the schema.
     * https://redocly.com/docs/redoc/config#hideschemapattern
     */
    var hideSchemaPattern: Boolean? = null


    /**
     * Hides the schema title next to the type.
     * https://redocly.com/docs/redoc/config#hideschematitles
     */
    var hideSchemaTitles: Boolean? = null


    /**
     * Hides the Security panel section.
     * https://redocly.com/docs/redoc/config#hidesecuritysection
     */
    var hideSecuritySection: Boolean? = null


    /**
     * Hides the request sample tab for requests with only one sample.
     * https://redocly.com/docs/redoc/config#hidesinglerequestsampletab
     */
    var hideSingleRequestSampleTab: Boolean? = null


    /**
     * Sets the path to the optional HTML file used to modify the layout of the reference docs page.
     * https://redocly.com/docs/redoc/config#htmltemplate
     */
    var htmlTemplate: String? = null


    /**
     * Sets the default expand level for JSON payload samples (response and request body).
     * The default value is "2", and the maximum supported value is '+Infinity'.
     * It can also be configured with the special value "all" that expands all levels.
     * https://redocly.com/docs/redoc/config#jsonsampleexpandlevel
     */
    var jsonSampleExpandLevel: String? = null


    /**
     * Displays only the specified number of enum values. The remaining values are hidden in an expandable area.
     * If not set, all values are displayed.
     * https://redocly.com/docs/redoc/config#maxdisplayedenumvalues
     */
    var maxDisplayedEnumValues: Int? = null


    /**
     * If set to true, selecting an expanded item in the sidebar twice collapses it.
     * https://redocly.com/docs/redoc/config#menutoggle
     */
    var menuToggle: Boolean? = null


    /**
     * If set to true, the sidebar uses the native scrollbar instead of perfect-scroll.
     * This setting is a scrolling performance optimization for big API definitions.
     * https://redocly.com/docs/redoc/config#nativescrollbars
     */
    var nativeScrollbars: Boolean? = null


    /**
     * Shows only required fields in request samples.
     * https://redocly.com/docs/redoc/config#onlyrequiredinsamples
     */
    var onlyRequiredInSamples: Boolean? = null


    /**
     * Shows the path link and HTTP verb in the middle panel instead of the right panel.
     * https://redocly.com/docs/redoc/config#pathinmiddlepanel
     */
    var pathInMiddlePanel: Boolean? = null


    /**
     * If set, the payload sample is inserted at the specified index.
     * If there are N payload samples and the value configured here is bigger than N, the payload sample is inserted last.
     * Indexes start from 0.
     * https://redocly.com/docs/redoc/config#payloadsampleidx
     */
    var payloadSampleIdx: Int? = null


    /**
     * Shows required properties in schemas first, ordered in the same order as in the required array.
     * https://redocly.com/docs/redoc/config#requiredpropsfirst
     */
    var requiredPropsFirst: Boolean? = null


    /**
     * Specifies whether to automatically expand schemas in Reference docs.
     * Set it to "all" to expand all schemas regardless of their level, or set it to a number to expand schemas up to the specified level.
     * For example, schemaExpansionLevel: "3" expands schemas up to three levels deep.
     * The default value is 0, meaning no schemas are expanded automatically.
     * https://redocly.com/docs/redoc/config#schemaexpansionlevel
     */
    var schemaExpansionLevel: String? = null


    /**
     * Shows object schema example in the properties; default false.
     * https://redocly.com/docs/redoc/config#showobjectschemaexamples
     */
    var showObjectSchemaExamples: Boolean? = null


    /**
     * When set to true, shows the HTTP request method for webhooks in operations and in the sidebar.
     * https://redocly.com/docs/redoc/config#showwebhookverb
     */
    var showWebhookVerb: Boolean? = null


    /**
     * Shows only unique oneOf types in the label without titles.
     * https://redocly.com/docs/redoc/config#simpleoneoftypelabel
     */
    var simpleOneOfTypeLabel: Boolean? = null


    /**
     * When set to true, sorts all enum values in all schemas alphabetically.
     * https://redocly.com/docs/redoc/config#sortenumvaluesalphabetically
     */
    var sortEnumValuesAlphabetically: Boolean? = null


    /**
     * When set to true, sorts operations in the navigation sidebar and in the middle panel alphabetically.
     * https://redocly.com/docs/redoc/config#sortoperationsalphabetically
     */
    var sortOperationsAlphabetically: Boolean? = null


    /**
     * When set to true, sorts properties in all schemas alphabetically.
     * https://redocly.com/docs/redoc/config#sortpropsalphabetically
     */
    var sortPropsAlphabetically: Boolean? = null


    /**
     * When set to true, sorts tags in the navigation sidebar and in the middle panel alphabetically.
     * Note that only tags are sorted alphabetically in the middle panel, not the operations associated with each tag.
     * To sort operations alphabetically as well, you must set the sortOperationsAlphabetically setting to true.
     * https://redocly.com/docs/redoc/config#sorttagsalphabetically
     */
    var sortTagsAlphabetically: Boolean? = null


    /**
     * If set to true, the API definition is considered untrusted and all HTML/Markdown is sanitized to prevent XSS.
     * https://redocly.com/docs/redoc/config#untrusteddefinition
     */
    var untrustedDefinition: Boolean? = null


    /**
     * The theme configuration setting is more complex since it represents many nested options, you can supply these as a JSON string
     * https://redocly.com/docs/redoc/config#theme-settings
     */
    var theme: String? = null

}
