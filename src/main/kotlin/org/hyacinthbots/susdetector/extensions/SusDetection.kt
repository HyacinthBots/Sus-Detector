package org.hyacinthbots.susdetector.extensions

import dev.kord.common.entity.Permission
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_PINK
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.dm
import dev.kordex.core.utils.hasPermission
import io.github.oshai.kotlinlogging.KotlinLogging
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.utils.trimmedContents
import susdetector.i18n.Translations
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

class SusDetection : Extension() {
    override val name: String = "sus-detection"

    val logger = KotlinLogging.logger {}

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check {
                anyGuild()
                failIf {
                    val config = event.guildId?.let { ConfigCollection().get(it) }

                    config == null || event.message.channelId != config.detectionChannelId ||
                        event.message.author?.id == kord.selfId ||
                        event.message.author?.asMember(event.guildId!!)!!.hasPermission(Permission.BanMembers)
                }
            }

            action {
                val guild = event.getGuildOrNull() ?: return@action

                val config = ConfigCollection().get(guild.id)

                logger.debug { "Getting message author" }
                val targetUser = event.message.author
                if (targetUser == null) {
                    logger.debug { "Message author is null. Action NOT running" }
                    return@action
                }
                logger.debug { "Message Author ID: ${targetUser.id}" }

                logger.debug { "Attempting to send DM to ${targetUser.id}" }
                targetUser.dm {
                    embed {
                        title = Translations.Sus.dmTitle.translate(guild.asGuildOrNull().name)
                        description = Translations.Sus.dmDesc.translate()
                    }
                }
                logger.debug { "DM attempt sent to ${targetUser.id}" }

                logger.debug { "Banning ${targetUser.id}" }
                guild.ban(targetUser.id) {
                    reason = "Sent a message in ${event.message.channel.mention}"
                    deleteMessageDuration = 3.days
                }
                logger.debug { "Banned ${targetUser.id}" }

                logger.debug { "Unbanning ${targetUser.id}" }
                guild.unban(targetUser.id, "Unbanned following sus messages.")
                logger.debug { "Unbanned ${targetUser.id}" }

                logger.debug { "Getting action log" }
                val actionLog = guild.getChannelOfOrNull<GuildMessageChannel>(config!!.actionLogId)
                if (actionLog == null) {
                    logger.error { "Action Log does not exist! Fix your config!" }
                    return@action
                }
                logger.debug { "Action Log retrieved" }

                logger.debug { "Sending log to action log!" }
                actionLog.createMessage {
                    embed {
                        title = Translations.Sus.logTitle.translate()
                        description = Translations.Sus.logDesc.translate(targetUser.mention, config.detectionChannelId)
                        timestamp = Clock.System.now()
                        color = DISCORD_PINK

                        field {
                            name = Translations.Sus.user.translate()
                            value = "${targetUser.username}\n${targetUser.id}"
                        }

                        field {
                            name = Translations.Sus.content.translate()
                            value = event.message.content.trimmedContents()
                        }
                    }
                }
            }
        }
    }
}
