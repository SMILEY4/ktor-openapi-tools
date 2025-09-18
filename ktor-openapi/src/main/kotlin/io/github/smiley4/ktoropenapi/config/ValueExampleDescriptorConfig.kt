package io.github.smiley4.ktoropenapi.config

class ValueExampleDescriptorConfig<T> {

    /**
     * the example value
     */
    var value: T? = null


    /**
     *  a short summary of the example
     */
    var summary: String? = null


    /**
     * a description of the example
     */
    var description: String? = null

}
