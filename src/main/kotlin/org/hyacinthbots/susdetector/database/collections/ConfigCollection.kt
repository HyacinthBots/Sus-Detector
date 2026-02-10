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

class ConfigCollection : KordExKoinComponent {
    private val db: Database by inject()

    private val collection = db.database.getCollection<Config>(name)

    suspend fun get(id: Snowflake) = collection.findOne(Config::_id eq id)

    suspend fun set(config: Config) = collection.insertOne(config)

    suspend fun update(id: Snowflake, detectionChannelId: Snowflake? = null, actionLogId: Snowflake? = null) {
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

    suspend fun delete(id: Snowflake) = collection.deleteOne(Config::_id eq id)

    companion object : Collection("config")
}
