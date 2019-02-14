package org.chronopolis.bridge.config

/**
 * Config for storage. Expected to be posix for now.
 *
 * @since 1.0
 * @author shake
 */
data class StorageConfig(val duracloud: String, val chronopolis: String)

/**
 * Config for smtp reporting. Only expose what we need to.
 *
 * @since 1.0
 * @author shake
 */
data class SmtpConfig(val to: String, val send: Boolean)

/**
 * Config for [Bridge] api access
 *
 * @since 1.0
 * @author shake
 */
data class DuracloudConfig(val bridgeEndpoint: String,
                           val bridgeUser: String,
                           val bridgePassword: String)

/**
 * Config for connecting to the [Bridge] database. Should be a read only user.
 *
 * @since 1.0
 * @author shake
 */
data class DbConfig(val url: String, val username: String, val password: String)
