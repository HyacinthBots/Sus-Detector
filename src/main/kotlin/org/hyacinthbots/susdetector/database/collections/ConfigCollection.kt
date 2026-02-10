package org.hyacinthbots.susdetector.database.collections

import com.mongodb.kotlin.client.model.Filters.eq
import com.mongodb.kotlin.client.model.Updates
import dev.kord.common.entity.Snowflake
import dev.kordex.core.koin.KordExKoinComponent
import org.hyacinthbots.susdetector.database.Collection
import org.hyacinthbots.susdetector.database.Database
import org.hyacinthbots.susdetector.database.entities.Config
import org.hyacinthbots.susdetector.database.findOne
import org.koin.core.component.inject

/**
 * Contains the functions for interacting with the [Config] database.
 */
class ConfigCollection : KordExKoinComponent {
    private val db: Database by inject()

    private val collection = db.database.getCollection<Config>(name)

    /**
     * Gets the config for a given Guild [id].
     *
     * @param id The ID of the guild to get the config for.
     * @return The [Config] object for the guild, or null if there isn't one
     */
    suspend fun get(id: Snowflake) = collection.findOne(Config::_id eq id)

    /**
     * Sets the [Config] for a guild.
     *
     * @param config The Config object to set.
     */
    suspend fun set(config: Config) = collection.insertOne(config)

    /**
     * Updates the [Config] for a given guild [id].
     *
     * @param id The ID of the guild to update the config for
     * @param detectionChannelId The new ID for the detection channel
     * @param actionLogId The new ID for the action log.
     */
    suspend fun update(id: Snowflake, detectionChannelId: Snowflake? = null, actionLogId: Snowflake? = null) {
        // If both are null, no changes are required
        if (detectionChannelId == null && actionLogId == null) return

        if (detectionChannelId != null) {
            collection.findOneAndUpdate(
                Config::_id eq id,
                Updates.set(Config::detectionChannelId, detectionChannelId)
            )
        }

        if (actionLogId != null) {
            collection.findOneAndUpdate(Config::_id eq id, Updates.set(Config::actionLogId, detectionChannelId))
        }
    }

    /**
     * Deletes a [Config] for a given guild [id].
     *
     * @param id The ID of the guild to delete the config for.
     */
    suspend fun delete(id: Snowflake) = collection.deleteOne(Config::_id eq id)

    companion object : Collection("config")
}
