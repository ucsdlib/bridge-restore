package org.chronopolis.bridge

import org.chronopolis.bridge.config.StorageConfig
import org.chronopolis.bridge.models.Result
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

    /**
     * Using a [RestoreTuple], determine where data is located and stage a copy for the [Bridge]
     *
     * If either the directory for the Duracloud Bridge or Chronopolis Preservation does not exist,
     * a [Result.Error] will be returned. If any of the required files cannot have a link made, a
     * [Result.ErrorException] will be returned. When all operations are successful, a
     * [Result.Success] is returned.
     *
     * @param restoreTuple A tuple containing the Restoration and Snapshot information
     */
    fun stageForBridge(restoreTuple: RestoreTuple): Result {
        val restore = restoreTuple.restore
        val snapshot = restoreTuple.snapshot
        val duracloud = storageConfig.duracloud()
        val chronopolis = storageConfig.chronopolis()

        val restoreDirectory = duracloud.resolve(restore.restorationId)
        val preservationDirectory = chronopolis.resolve(snapshot.memberId).resolve(snapshot.name)

        if (!restoreDirectory.toFile().exists()) {
            return Result.Error(restoreTuple, "Directory $restoreDirectory does not exist")
        }

        if (!preservationDirectory.toFile().exists()) {
            return Result.Error(restoreTuple, "Directory $preservationDirectory does not exist")
        }

        return createLinks(restoreTuple, restoreDirectory, preservationDirectory)
    }

    private fun createLinks(restoreTuple: RestoreTuple,
                            restoreDirectory: Path,
                            preservationDirectory: Path): Result {
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

        val error: Result? = files.asSequence()
                .map { it ->
                    val errorMessage = "Exception staging file ${it.key} for Bridge"
                    try {
                        // might be a better way to do this but it works ok
                        if (it.key.toFile().exists()) {
                            Files.createSymbolicLink(it.value, it.key)
                            Result.Success(restoreTuple)
                        } else {
                            val exception = IOException("${it.key} does not exist")
                            Result.ErrorException(restoreTuple, errorMessage, exception)
                        }
                    } catch (e: IOException) {
                        Result.ErrorException(restoreTuple, errorMessage, e)
                    }
                }
                // short circuit if we result in an exception
                .firstOrNull { it is Result.ErrorException }

        // do we want to do any cleanup when an error is thrown?
        return when (error) {
            is Result.ErrorException -> error
            else -> Result.Success(restoreTuple)
        }
    }
}
