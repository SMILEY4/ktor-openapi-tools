package io.github.smiley4.ktoropenapi.config

/**
 * Generates additional tags for routes.
 * url - the parts of the route-url split at all `/`.
 * return a collection of tags. "Null"-entries will be ignored.
 */
typealias TagGenerator = (url: List<String>) -> Collection<String?>
