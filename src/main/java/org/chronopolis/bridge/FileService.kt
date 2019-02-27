package org.chronopolis.bridge

import org.chronopolis.bridge.config.StorageConfig
import org.chronopolis.bridge.models.RestoreResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Class which is sort of not needed but handles staging of [RestoreTuple]s
 *
 * @since 1.0
 * @author shake
 */
class FileService(private val storageConfig: StorageConfig) {
    private val log: Logger = LoggerFactory.getLogger(FileService::class.java)

    /**
     * Using a [RestoreTuple], determine where data is located and stage a copy for the [Bridge]
     *
     * If either the directory for the Duracloud Bridge or Chronopolis Preservation does not exist,
     * a [RestoreResult.Error] will be returned. If any of the required files cannot have a link made, a
     * [RestoreResult.ErrorException] will be returned. When all operations are successful, a
     * [RestoreResult.Success] is returned.
     *
     * @param restoreTuple A tuple containing the Restoration and Snapshot information
     */
    fun stageForBridge(restoreTuple: RestoreTuple): RestoreResult {
        val restore = restoreTuple.restore
        val snapshot = restoreTuple.snapshot
        val duracloud = storageConfig.duracloud()
        val chronopolis = storageConfig.chronopolis()

        val restoreDirectory = duracloud.resolve(restore.restorationId)
        val preservationDirectory = chronopolis.resolve(snapshot.memberId).resolve(snapshot.name)

        if (!restoreDirectory.toFile().exists()) {
            log.error("[{}] {} does not exist!", restore.restorationId, restoreDirectory)
            return RestoreResult.Error(restoreTuple, "Directory $restoreDirectory does not exist")
        }

        if (!preservationDirectory.toFile().exists()) {
            log.error("[{}] {} does not exist!", restore.restorationId, preservationDirectory)
            return RestoreResult.Error(restoreTuple, "Directory $preservationDirectory does not exist")
        }

        return createLinks(restoreTuple, restoreDirectory, preservationDirectory)
    }

    private fun createLinks(restoreTuple: RestoreTuple,
                            restoreDirectory: Path,
                            preservationDirectory: Path): RestoreResult {
        // constants which we can just hold here maybe, probably allocates them on the stack
        val data = "data"
        val md5 = "manifest-md5.txt"
        val sha256 = "manifest-sha256.txt"
        val contentProperties = "content-properties.json"
        val collectionProperties = ".collection-snapshot.properties"

        // validate all preservation files exist, then map to Pairs?
        val files: Map<Path, Path> = mapOf(
                Pair(preservationDirectory.resolve(md5), restoreDirectory.resolve(md5)),
                Pair(preservationDirectory.resolve(sha256), restoreDirectory.resolve(sha256)),
                Pair(preservationDirectory.resolve(contentProperties), restoreDirectory.resolve(contentProperties)),
                Pair(preservationDirectory.resolve(collectionProperties), restoreDirectory.resolve(collectionProperties)),
                Pair(preservationDirectory.resolve(data), restoreDirectory.resolve(data))
        )

        val error: RestoreResult? = files.asSequence()
                .map { it ->
                    val errorMessage = "${it.key} does not exist!"
                    val exceptionMessage = "Exception staging file ${it.key} for Bridge"
                    try {
                        // might be a better way to do this but it works ok
                        if (it.key.toFile().exists()) {
                            Files.createSymbolicLink(it.value, it.key)
                            RestoreResult.Success(restoreTuple)
                        } else {
                            log.error("[{}] $errorMessage", restoreTuple.restore.restorationId)
                            val exception = IOException("${it.key} does not exist")
                            RestoreResult.ErrorException(restoreTuple, errorMessage, exception)
                        }
                    } catch (e: IOException) {
                        log.error("[{}] $exceptionMessage", restoreTuple.restore.restorationId, e)
                        RestoreResult.ErrorException(restoreTuple, exceptionMessage, e)
                    }
                }
                // short circuit if we result in an exception
                .firstOrNull { it is RestoreResult.ErrorException }

        // do we want to do any cleanup when an error is thrown?
        return when (error) {
            is RestoreResult.ErrorException -> error
            else -> RestoreResult.Success(restoreTuple)
        }
    }
}
