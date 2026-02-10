package org.hyacinthbots.susdetector.extensions

import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.scheduling.Scheduler
import dev.kordex.core.utils.scheduling.Task
import org.hyacinthbots.susdetector.database.Cleanups
import org.hyacinthbots.susdetector.database.collections.GuildLeaveTimeCollection
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

class Utilities : Extension() {
    override val name: String
        get() = "utilities"

    private val scheduler = Scheduler()

    private lateinit var task: Task

    override suspend fun setup() {
        task = scheduler.schedule(1.days, repeat = true, callback = ::cleanup, name = "Cleanup task")

        event<GuildDeleteEvent> {
            action {
                GuildLeaveTimeCollection().set(event.guildId, Clock.System.now())
            }
        }

        event<GuildCreateEvent> {
            action {
                GuildLeaveTimeCollection().delete(event.guild.id)
            }
        }
    }

    suspend fun cleanup() {
        Cleanups.cleanup()
    }
}
