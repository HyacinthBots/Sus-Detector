package org.hyacinthbots.susdetector.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity
import kotlin.time.Instant

@Serializable
data class GuildLeaveTime(
    override val _id: Snowflake,
    val guildLeaveTime: Instant
) : Entity<Snowflake>
