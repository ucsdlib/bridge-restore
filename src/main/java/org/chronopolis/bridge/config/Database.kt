package org.chronopolis.bridge.config


interface DbConfig {
    fun url(): String
    fun username(): String
    fun password(): String
}

/**
 * Config for connecting to the [Bridge] database. Should be a read only user.
 *
 * @since 1.0
 * @author shake
 */
class PropertiesDbConfig() : DbConfig {
    private val url: String
    private val username: String
    private val password: String

    init {
        url = System.getProperty("db.url")
        username = System.getProperty("db.username")
        password = System.getProperty("db.password")
    }

    override fun url(): String {
        TODO("not implemented")
    }

    override fun username(): String {
        TODO("not implemented")
    }

    override fun password(): String {
        TODO("not implemented")
    }

}