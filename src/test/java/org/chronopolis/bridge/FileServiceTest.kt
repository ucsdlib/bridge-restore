package org.chronopolis.bridge

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.chronopolis.bridge.config.StorageConfig
import org.chronopolis.bridge.db.tables.records.RestorationRecord
import org.chronopolis.bridge.db.tables.records.SnapshotRecord
import org.chronopolis.bridge.models.Result
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

/**
 * Tests for the [FileService]
 *
 * Just basic success/failure stuff. Might update the duracloud root to be a temporary folder
 * instead of using target/duracloud/... because it tends to be messy.
 *
 * @author shake
 */
class FileServiceTest {

    private val member = "file-service-test"

    private val dcRoot = File("src/test/resources/duracloud").absoluteFile.toPath()
    private val chronRoot = File("src/test/resources/chronopolis").absoluteFile.toPath()
    private val storageConfig = TestStorageConfig(dcRoot, chronRoot)
    private val fileService = FileService(storageConfig)

    @Test
    fun stageSuccess() {
        // dc path = test-restore
        // chronopolis path = file-service-test/stage-success
        val restoreName = "test-restore"
        val snapshotName = "stage-success"

        val snapshot = SnapshotRecord()
        snapshot.name =  snapshotName
        snapshot.memberId = member

        val restoration = RestorationRecord()
        restoration.restorationId = restoreName

        val restoreTuple = RestoreTuple(restoration, snapshot)

        // finished setup, now test + assertions
        val result = fileService.stageForBridge(restoreTuple)

        assertThat(result).isInstanceOf(Result.Success::class.javaObjectType)

        val restoreRoot = dcRoot.resolve(restoreName)
        Assertions.assertThat(restoreRoot.resolve("data")).isDirectory()
        Assertions.assertThat(restoreRoot.resolve("data").resolve("hw")).isRegularFile()
        Assertions.assertThat(restoreRoot.resolve("manifest-md5.txt")).isRegularFile()
        Assertions.assertThat(restoreRoot.resolve("manifest-sha256.txt")).isRegularFile()
        Assertions.assertThat(restoreRoot.resolve(".collection-snapshot.properties")).isRegularFile()
        Assertions.assertThat(restoreRoot.resolve("content-properties.json")).isRegularFile()

        cleanup(restoreName)
    }

    @Test
    fun stageFileNotPreserved() {
        val restoreName = "test-restore"
        val snapshotName = "stage-missing"

        val snapshot = SnapshotRecord()
        snapshot.name =  snapshotName
        snapshot.memberId = member

        val restoration = RestorationRecord()
        restoration.restorationId = restoreName

        val restoreTuple = RestoreTuple(restoration, snapshot)

        // finished setup, now test + assertions
        val result = fileService.stageForBridge(restoreTuple)

        assertThat(result).isInstanceOf(Result.ErrorException::class.javaObjectType)
        cleanup(restoreName)
    }

    @Test
    fun stageRestoreDoesNotExist() {
        val restoreName = "test-restore-missing"
        val snapshotName = "stage-restore-not-exist"

        val snapshot = SnapshotRecord()
        snapshot.name =  snapshotName
        snapshot.memberId = member

        val restoration = RestorationRecord()
        restoration.restorationId = restoreName

        val restoreTuple = RestoreTuple(restoration, snapshot)

        // finished setup, now test + assertions
        val result = fileService.stageForBridge(restoreTuple)

        assertThat(result).isInstanceOf(Result.Error::class.javaObjectType)
    }

    @Test
    fun stagePreservationDoesNotExist() {
        val restoreName = "test-restore"
        val snapshotName = "stage-not-exist"

        val snapshot = SnapshotRecord()
        snapshot.name =  snapshotName
        snapshot.memberId = member

        val restoration = RestorationRecord()
        restoration.restorationId = restoreName

        val restoreTuple = RestoreTuple(restoration, snapshot)

        // finished setup, now test + assertions
        val result = fileService.stageForBridge(restoreTuple)

        assertThat(result).isInstanceOf(Result.Error::class.javaObjectType)
    }

    private fun cleanup(name: String) {
        dcRoot.resolve(name).resolve("data").toFile().delete()
        dcRoot.resolve(name).resolve("manifest-md5.txt").toFile().delete()
        dcRoot.resolve(name).resolve("manifest-sha256.txt").toFile().delete()
        dcRoot.resolve(name).resolve("content-properties.json").toFile().delete()
        dcRoot.resolve(name).resolve(".collection-snapshot.properties").toFile().delete()
    }
}

class TestStorageConfig(private val duracloud: Path,
                        private val chronopolis: Path) : StorageConfig {
    override fun duracloud() = duracloud
    override fun chronopolis() = chronopolis
}