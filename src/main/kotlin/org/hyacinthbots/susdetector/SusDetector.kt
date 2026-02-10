package org.hyacinthbots.susdetector

import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.i18n.SupportedLocales
import dev.kordex.data.api.DataCollection
import org.hyacinthbots.susdetector.extensions.Config
import org.hyacinthbots.susdetector.extensions.StartupHooks
import org.hyacinthbots.susdetector.extensions.SusDetection
import org.hyacinthbots.susdetector.internal.BuildInfo
import org.hyacinthbots.susdetector.utils.BOT_TOKEN
import org.hyacinthbots.susdetector.utils.HYACINTH_GITHUB
import org.hyacinthbots.susdetector.utils.database
import susdetector.i18n.Translations

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val bot = ExtensibleBot(BOT_TOKEN) {
        dataCollectionMode = DataCollection.None

        database(true)

        kord {
            stackTraceRecovery = true
        }

        about {
            ephemeral = true
            general {
                message {
                    embed {
                        title = Translations.About.embedTitle.translate()

                        // TODO Get a logo for the thumbnail
// 						thumbnail {
// 							url = ""
// 						}

                        description = Translations.About.embedDesc.translate()

                        field {
                            name = Translations.About.devInfo.translate()
                            value = Translations.About.devInfoDesc.translate()
                        }

                        field {
                            name = Translations.About.version.translate()
                            value = "${BuildInfo.BOT_VERSION} (${BuildInfo.BUILD_ID})"
                        }
                    }

                    actionRow {
                        linkButton(
                            "https://discord.com/oauth2/authorize?client_id=1461718494361161789&" +
                                "permissions=26628&integration_type=0&scope=bot+applications.commands"
                        ) {
                            label = Translations.About.invite.translate()
                        }

                        linkButton("$HYACINTH_GITHUB/LilyBot/blob/main/docs/privacy-policy.md") {
                            label = Translations.About.privacy.translate()
                        }

                        linkButton("$HYACINTH_GITHUB/.github/blob/main/terms-of-service.md") {
                            label = Translations.About.tos.translate()
                        }
                    }
                }
            }
        }

        intents(addDefaultIntents = false, addExtensionIntents = false) {
            +Intent.Guilds
            +Intent.GuildMembers
            +Intent.GuildModeration
            +Intent.GuildMessages
            +Intent.DirectMessages
            +Intent.MessageContent
        }

        extensions {
            add(::Config)
            add(::StartupHooks)
            add(::SusDetection)
        }

        presence {
            status = PresenceStatus.Online
            state = "Are you being sus?"
        }

        i18n {
            interactionUserLocaleResolver()
            interactionGuildLocaleResolver()

            // TODO Add more languages when files are created
            applicationCommandLocale(SupportedLocales.ENGLISH)
        }
    }

    bot.start()
}
