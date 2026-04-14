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
import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.commands.application.slash.converters.impl.defaultingEnumChoice
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.channel
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.i18n.Key
import org.hyacinthbots.susdetector.database.collections.ConfigCollection
import org.hyacinthbots.susdetector.database.entities.Config
import org.hyacinthbots.susdetector.utils.trimmedContents
import susdetector.i18n.Translations
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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

                    val deleteDuration = when (arguments.deleteMessageDuration) {
                        DeleteDuration.LastHour -> 1.hours
                        DeleteDuration.Last6Hours -> 6.hours
                        DeleteDuration.Last12Hours -> 12.hours
                        DeleteDuration.LastDay -> 1.days
                        DeleteDuration.Last3Days -> 3.days
                        DeleteDuration.Last7Days -> 7.days
                    }

                    if (config == null) {
                        ConfigCollection().set(
                            Config(
                                guild!!.id,
                                arguments.detectionChannel.id,
                                arguments.actionLogChannel.id,
                                deleteDuration.inWholeSeconds,
                                arguments.customDm
                            )
                        )
                    } else {
                        ConfigCollection().update(
                            guild!!.id,
                            arguments.detectionChannel.id,
                            arguments.actionLogChannel.id,
                            deleteDuration.inWholeSeconds,
                            arguments.customDm
                        )
                    }

                    arguments.actionLogChannel.asChannelOf<GuildMessageChannel>().createMessage {
                        embed {
                            title = Translations.Config.Embed.setTitle.translate()
                            color = DISCORD_GREEN
                            timestamp = Clock.System.now()
                            embed(
                                arguments.detectionChannel,
                                arguments.actionLogChannel,
                                deleteDuration,
                                arguments.customDm
                            )

                            footer {
                                text = Translations.Config.Embed.setBy.translate(user.asUser().username)
                                icon = user.asUser().avatar?.cdnUrl?.toUrl()
                            }
                        }
                    }

                    respond {
                        embed {
                            title = Translations.Config.Embed.setTitle.translate()
                            color = DISCORD_GREEN
                            timestamp = Clock.System.now()
                            embed(
                                arguments.detectionChannel,
                                arguments.actionLogChannel,
                                deleteDuration,
                                arguments.customDm
                            )
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
                    val deleteDuration = when (config.deleteDuration) {
                        1.hours.inWholeSeconds -> 1.hours
                        6.hours.inWholeSeconds -> 6.hours
                        12.hours.inWholeSeconds -> 12.hours
                        1.days.inWholeSeconds -> 1.days
                        3.days.inWholeSeconds -> 3.days
                        7.days.inWholeSeconds -> 7.days
                        else -> 3.days
                    }

                    respond {
                        embed {
                            title = Translations.Config.Embed.viewTitle.translate()
                            color = DISCORD_WHITE

                            embed(detectionChannel, actionLogChannel, deleteDuration, config.customDm)
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

        val deleteMessageDuration by defaultingEnumChoice<DeleteDuration> {
            name = Translations.Config.DelDays.name
            description = Translations.Config.DelDays.desc
            typeName = DeleteDuration.Last3Days.readableName
            defaultValue = DeleteDuration.Last3Days
            choices = mutableMapOf(
                DeleteDuration.LastHour.readableName to DeleteDuration.LastHour,
                DeleteDuration.Last6Hours.readableName to DeleteDuration.Last6Hours,
                DeleteDuration.Last12Hours.readableName to DeleteDuration.Last12Hours,
                DeleteDuration.LastDay.readableName to DeleteDuration.LastDay,
                DeleteDuration.Last3Days.readableName to DeleteDuration.Last3Days,
                DeleteDuration.Last7Days.readableName to DeleteDuration.Last7Days
            )
        }

        val customDm by optionalString {
            name = Translations.Config.CustDm.name
            description = Translations.Config.CustDm.desc
        }
    }

    suspend fun EphemeralSlashCommandContext<*, *>.getConfig(): Config? {
        if (guild == null) return null

        return ConfigCollection().get(guild!!.id)
    }

    fun EmbedBuilder.embed(
        detectionChannel: Channel,
        actionLogChannel: Channel,
        deleteDuration: Duration,
        customDm: String?
    ) {
        field {
            name = Translations.Config.Embed.detectionChannelName.translate()
            value = detectionChannel.mention
        }
        field {
            name = Translations.Config.Embed.actionLogName.translate()
            value = actionLogChannel.mention
        }
        field {
            name = Translations.Config.Embed.deleteName.translate()
            value = deleteDuration.toString().lowercase().replace("pt", "").replace("p", "")
        }
        if (customDm != null) {
            field {
                name = Translations.Config.Embed.custDmName.translate()
                value = customDm.trimmedContents()
            }
        }
    }
}

enum class DeleteDuration(override val readableName: Key) : ChoiceEnum {
    LastHour(Translations.Delete.lasthour),
    Last6Hours(Translations.Delete.last6),
    Last12Hours(Translations.Delete.last12),
    LastDay(Translations.Delete.lastday),
    Last3Days(Translations.Delete.last3days),
    Last7Days(Translations.Delete.last7days),
}
