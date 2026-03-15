package org.hyacinthbots.susdetector.database.migrations

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.model.Filters.exists
import com.mongodb.kotlin.client.model.Updates
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.entities.Config
import kotlin.time.Duration.Companion.days

suspend fun v2(db: MongoDatabase) {
    with(db.getCollection<Config>(ConfigCollection.name)) {
        updateMany(Config::deleteDuration exists false, Updates.set(Config::deleteDuration, 3.days.inWholeSeconds))
    }
}
