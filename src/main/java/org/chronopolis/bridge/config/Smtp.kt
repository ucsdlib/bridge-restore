package org.chronopolis.bridge.config

/**
 * Config for smtp reporting. Only expose what we need to.
 *
 * @since 1.0
 * @author shake
 */
interface SmtpConfig {
    fun to(): String
    fun from(): String
    fun send(): Boolean
}

class YamlSmtpConfig(private val to: String,
                     private val from: String,
                     private val send: Boolean) : SmtpConfig {

    override fun to() = to
    override fun from() = from
    override fun send() = send

}