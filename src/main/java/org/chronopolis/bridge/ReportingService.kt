package org.chronopolis.bridge

import org.chronopolis.bridge.models.Result
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

    /**
     * Create and send a report of operations for the current run
     *
     * @param results a list of [Result]s to check and report on
     */
    fun report(results: Sequence<Result>) {
        val prefix = "Report for ${LocalDateTime.now()}\n"
        val body = results.joinToString(
                separator = "\n------------------------------------------------------------\n",
                transform = Result::message)

        if (body.isNotBlank()) {
            smtpSender.send(prefix, body)
        }
    }

}