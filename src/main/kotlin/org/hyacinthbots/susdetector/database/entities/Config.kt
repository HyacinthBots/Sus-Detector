package org.hyacinthbots.susdetector.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity

@Serializable
data class Config(
    /** The ID of the guild this config is for. */
    override val _id: Snowflake,

    val detectionChannelId: Snowflake,
    val actionLogId: Snowflake,
) : Entity<Snowflake>
