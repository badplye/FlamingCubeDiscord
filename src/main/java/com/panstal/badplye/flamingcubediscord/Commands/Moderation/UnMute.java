package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Logs.Moderation;
import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.Utilities.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.apache.commons.lang.WordUtils;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UnMute extends Punishment {
    public UnMute(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        super(cmdEvent, plugin);
    }
    public void executeCommand() {
        event.deferReply().queue();

        EmbedBuilder response = new EmbedBuilder();

        String reason = event.getOption("reason").getAsString();
        String id = generateID();

        List<Role> mutedRoles = guild.getRolesByName("Muted", false);
        if (mutedRoles.isEmpty()) {
            response.setTitle("Runtime Error");
            response.setDescription("No muted role exists!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }
        List<Role> memberRoles = event.getOption("user").getAsMember().getRoles();
        for (Role memberRole : memberRoles) {
            for (Role mutedRole : mutedRoles) {
                if(memberRole.equals(mutedRole)) {
                    sendNotice(target,"Un-Muted", reason, id, "N/A");

                    Guild staffDiscord = event.getJDA().getGuildById(mcPlugin.getConfig().getString("staff-guild"));
                    TextChannel logChannel = staffDiscord.getTextChannelById(mcPlugin.getConfig().getString("mod-log"));

                    logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Un-Mute", "N/A", target, sender, event.getOption("reason").getAsString()).setColor(Color.GREEN).build()).queue();
                    PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Un-Mute", target.getId(), sender.getId(), event.getOption("reason").getAsString(), "None", Long.parseLong("9999999999"), true);
                    database.createPunishment(dbRecord);

                    // send a message to the channel to confirm that the user was banned
                    response.setTitle("Success");
                    response.setDescription(target.getName() + " has been un-muted!");
                    response.setColor(Color.GREEN);
                    event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();

                    guild.removeRoleFromMember(target, mutedRole).queue();

                    return;
                }
            }
        }
        response.setTitle("Runtime Error");
        response.setDescription("This user is not currently muted!");
        response.setColor(Color.RED);
        event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
    }

    @Override
    public void sendNotice(User target, String type, String reason, String id, String duration) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment Reversal Notice");
        embed.setDescription("You have been **un-muted** from the " + guild.getName() + " discord!");
        embed.setThumbnail(mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Reason", event.getOption("reason").getAsString(), false);
        embed.addField("Reversal ID", id, true);
        embed.addField("Duration", "N/A", true);
        embed.addField("Un-Muted" + " By", sender.getAsMention(), true);
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
