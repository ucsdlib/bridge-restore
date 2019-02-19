package org.chronopolis.bridge

import org.chronopolis.bridge.config.SmtpConfig
import org.chronopolis.bridge.models.Result
import java.time.ZonedDateTime
import java.util.Properties
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.MimeMessage

/**
 * Just send smtp messages ok
 *
 * Still need: smtp settings
 *
 * @author shake
 * @since 1.0
 */
class ReportingService(val smtpConfig: SmtpConfig) {

    /**
     * Create and send a report of operations for the current run
     *
     * @param results a list of [Result]s to check and report on
     */
    fun report(results: Sequence<Result>) {
        val body = results.joinToString(
                prefix = "Report for ${ZonedDateTime.now().toLocalDate()}\n",
                separator = "\n------------------------------------------------------------\n",
                transform = Result::message)

        if (body.isNotBlank()) {
            send(body)
        }
    }

    private fun send(body: String) {
        // push to property?
        val smtpFrom = "chronopolis-mail@umiacs.umd.edu"

        val properties = Properties()
        properties["mail.smtp.host"] = "localhost.localdomain"
        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        message.setFrom(smtpFrom)
        message.setRecipients(Message.RecipientType.TO, smtpConfig.to())

        message.subject = "Duracloud Bridge Restoration Report"
        message.setContent(body, "text/plain")
        Transport.send(message)
    }

}