package org.hyacinthbots.susdetector.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.hyacinthbots.susdetector.database.Entity

/**
 * The configuration data for Sus Detector.
 *
 * @property _id The ID of the Guild the configuration is for.
 * @property detectionChannelId The ID of the channel to detect messages in
 * @property actionLogId The ID of the channel to log actions too.
 * @property deleteDuration The duration of previous messages to delete.
 */
@Serializable
data class Config(
    override val _id: Snowflake,

    val detectionChannelId: Snowflake,
    val actionLogId: Snowflake,
    val deleteDuration: Long,
) : Entity<Snowflake>
