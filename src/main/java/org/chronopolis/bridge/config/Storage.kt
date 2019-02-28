package org.chronopolis.bridge.config

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Config for storage. Expected to be posix for now.
 *
 * @since 1.0
 * @author shake
 */
interface StorageConfig : Validated {
    fun duracloud(): Path
    fun chronopolis(): Path
}

class YamlStorageConfig(private val duracloud: String,
                        private val chronopolis: String) : StorageConfig {
    override fun duracloud() = Paths.get(duracloud)
    override fun chronopolis() = Paths.get(chronopolis)

    override fun validate() {
        val dc = duracloud().toFile()
        if (!dc.exists() || !dc.isDirectory) {
            throw IllegalStateException("$dc must be a directory")
        }
        if (!dc.canRead() || !dc.canWrite()) {
            throw java.lang.IllegalStateException("$dc is not rw")
        }

        val chron = chronopolis().toFile()
        if (!chron.isFile) {
            throw java.lang.IllegalStateException("$chron must be a directory")
        }
        if (!chron.canRead()) {
            throw java.lang.IllegalStateException("$chron not able to be read from")
        }
    }
}