package org.chronopolis.bridge.config

import java.nio.file.Path
import java.nio.file.Paths

interface StorageConfig {
    fun duracloud(): Path
    fun chronopolis(): Path
}

/**
 * Config for storage. Expected to be posix for now.
 *
 * @since 1.0
 * @author shake
 */
class PropertiesStorageConfig() : StorageConfig {
    private val duracloud: Path
    private val chronopolis: Path

    init {
        val dcPath = System.getProperty("storage.duracloud")
        val chronPath = System.getProperty("storage.chronopolis")

        duracloud = Paths.get(dcPath)
        chronopolis = Paths.get(chronPath)

        // validatePath(duracloud)
        // validatePath(chronopolis)
    }

    override fun duracloud() = duracloud
    override fun chronopolis() = chronopolis
}