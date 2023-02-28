package com.oceanusmc.dev.badplye.oceanusdiscord.Logs;

import com.oceanusmc.dev.badplye.oceanusdiscord.OceanusDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;

public class Moderation {

    public static EmbedBuilder staffLog(OceanusDiscord mcPlugin, String id, String type, String duration, User target, User sender, String reason, String proof) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment " + id);
        embed.setDescription("Punishment issued on " + TimeFormat.DATE_TIME_LONG.now());
        embed.setFooter("OceanusMC", mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Type", type, true);
        embed.addField("Target", target.getAsMention(), true);
        embed.addField("Target ID", target.getId(), true);
        embed.addField("Duration", duration, true);
        embed.addField("Issuer", sender.getAsMention(), true);
        embed.addField("Issuer ID", sender.getId(), true);
        embed.addField("Reason", reason, false);
        embed.addField("Proof", "[Click here](" + proof + ") to view a full resolution file.", false);
        embed.setImage(proof);
        embed.setColor(Color.RED);

        return embed;
    }
    public static EmbedBuilder staffLog(OceanusDiscord mcPlugin, String id, String type, String duration, User target, User sender, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment " + id);
        embed.setDescription("Punishment issued on " + TimeFormat.DATE_TIME_LONG.now());
        embed.setFooter("OceanusMC", mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Type", type, true);
        embed.addField("Target", target.getAsMention(), true);
        embed.addField("Target ID", target.getId(), true);
        embed.addField("Duration", duration, true);
        embed.addField("Issuer", sender.getAsMention(), true);
        embed.addField("Issuer ID", sender.getId(), true);
        embed.addField("Reason", reason, false);
        embed.setColor(Color.RED);

        return embed;
    }
    private static void isExempt(User staff){

    }
}
