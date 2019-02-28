package org.chronopolis.bridge.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

data class ConfigDto(val db: YamlDbConfig,
                     val log: LoggingConfig,
                     val smtp: YamlSmtpConfig,
                     val storage: YamlStorageConfig,
                     val bridge: YamlDuracloudConfig)

interface Validated {
    fun validate()
}

interface RestoreConfig {
    fun dbConfig(): DbConfig
    fun smtpConfig(): SmtpConfig
    fun loggingConfig(): LoggingConfig
    fun storageConfig(): StorageConfig
    fun duracloudConfig(): DuracloudConfig
}

class YamlConfig() : RestoreConfig {
    private val dto: ConfigDto

    init {
        val default = "/usr/local/chronopolis/bridge-restore/restore.yaml"
        val path = System.getProperty("restore.config") ?: default
        val input = File(path)

        if (!input.exists()) {
            throw IllegalStateException("$path must exist for config to be loaded!")
        }

        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        dto = mapper.readValue(input.bufferedReader(), ConfigDto::class.java)
    }

    override fun dbConfig() = dto.db
    override fun smtpConfig() = dto.smtp
    override fun loggingConfig() = dto.log
    override fun storageConfig() = dto.storage
    override fun duracloudConfig() = dto.bridge
}
