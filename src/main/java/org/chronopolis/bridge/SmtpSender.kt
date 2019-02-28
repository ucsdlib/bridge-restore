package org.chronopolis.bridge

import org.chronopolis.bridge.config.SmtpConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.Properties
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.MimeMessage

/**
 * Wrapper around send so that we can mock so we don't send
 * any emails when testing
 *
 * @since 1.0
 * @author shake
 */
class SmtpSender(private val smtpConfig: SmtpConfig) {
    private val log: Logger = LoggerFactory.getLogger(SmtpSender::class.java)
    private val title = "Duracloud Bridge Restoration Report"

    fun send(prefix: String, body: String) {
        log.info("Sending report for run on ${LocalDateTime.now()}")
        val smtpFrom = smtpConfig.from()

        val properties = Properties()
        properties["mail.smtp.host"] = "localhost.localdomain"
        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        message.setFrom(smtpFrom)
        message.setRecipients(Message.RecipientType.TO, smtpConfig.to())

        message.subject = title
        message.setContent(prefix + body, "text/plain")
        Transport.send(message)
    }

}