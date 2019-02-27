package org.chronopolis.bridge

import org.chronopolis.bridge.config.DbConfig
import org.chronopolis.bridge.config.configModule
import org.chronopolis.bridge.models.RestoreResult
import org.kodein.di.DKodeinAware
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


fun main(args: Array<String>) {
    Application()
}

class Application : DKodeinAware {
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
    private val fileService: FileService = instance()
    private val reportingService: ReportingService = instance()
    private val bridgeNotification: BridgeNotification = instance()

    init {
        println("Restore Client version whatever")
        val results = fetchRestorations(dbConfig).asSequence()
                .map(fileService::stageForBridge)
                .map { it: RestoreResult ->
                    when (it) {
                        is RestoreResult.Success -> bridgeNotification.notify(it)
                        else -> it
                    }
                }

        reportingService.report(results)
    }
}