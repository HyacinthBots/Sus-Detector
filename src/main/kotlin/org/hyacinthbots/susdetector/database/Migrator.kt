package org.hyacinthbots.susdetector.database

import dev.kordex.core.koin.KordExKoinComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.hyacinthbots.susdetector.database.collections.MetaCollection
import org.hyacinthbots.susdetector.database.entities.Meta
import org.hyacinthbots.susdetector.database.migrations.v1
import org.hyacinthbots.susdetector.database.migrations.v2
import org.hyacinthbots.susdetector.database.migrations.v3
import org.koin.core.component.inject

object Migrator : KordExKoinComponent {
    private val logger = KotlinLogging.logger("Migration logger")

    val db: Database by inject()
    private val metaCollection: MetaCollection by inject()

    suspend fun migrate() {
        logger.info { "Starting database migration" }

        var meta = metaCollection.get()

        if (meta == null) {
            meta = Meta(0)
            metaCollection.set(meta)
        }

        var currentVersion = meta.version

        logger.info { "Current database version: v$currentVersion" }

        while (true) {
            val nextVersion = currentVersion + 1

            @Suppress("TooGenericExceptionCaught")
            try {
                @Suppress("UseIfInsteadOfWhen")
                when (nextVersion) {
                    1 -> ::v1
                    2 -> ::v2
                    3 -> ::v3
                    else -> break
                }(db.database)

                logger.info { "Migrated database to version $nextVersion" }
            } catch (t: Throwable) {
                logger
                throw t
            }

            currentVersion = nextVersion
        }

        if (currentVersion != meta.version) {
            meta = meta.copy(version = currentVersion)

            metaCollection.update(meta)

            logger.info { "Finished database migrations." }
        }
    }
}
