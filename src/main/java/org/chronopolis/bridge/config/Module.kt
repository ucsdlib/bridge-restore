package org.chronopolis.bridge.config

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

val configModule = Kodein.Module("configModule") {
    bind<DbConfig>() with singleton { PropertiesDbConfig() }
    bind<SmtpConfig>() with singleton { PropertiesSmtpConfig() }
    bind<StorageConfig>() with singleton { PropertiesStorageConfig() }
    bind<DuracloudConfig>() with singleton { PropertiesDuracloudConfig() }
}

