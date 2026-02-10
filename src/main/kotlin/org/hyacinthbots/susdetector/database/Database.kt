package org.hyacinthbots.susdetector.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.UuidRepresentation
import org.hyacinthbots.susdetector.utils.MONGO_URI

class Database {
    /** The connection settings for the database. */
    private val settings = MongoClientSettings
        .builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(ConnectionString(MONGO_URI))
        .build()

    /** The mongo client with the relevant [settings] applied. */
    private val client = MongoClient.create(settings)

    /** The main database for the bot. */
    @Suppress("MemberNameEqualsClassName")
    val database get() = client.getDatabase("Sus-Detector")

    /**
     * Migrates the database to newer versions.
     */
    suspend fun migrate() {
        Migrator.migrate()
    }
}
