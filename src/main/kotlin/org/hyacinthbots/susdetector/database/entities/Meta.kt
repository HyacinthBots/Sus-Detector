package org.hyacinthbots.susdetector.database.entities

import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity

@Serializable
data class Meta(
    val version: Int,

    override val _id: String = "meta" // Will never change
) : Entity<String>
