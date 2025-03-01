package io.github.smiley4.ktoropenapi.config

/**
 * The authentication scheme.
 * https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml
 */
enum class AuthScheme(val swaggerType: String) {
    BASIC("Basic"),
    BEARER("bearer"),
    DIGEST("Digest"),
    HOBA("HOBA"),
    MUTUAL("Mutual"),
    OAUTH("OAuth"),
    SCRAM_SHA_1("SCRAM-SHA-1"),
    SCRAM_SHA_256("SCRAM-SHA-256"),
    VAPID("vapid")
}
