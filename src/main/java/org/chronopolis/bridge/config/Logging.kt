package org.chronopolis.bridge.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.util.FileSize
import org.slf4j.impl.StaticLoggerBinder


/**
 * Configuration for logging, programmatic instead of through logback.xml
 *
 * @since 1.0
 * @author shake
 */
data class LoggingConfig(private val file: String, private val level: String) {
    fun setup() {
        val lc: LoggerContext = StaticLoggerBinder.getSingleton().loggerFactory as LoggerContext

        val appender = RollingFileAppender<ILoggingEvent>()
        appender.context = lc
        appender.file = file

        val ple = PatternLayoutEncoder()
        ple.context = lc
        ple.pattern = "%d{yyyy/MM/dd HH:mm:ss} %p %C{5} : %m%n"
        appender.encoder = ple

        val triggeringPolicy = SizeBasedTriggeringPolicy<ILoggingEvent>()
        triggeringPolicy.context = lc
        triggeringPolicy.setMaxFileSize(FileSize.valueOf("20MB"))
        appender.triggeringPolicy = triggeringPolicy

        val rollingPolicy = FixedWindowRollingPolicy()
        rollingPolicy.context = lc
        rollingPolicy.maxIndex = 5
        rollingPolicy.setParent(appender)
        rollingPolicy.fileNamePattern = "$file.%i"
        appender.rollingPolicy = rollingPolicy

        ple.start()
        rollingPolicy.start()
        triggeringPolicy.start()
        appender.start()

        val log = lc.getLogger("ROOT")
        log.level = Level.toLevel(level)
        log.addAppender(appender)
    }
}

