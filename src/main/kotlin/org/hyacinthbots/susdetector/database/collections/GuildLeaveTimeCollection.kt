package org.hyacinthbots.susdetector.database.collections

import com.mongodb.kotlin.client.model.Filters.eq
import dev.kord.common.entity.Snowflake
import dev.kordex.core.koin.KordExKoinComponent
import org.hyacinthbots.susdetector.database.Collection
import org.hyacinthbots.susdetector.database.Database
import org.hyacinthbots.susdetector.database.entities.GuildLeaveTime
import org.koin.core.component.inject
import kotlin.time.Instant

/**
 * Contains the functions for interacting with the [GuildLeaveTime] database.
 */
class GuildLeaveTimeCollection : KordExKoinComponent {
    private val db: Database by inject()
    private val collection = db.database.getCollection<GuildLeaveTime>(name)

    /**
     * Adds the time Sus Detector left a guild with a config.
     *
     * @param guildId The ID of the guild that was left.
     * @param time The current time
     */
    suspend fun set(guildId: Snowflake, time: Instant) =
        collection.insertOne(GuildLeaveTime(guildId, time))

    /**
     * Deletes a leave time from the database.
     *
     * @param guildId The ID of the guild to delete the data for
     */
    suspend fun delete(guildId: Snowflake) =
        collection.deleteOne(GuildLeaveTime::_id eq guildId)

    companion object : Collection("guild-leave-time")
}
