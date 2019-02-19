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

    init {

    }

    override fun to(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun send(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}