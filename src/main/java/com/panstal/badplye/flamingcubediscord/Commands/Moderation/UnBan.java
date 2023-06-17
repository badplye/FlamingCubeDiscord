package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Logs.Moderation;
import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.Utilities.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.apache.commons.lang.WordUtils;

import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnBan extends Punishment {
    public UnBan(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        super(cmdEvent, plugin);
    }
    public void executeCommand() {
        event.deferReply().queue();

        EmbedBuilder response = new EmbedBuilder();
        guild.retrieveBan(target).queue(
                (success) -> {
                    String reason = event.getOption("reason").getAsString();
                    String id = generateID();

                    sendNotice(target,"Un-Banned", reason, id, "N/A");

                    event.getGuild().unban(target).queue();

                    Guild staffDiscord = event.getJDA().getGuildById(mcPlugin.getConfig().getString("staff-guild"));
                    TextChannel logChannel = staffDiscord.getTextChannelById(mcPlugin.getConfig().getString("mod-log"));

                    logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Un-Ban", "N/A", target, sender, event.getOption("reason").getAsString()).setColor(Color.GREEN).build()).queue();
                    PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Un-Ban", target.getId(), sender.getId(), event.getOption("reason").getAsString(), "None", Long.parseLong("9999999999"), false);
                    database.createPunishment(dbRecord);

                    // send a message to the channel to confirm that the user was banned
                    response.setTitle("Success");
                    response.setDescription(target.getName() + " has been unbanned!");
                    response.setColor(Color.GREEN);
                    event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
                },
                (failure) -> {
                    response.setTitle("Runtime Error");
                    response.setDescription("This user is not currently banned!");
                    response.setColor(Color.RED);
                    event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
                }
        );
    }

    @Override
    public void sendNotice(User target, String type, String reason, String id, String duration) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment Reversal Notice");
        embed.setDescription("You have been **un-banned** from the " + guild.getName() + " discord!");
        embed.setThumbnail(mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Reason", event.getOption("reason").getAsString(), false);
        embed.addField("Reversal ID", id, true);
        embed.addField("Duration", "N/A", true);
        embed.addField("Un-Banned" + " By", sender.getAsMention(), true);
        embed.setFooter(guild.getName(), mcPlugin.getConfig().getString("icon-url"));
        embed.setColor(Color.GREEN);

        CompletableFuture<Boolean> noticeSentFuture = new CompletableFuture<>();
        event.getOption("user").getAsUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessageEmbeds(embed.build()).queue(
                    success -> {
                        // The embed was successfully sent
                        noticeSentFuture.complete(true);
                    },
                    failure -> {
                        // The embed failed to be delivered
                        noticeSentFuture.complete(false);
                    }
            );
        });
        try {
            noticeSent = noticeSentFuture.get();
        } catch (Exception e) {
            // Handle any exceptions that occurred during the future retrieval
            e.printStackTrace();
        }

    }
}
