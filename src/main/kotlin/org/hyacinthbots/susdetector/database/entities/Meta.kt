package org.hyacinthbots.susdetector.database.entities

import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity

/**
 * The metadata for the Database.
 *
 * @property version The current database version
 * @property _id The identifier for the document. Will never change.
 */
@Serializable
data class Meta(
    val version: Int,

    override val _id: String = "meta"
) : Entity<String>
