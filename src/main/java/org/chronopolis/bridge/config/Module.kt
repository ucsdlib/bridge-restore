package org.chronopolis.bridge.config

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

val configModule = Kodein.Module("configModule") {
    bind<RestoreConfig>() with singleton { YamlConfig() }
    bind<DbConfig>() with provider { instance<RestoreConfig>().dbConfig() }
    bind<SmtpConfig>() with provider { instance<RestoreConfig>().smtpConfig() }
    bind<StorageConfig>() with provider { instance<RestoreConfig>().storageConfig() }
    bind<DuracloudConfig>() with provider { instance<RestoreConfig>().duracloudConfig() }

    onReady {
        val dbConfig = instance<DbConfig>()
        dbConfig.validate()

        val storageConfig = instance<StorageConfig>()
        storageConfig.validate()
    }
}

