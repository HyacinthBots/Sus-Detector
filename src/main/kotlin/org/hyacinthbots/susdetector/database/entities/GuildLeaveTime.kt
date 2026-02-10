package org.hyacinthbots.susdetector.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity
import kotlin.time.Instant

/**
 * The data for when Sus Detector leaves a Guild.
 *
 * @property _id The ID of the Guild that was left.
 * @property guildLeaveTime The [Instant] the guild was left.
 */
@Serializable
data class GuildLeaveTime(
    override val _id: Snowflake,

    val guildLeaveTime: Instant
) : Entity<Snowflake>
