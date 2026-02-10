package org.hyacinthbots.susdetector.database

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.model.Filters.and
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson

fun <T : Any> MongoCollection<T>.find(vararg filters: Bson?) = find(and(*filters))

suspend fun <T : Any> MongoCollection<T>.findOne(filter: Bson): T? = find(filter).firstOrNull()

suspend fun <T : Any> MongoCollection<T>.findOne(vararg filters: Bson?): T? = find(*filters).firstOrNull()
