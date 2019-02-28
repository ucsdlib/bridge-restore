package org.chronopolis.bridge.config

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.chronopolis.bridge.Bridge
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.Base64

/**
 * Config for [Bridge] api access
 *
 * @since 1.0
 * @author shake
 */
interface DuracloudConfig {
    fun bridge(): Bridge
    fun bridgeEndpoint(): String
    fun bridgeUsername(): String
    fun bridgePassword(): String
}

class YamlDuracloudConfig(private val endpoint: String,
                          private val username: String,
                          private val password: String) : DuracloudConfig {

    override fun bridge(): Bridge {
        val client = OkHttpClient.Builder()
                .addInterceptor { it: Interceptor.Chain ->
                    val credentials = "$username:$password"
                    val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
                    val basic = "Basic $encoded"

                    val request = it.request().newBuilder().header("Authorization", basic).build()
                    it.proceed(request)
                }
                .build()
        val retrofit = Retrofit.Builder().baseUrl(endpoint)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()

        return retrofit.create(Bridge::class.java)
    }

    override fun bridgeEndpoint() = endpoint
    override fun bridgeUsername() = username
    override fun bridgePassword() = password

}