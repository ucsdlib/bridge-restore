package org.chronopolis.bridge.config

import org.jooq.Record1
import org.jooq.Result
import org.jooq.impl.DSL

/**
 * Config for connecting to the [Bridge] database. Should be a read only user.
 *
 * @since 1.0
 * @author shake
 */
interface DbConfig : Validated {
    fun url(): String
    fun username(): String
    fun password(): String
}

class YamlDbConfig(private val url: String,
                   private val username: String,
                   private val password: String) : DbConfig {
    override fun url() = url
    override fun username() = username
    override fun password() = password

    override fun validate() {
        val result: Result<Record1<Int>> = DSL.using(url, username, password).use { ctx ->
            ctx.selectOne().fetch()
        }

        if (result.isEmpty()) {
            throw IllegalStateException("Unable to connect to bridge database")
        }
    }
}