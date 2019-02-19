package org.chronopolis.bridge.config

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.chronopolis.bridge.Bridge
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


interface DuracloudConfig {
    fun bridge(): Bridge
    fun bridgeEndpoint(): String
    fun bridgeUsername(): String
    fun bridgePassword(): String
}

/**
 * Config for [Bridge] api access
 *
 * @since 1.0
 * @author shake
 */
class PropertiesDuracloudConfig() : DuracloudConfig {
    private val endpoint: String;
    private val username: String;
    private val password: String;

    init {
        endpoint = System.getProperty("duracloud.bridge.endpoint")
        username = System.getProperty("duracloud.bridge.username")
        password = System.getProperty("duracloud.bridge.password")
    }

    override fun bridge(): Bridge {
        val client = OkHttpClient.Builder()
                .addInterceptor { it: Interceptor.Chain ->
                    it.proceed(it.request())
                }
                .build()
        val retrofit = Retrofit.Builder().baseUrl(endpoint)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        return retrofit.create(Bridge::class.java)
    }

    override fun bridgeEndpoint() = endpoint
    override fun bridgeUsername() = username
    override fun bridgePassword() = password

}