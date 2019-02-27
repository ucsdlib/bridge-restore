package org.chronopolis.bridge

import io.mockk.Called
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chronopolis.bridge.db.tables.records.RestorationRecord
import org.chronopolis.bridge.db.tables.records.SnapshotRecord
import org.chronopolis.bridge.models.RestoreResult
import org.junit.jupiter.api.Test

class ReportingServiceTest {

    private val sender: SmtpSender = mockk()
    private val reportingService = ReportingService(sender)

    @Test
    fun sendWithBody() {
        val restoration = RestorationRecord()
        restoration.restorationId = "send-with-body"
        val snapshot = SnapshotRecord()
        val tuple = RestoreTuple(restoration, snapshot)

        every { sender.send(any(), any()) } just Runs
        
        reportingService.report(sequenceOf(RestoreResult.Success(tuple)))

        verify(exactly = 1) { sender.send(any(), any()) }
        confirmVerified(sender)
    }

    @Test
    fun sendEmptyBody() {
        reportingService.report(emptySequence())

        verify { sender wasNot Called }
        confirmVerified(sender)
    }

}