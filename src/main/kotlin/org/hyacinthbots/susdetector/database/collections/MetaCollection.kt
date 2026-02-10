package org.hyacinthbots.susdetector.database.collections

import com.mongodb.kotlin.client.model.Filters.eq
import dev.kordex.core.koin.KordExKoinComponent
import org.hyacinthbots.susdetector.database.Collection
import org.hyacinthbots.susdetector.database.Database
import org.hyacinthbots.susdetector.database.entities.Meta
import org.hyacinthbots.susdetector.database.findOne
import org.koin.core.component.inject

class MetaCollection : KordExKoinComponent {
    private val db: Database by inject()
    private val collection = db.database.getCollection<Meta>(name)

    suspend fun get() = collection.findOne()

    suspend fun set(meta: Meta) = collection.insertOne(meta)

    suspend fun update(meta: Meta) = collection.findOneAndReplace(Meta::_id eq name, meta)

    companion object : Collection("meta")
}
