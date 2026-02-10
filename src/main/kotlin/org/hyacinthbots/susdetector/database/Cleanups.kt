package org.hyacinthbots.susdetector.database

import dev.kordex.core.koin.KordExKoinComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.toSet
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.collections.GuildLeaveTimeCollection
import org.hyacinthbots.susdetector.database.entities.GuildLeaveTime
import org.koin.core.component.inject
import kotlin.time.Clock

object Cleanups : KordExKoinComponent {
    private val db: Database by inject()

    private val leaveTimeCollection = db.database.getCollection<GuildLeaveTime>(GuildLeaveTimeCollection.name)

    private val logger = KotlinLogging.logger("Database cleanups")

    suspend fun cleanup() {
        logger.info { "Starting bot cleanup..." }

        val leaveTimeData = leaveTimeCollection.find().toSet()
        var deletedData = 0
        val now = Clock.System.now()

        leaveTimeData.forEach {
            val leaveInterval = now - it.guildLeaveTime

            if (leaveInterval.inWholeDays >= 30) {
                ConfigCollection().delete(it._id)
                GuildLeaveTimeCollection().delete(it._id)
                deletedData++
            }
        }

        logger.info { "Deleted old data for $deletedData guilds" }
    }
}
