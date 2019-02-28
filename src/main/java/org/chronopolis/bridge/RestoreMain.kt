package org.chronopolis.bridge

import org.chronopolis.bridge.config.DbConfig
import org.chronopolis.bridge.config.DuracloudConfig
import org.chronopolis.bridge.config.SmtpConfig
import org.chronopolis.bridge.config.StorageConfig
import org.chronopolis.bridge.config.configModule
import org.chronopolis.bridge.models.RestoreResult
import org.kodein.di.DKodeinAware
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {
    Application()
}

class Application : DKodeinAware {
    private val log: Logger = LoggerFactory.getLogger(Application::class.java)

    override val dkodein = Kodein.direct {
        // could we also have something which is initialized which binds values read from a file
        // to system properties? probably overkill at the moment but could be fun.
        import(configModule)

        bind<SmtpSender>() with singleton { SmtpSender(instance()) }
        bind<FileService>() with singleton { FileService(instance()) }
        bind<ReportingService>() with singleton { ReportingService(instance()) }
        bind<BridgeNotification>() with singleton { BridgeNotification(instance()) }
    }

    private val dbConfig: DbConfig = instance()
    private val duracloudConfig: DuracloudConfig = instance()
    private val smtpConfig: SmtpConfig = instance()
    private val storageConfig: StorageConfig = instance()

    private val fileService: FileService = instance()
    private val reportingService: ReportingService = instance()
    private val bridgeNotification: BridgeNotification = instance()

    init {
        log.info("Restore Client Version {}", Application::class.java.`package`.implementationVersion)
        printConfig()

        val results = fetchRestorations(dbConfig).asSequence()
                .map(fileService::stageForBridge)
                .map { it: RestoreResult ->
                    when (it) {
                        is RestoreResult.Success -> bridgeNotification.notify(it)
                        else -> it
                    }
                }

        reportingService.report(results)
        log.info("Closing Restore client")
    }

    private fun printConfig() {
        log.debug("Loaded configuration: ")
        log.debug("  db.url: ${dbConfig.username()}")
        log.debug("  db.username: ${dbConfig.username()}")
        log.debug("  db.password: [scrubbed]")
        log.debug("  duracloud.endpoint: ${duracloudConfig.bridgeEndpoint()}")
        log.debug("  duracloud.username: ${duracloudConfig.bridgeUsername()}")
        log.debug("  duracloud.password: [scrubbed]")
        log.debug("  smtp.to: ${smtpConfig.to()}")
        log.debug("  smtp.from: ${smtpConfig.from()}")
        log.debug("  smtp.send: ${smtpConfig.send()}")
        log.debug("  storage.duracloud: ${storageConfig.duracloud()}")
        log.debug("  storage.chronopolis: ${storageConfig.chronopolis()}")
    }
}