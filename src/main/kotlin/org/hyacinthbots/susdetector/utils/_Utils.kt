package org.hyacinthbots.susdetector.utils

import dev.kordex.core.builders.ExtensibleBotBuilder
import dev.kordex.core.utils.loadModule
import org.hyacinthbots.susdetector.database.Database
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.collections.GuildLeaveTimeCollection
import org.hyacinthbots.susdetector.database.collections.MetaCollection
import org.koin.dsl.bind


fun String.trimmedContents(): String =
    if (this.length > 1024) {
        this.substring(0, 1021) + "..."
    } else {
        this
    }

suspend fun ExtensibleBotBuilder.database(migrate: Boolean) {
    val db = Database()

    hooks {
        beforeKoinSetup {
            loadModule {
                single { db } bind Database::class
            }

            loadModule {
                single { ConfigCollection() } bind ConfigCollection::class
                single { GuildLeaveTimeCollection() } bind GuildLeaveTimeCollection::class
                single { MetaCollection() } bind MetaCollection::class
            }
        }

        if (migrate) {
            db.migrate()
        }
    }
}
