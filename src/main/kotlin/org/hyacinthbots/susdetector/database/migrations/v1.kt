package org.hyacinthbots.susdetector.database.migrations

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.collections.GuildLeaveTimeCollection

suspend fun v1(db: MongoDatabase) {
    db.createCollection(ConfigCollection.name)
    db.createCollection(GuildLeaveTimeCollection.name)
}
