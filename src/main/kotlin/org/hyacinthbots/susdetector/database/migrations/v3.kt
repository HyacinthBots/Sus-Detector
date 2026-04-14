package org.hyacinthbots.susdetector.database.migrations

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.model.Filters.exists
import com.mongodb.kotlin.client.model.Updates
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.entities.Config
import kotlin.time.Duration.Companion.days

suspend fun v3(db: MongoDatabase) {
    with(db.getCollection<Config>(ConfigCollection.name)) {
        updateMany(Config::customDm exists false, Updates.set(Config::customDm, null))
    }
}
