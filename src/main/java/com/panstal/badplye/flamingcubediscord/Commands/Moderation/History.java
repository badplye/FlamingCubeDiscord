package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.MySQL.PunishmentGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.panstal.badplye.flamingcubediscord.Main.jda;

public class History {
    protected SlashCommandInteractionEvent event;
    protected final Main mcPlugin;
    private PunishmentGetter database;

    public History(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        this.mcPlugin = plugin;
        event = cmdEvent;
        this.database = new PunishmentGetter(plugin);
    }

    public void executeCommand() {
        String userID = event.getOption("id").getAsString();
        event.deferReply().queue();
        EmbedBuilder embed = new EmbedBuilder();
        ArrayList<PunishmentRecord> recordsList = new ArrayList<PunishmentRecord>();
        try {
            recordsList = database.targetLookup(userID);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (PunishmentRecord record : recordsList) {
            embed.addField("", "<t:" + record.getDate() + ":d> - " + record.getType() + ", " + record.getId(), false);
        }
        embed.setTitle("Punishment History");
        embed.setDescription("Punishment history for " + jda.retrieveUserById(userID).complete().getAsMention());
        embed.setFooter("FlamingCube", mcPlugin.getConfig().getString("icon-url"));
        embed.setColor(Color.RED);
        event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
