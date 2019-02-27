package org.chronopolis.bridge

import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.chronopolis.bridge.config.DuracloudConfig
import org.chronopolis.bridge.db.tables.records.RestorationRecord
import org.chronopolis.bridge.db.tables.records.SnapshotRecord
import org.chronopolis.bridge.models.BridgeStatus
import org.chronopolis.bridge.models.RestoreComplete
import org.chronopolis.bridge.models.RestoreId
import org.chronopolis.bridge.models.RestoreResult
import org.chronopolis.test.support.CallWrapper
import org.chronopolis.test.support.ErrorCallWrapper
import org.chronopolis.test.support.ExceptingCallWrapper
import org.junit.jupiter.api.Test

class BridgeNotificationTest {
    private val bridgeMock: Bridge = mockk()
    private val configMock: DuracloudConfig = mockk()
    private val notifier = BridgeNotification(configMock)
    private val details = "details"

    @Test
    fun successfulCallReturnsSuccess() {
        val id = RestoreId("bridge-notification-success")
        val rc = RestoreComplete(BridgeStatus.RESTORATION_COMPLETE, details)
        every { configMock.bridge() } returns bridgeMock

        // return CallWrapper<RestoreComplete>
        every { bridgeMock.completeRestoreById(id) } returns CallWrapper(rc)

        val snapshot = SnapshotRecord()
        val restoration = RestorationRecord()
        restoration.restorationId = id.id
        val result = RestoreResult.Success(RestoreTuple(restoration, snapshot))
        notifier.notify(result)

        verify(exactly = 1) { configMock.bridge() }
        verify(exactly = 1) { bridgeMock.completeRestoreById(id) }
        confirmVerified(configMock, bridgeMock)
        clearMocks(configMock, bridgeMock)
    }

    @Test
    fun httpErrorReturnsError() {
        val id = RestoreId("bridge-notification-exception")
        val rc = RestoreComplete(BridgeStatus.CANCELLED, details)

        every { configMock.bridge() } returns bridgeMock
        every { bridgeMock.completeRestoreById(id) } returns ErrorCallWrapper(rc, 401, "unauth")

        val snapshot = SnapshotRecord()
        val restoration = RestorationRecord()
        restoration.restorationId = id.id
        val result = RestoreResult.Success(RestoreTuple(restoration, snapshot))
        notifier.notify(result)

        verify(exactly = 1) { configMock.bridge() }
        verify(exactly = 1) { bridgeMock.completeRestoreById(id) }
        confirmVerified(configMock, bridgeMock)
        clearMocks(configMock, bridgeMock)
    }

    @Test
    fun exceptedCallReturnsException() {

        val id = RestoreId("bridge-notification-exception")
        val rc = RestoreComplete(BridgeStatus.RETRIEVING_FROM_STORAGE, details)

        every { configMock.bridge() } returns bridgeMock
        every { bridgeMock.completeRestoreById(id) } returns ExceptingCallWrapper(rc)

        val snapshot = SnapshotRecord()
        val restoration = RestorationRecord()
        restoration.restorationId = id.id
        val result = RestoreResult.Success(RestoreTuple(restoration, snapshot))
        notifier.notify(result)

        verify(exactly = 1) { configMock.bridge() }
        verify(exactly = 1) { bridgeMock.completeRestoreById(id) }
        confirmVerified(configMock, bridgeMock)
        clearMocks(configMock, bridgeMock)
    }

}
