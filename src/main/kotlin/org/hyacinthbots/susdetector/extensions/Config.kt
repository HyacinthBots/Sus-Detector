package org.hyacinthbots.susdetector.extensions

import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.DISCORD_WHITE
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.EphemeralSlashCommandContext
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.channel
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.entities.Config
import susdetector.i18n.Translations
import kotlin.time.Clock

class Config : Extension() {
    override val name: String
        get() = "config"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = Translations.Config.name
            description = Translations.Config.desc

            ephemeralSubCommand(::ConfigSetArgs) {
                name = Translations.Config.Set.name
                description = Translations.Config.Set.desc

                requirePermission(Permission.ManageGuild)

                check {
                    anyGuild()
                    hasPermission(Permission.ManageGuild)
                }

                action {
                    val config = getConfig()

                    if (config == null) {
                        ConfigCollection().set(
                            Config(
                                guild!!.id,
                                arguments.detectionChannel.id,
                                arguments.actionLogChannel.id
                            )
                        )
                    } else {
                        ConfigCollection().update(
                            guild!!.id,
                            arguments.detectionChannel.id,
                            arguments.actionLogChannel.id
                        )
                    }

                    arguments.actionLogChannel.asChannelOf<GuildMessageChannel>().createMessage {
                        embed {
                            embed(arguments.detectionChannel, arguments.actionLogChannel)

                            footer {
                                text = Translations.Config.Embed.setBy.translate(user.asUser().username)
                                icon = user.asUser().avatar?.cdnUrl?.toUrl()
                            }
                        }
                    }

                    respond {
                        embed {
                            embed(arguments.detectionChannel, arguments.actionLogChannel)
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Config.View.name
                description = Translations.Config.View.desc

                requirePermission(Permission.ManageGuild)

                check {
                    anyGuild()
                    hasPermission(Permission.ManageGuild)
                }

                action {
                    val config = getConfig()

                    if (config == null) {
                        respond {
                            content = Translations.Config.noExist.translate()
                        }
                        return@action
                    }

                    val detectionChannel = guild!!.getChannelOf<GuildMessageChannel>(config.detectionChannelId)
                    val actionLogChannel = guild!!.getChannelOf<GuildMessageChannel>(config.actionLogId)

                    respond {
                        embed {
                            title = Translations.Config.Embed.viewTitle.translate()
                            color = DISCORD_WHITE

                            field {
                                name = Translations.Config.Embed.detectionChannelName.translate()
                                value = detectionChannel.mention
                            }
                            field {
                                name = Translations.Config.Embed.actionLogName.translate()
                                value = actionLogChannel.mention
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Config.Clear.name
                description = Translations.Config.Clear.desc

                requirePermission(Permission.ManageGuild)

                check {
                    anyGuild()
                    hasPermission(Permission.ManageGuild)
                }

                action {
                    val config = getConfig()

                    if (config == null) {
                        respond {
                            content = Translations.Config.noExist.translate()
                        }
                        return@action
                    }

                    respond {
                        content = Translations.Config.Clear.success.translate()
                    }

                    val actionLog = guild!!.getChannelOfOrNull<GuildMessageChannel>(config.actionLogId)
                        ?: return@action

                    actionLog.createMessage {
                        embed {
                            description = Translations.Config.Clear.success.translate()
                            color = DISCORD_RED
                            timestamp = Clock.System.now()

                            footer {
                                text = Translations.Config.Embed.clearedBy.translate(user.asUserOrNull()?.username)
                                icon = user.asUserOrNull()?.avatar?.cdnUrl?.toUrl()
                            }
                        }
                    }

                    ConfigCollection().delete(guild!!.id)
                }
            }
        }
    }

    class ConfigSetArgs : Arguments() {
        val detectionChannel by channel {
            name = Translations.Config.Channel.name
            description = Translations.Config.Channel.desc
        }

        val actionLogChannel by channel {
            name = Translations.Config.Log.name
            description = Translations.Config.Log.desc
        }
    }

    suspend fun EphemeralSlashCommandContext<*, *>.getConfig(): Config? {
        if (guild == null) return null

        return ConfigCollection().get(guild!!.id)
    }

    fun EmbedBuilder.embed(detectionChannel: Channel, actionLogChannel: Channel) {
        title = Translations.Config.Embed.setTitle.translate()
        color = DISCORD_GREEN
        timestamp = Clock.System.now()

        field {
            name = Translations.Config.Embed.detectionChannelName.translate()
            value = detectionChannel.mention
        }
        field {
            name = Translations.Config.Embed.actionLogName.translate()
            value = actionLogChannel.mention
        }
    }
}
