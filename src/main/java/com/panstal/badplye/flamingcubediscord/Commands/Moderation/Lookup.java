package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.MySQL.PunishmentGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.sql.SQLException;

import static com.panstal.badplye.flamingcubediscord.Main.jda;

public class Lookup {
    protected SlashCommandInteractionEvent event;
    protected final Main mcPlugin;
    private PunishmentGetter database;

    public Lookup(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        this.mcPlugin = plugin;
        event = cmdEvent;
        this.database = new PunishmentGetter(plugin);
    }

    public void executeCommand() {
        String id = event.getOption("id").getAsString();
        event.deferReply().queue();
        EmbedBuilder embed = new EmbedBuilder();
        PunishmentRecord record;
        try {
            record = database.getPunishment(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        embed.setTitle("Punishment " + record.getId());
        embed.setDescription("Punishment issued on " + "<t:" + Long.parseLong(record.getDate()) + ":F>");
        embed.setFooter("FlamingCube", mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Type", record.getType(), true);
        embed.addField("Target", jda.retrieveUserById(record.getTarget()).complete().getAsMention(), true);
        embed.addField("Target ID", record.getTarget(), true);
        if(record.getExpires() > Long.parseLong("9999999999")) {
            embed.addField("Expires", "Never", true);
        } else {
            embed.addField("Expires", "<t:" + record.getExpires() + ":d>", true);
        }
        embed.addField("Issuer", jda.retrieveUserById(record.getStaff()).complete().getAsMention(), true);
        embed.addField("Issuer ID", record.getStaff(), true);
        embed.addField("Reason", record.getReason(), false);
        if(!record.getProof().equalsIgnoreCase("None")) {
            embed.addField("Proof", "[Click here](" + record.getProof() + ") to view a full resolution file.", false);
            embed.setImage(record.getProof());
        }
        embed.setColor(Color.RED);
        event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
