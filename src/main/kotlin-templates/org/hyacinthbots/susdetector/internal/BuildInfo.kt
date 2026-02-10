package org.hyacinthbots.susdetector.internal

/**
 * This object stores the constants for the Build ID and version of Lily in her current state
 */
object BuildInfo {
    /** The short commit hash of this build of Lily. */
    const val BUILD_ID: String = "{{ build_id }}"

    /** The current version of LilyBot. */
    const val BOT_VERSION: String = "{{ version }}"
}
