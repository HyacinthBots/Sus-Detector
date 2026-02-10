package org.hyacinthbots.susdetector.utils

import dev.kordex.core.utils.env
import dev.kordex.core.utils.envOrNull

/** The Bot Token. */
val BOT_TOKEN = env("TOKEN")

/** The string for connection to the mongo database. Defaults to local host. */
val MONGO_URI = envOrNull("MONGO_URI") ?: "mongodb://localhost:27017"

/** The main section of the URL for HyacinthBots GitHub. */
const val HYACINTH_GITHUB: String = "https://github.com/HyacinthBots"
