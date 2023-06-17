package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Logs.Moderation;
import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.Utilities.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Mute extends Punishment {

    public Mute(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        super(cmdEvent, plugin);
    }
    public void executeCommand() {
        event.deferReply().queue();

        EmbedBuilder response = new EmbedBuilder();
        if(!checkPermissions()) {
            response.setTitle("Permission Error");
            response.setDescription("This user has a equal or higher rank than you!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }
        if(!checkProof()) {
            response.setTitle("Permission Error");
            response.setDescription("Your rank requires proof to be included in this punishment!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }

        List<Role> mutedRoles = guild.getRolesByName("Muted", false);
        if (!mutedRoles.isEmpty()) {
            Role mutedRole = mutedRoles.get(0);
            guild.addRoleToMember(target, mutedRole).queue();
        } else {
            response.setTitle("Runtime Error");
            response.setDescription("No muted role exists!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }
        String reason = event.getOption("reason").getAsString();
        String id = generateID();

        sendNotice(target,"Muted", reason, id, "Permanent");

        Guild staffDiscord = event.getJDA().getGuildById(mcPlugin.getConfig().getString("staff-guild"));
        TextChannel logChannel = staffDiscord.getTextChannelById(mcPlugin.getConfig().getString("mod-log"));

        if(!checkProof() || event.getOption("proof") != null) {
            logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Mute", "Permanent", target, sender, event.getOption("reason").getAsString(), event.getOption("proof").getAsString()).build()).queue();
            PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Mute", target.getId(), sender.getId(), event.getOption("reason").getAsString(), event.getOption("proof").getAsString(), Long.parseLong("9999999999"), true);
            database.createPunishment(dbRecord);
        } else {
            logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Mute", "Permanent", target, sender, event.getOption("reason").getAsString()).build()).queue();
            PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Mute", target.getId(), sender.getId(), event.getOption("reason").getAsString(), "None", Long.parseLong("9999999999"), true);
            database.createPunishment(dbRecord);
        }

        // send a message to the channel to confirm that the user was banned
        response.setTitle("Success");
        response.setDescription(target.getName() + " has been muted!");
        response.setColor(Color.GREEN);
        event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
    }
}
