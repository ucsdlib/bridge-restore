package org.chronopolis.bridge

import org.chronopolis.bridge.models.RestoreResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Just send smtp messages ok
 *
 * Still need: smtp settings
 *
 * @author shake
 * @since 1.0
 */
class ReportingService(private val smtpSender: SmtpSender) {
    private val log: Logger = LoggerFactory.getLogger(ReportingService::class.java)

    /**
     * Create and send a report of operations for the current run
     *
     * @param results a list of [RestoreResult]s to check and report on
     */
    fun report(results: Sequence<RestoreResult>) {
        val prefix = "Report for ${LocalDateTime.now()}\n"
        val body = results.joinToString(
                separator = "\n------------------------------------------------------------\n",
                transform = RestoreResult::message)

        if (body.isNotBlank()) {
            smtpSender.send(prefix, body)
        } else {
            log.debug("Skipping result reporting")
        }
    }

}