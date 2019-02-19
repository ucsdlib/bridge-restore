package org.chronopolis.bridge.config

interface SmtpConfig {
    fun to(): String
    fun send(): Boolean
}

/**
 * Config for smtp reporting. Only expose what we need to.
 *
 * @since 1.0
 * @author shake
 */
class PropertiesSmtpConfig() : SmtpConfig {

    private val to: String
    private val send: Boolean

    init {
        to = System.getProperty("smtp.to")
        val sendString = System.getProperty("smtp.send")
        send = sendString.toBoolean()
    }

    override fun to() = to
    override fun send() = send

}