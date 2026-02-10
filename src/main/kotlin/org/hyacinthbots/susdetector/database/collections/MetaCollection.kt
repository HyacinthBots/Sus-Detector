package org.hyacinthbots.susdetector.database.collections

import com.mongodb.kotlin.client.model.Filters.eq
import dev.kordex.core.koin.KordExKoinComponent
import org.hyacinthbots.susdetector.database.Collection
import org.hyacinthbots.susdetector.database.Database
import org.hyacinthbots.susdetector.database.entities.Meta
import org.hyacinthbots.susdetector.database.findOne
import org.koin.core.component.inject

/**
 * Contains the functions for interacting with the [Meta] database.
 */
class MetaCollection : KordExKoinComponent {
    private val db: Database by inject()
    private val collection = db.database.getCollection<Meta>(name)

    /** Finds the document in the collection. There should only ever be one document. */
    suspend fun get() = collection.findOne()

    /** Adds [meta]data record to the database.  */
    suspend fun set(meta: Meta) = collection.insertOne(meta)

    /** Updates the current metadata with new [meta]. */
    suspend fun update(meta: Meta) = collection.findOneAndReplace(Meta::_id eq name, meta)

    companion object : Collection("meta")
}
