package org.hyacinthbots.susdetector.database

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.model.Filters.and
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson

/**
 * Finds all the documents in the collection.
 */
fun <T : Any> MongoCollection<T>.find(vararg filters: Bson?) =
    if (filters.isNotEmpty()) {
        find(and(*filters))
    } else {
        find()
    }

/**
 * Finds the first document that matches the [filter] in the collection. If no documents match, null is returned.
 */
suspend fun <T : Any> MongoCollection<T>.findOne(filter: Bson): T? = find(filter).firstOrNull()

/**
 * Finds the first document that matches the [filters] in the collection. If no documents match, null is returned.
 */
suspend fun <T : Any> MongoCollection<T>.findOne(vararg filters: Bson?): T? = find(*filters).firstOrNull()
