package org.hyacinthbots.susdetector.extensions

import dev.kordex.core.extensions.Extension
import dev.kordex.core.utils.scheduling.Scheduler
import dev.kordex.core.utils.scheduling.Task
import org.hyacinthbots.susdetector.database.Cleanups
import kotlin.time.Duration.Companion.days

class StartupHooks : Extension() {
    override val name: String
        get() = "startup-hooks"

    private val scheduler = Scheduler()

    private lateinit var task: Task

    override suspend fun setup() {
        task = scheduler.schedule(1.days, repeat = true, callback = ::cleanup, name = "Cleanup task")
    }

    suspend fun cleanup() {
        Cleanups.cleanup()
    }
}
