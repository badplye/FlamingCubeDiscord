package com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats;
import me.clip.placeholderapi.PlaceholderAPI;
import com.oceanusmc.dev.badplye.oceanusdiscord.OceanusDiscord;
import com.oceanusmc.dev.badplye.oceanusdiscord.Utilities.PlayerFetcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class PlayerInfo extends ListenerAdapter {
    private final OceanusDiscord mcPlugin;
    private OfflinePlayer player;
    private OfflinePlayer target;

    public PlayerInfo(OceanusDiscord oceanusDiscord){
        this.mcPlugin = oceanusDiscord;
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("stats")) {
            event.deferReply().queue();
            EmbedBuilder response = new EmbedBuilder();
            PlayerStats stats = null;
            boolean online = false;
            Bukkit.getLogger().info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " (" + event.getUser().getId() + ") issued discord command: /stats player:" + event.getOption("player").getAsString());

            if(Bukkit.getOfflinePlayer(event.getOption("player").getAsString()).hasPlayedBefore()){
                player = Bukkit.getOfflinePlayer(PlayerFetcher.getUUID(event.getOption("player").getAsString()));
                target = Bukkit.getOfflinePlayer(event.getOption("player").getAsString());
                try {
                    stats = mcPlugin.getDatabase().findPlayerStatsByUUID(player.getUniqueId().toString());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(target.isOnline()) {
                online = true;
            }
            if(stats == null || player == null) {
                response.setTitle("Error");
                response.setDescription("This player has never joined the server!");
                response.setColor(Color.RED);
            } else {
                if(online) {
                    stats.setRank(StringUtils.capitalize(OceanusDiscord.getPermissions().getPrimaryGroup(target.getPlayer().getWorld().getName(), target.getPlayer())));
                    stats.setCurrentIsland(PlaceholderAPI.setPlaceholders(target.getPlayer(), "%ezrankspro_rank%"));
                    stats.setPrestige(PlaceholderAPI.setPlaceholders(target.getPlayer(), "%ezprestige_prestige%"));
                    try {
                        this.mcPlugin.getDatabase().updatePlayerStats(stats);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                boolean vanished = false;
                response.setTitle(event.getOption("player").getAsString() + " - Statistics");
                response.setThumbnail("https://visage.surgeplay.com/face/" + PlayerFetcher.getUUID(event.getOption("player").getAsString()) + ".png");
                if(online) {
                    for (MetadataValue value : target.getPlayer().getMetadata("vanished")) {
                        vanished |= value.asBoolean(); // But we only care if AT LEAST one of them is true.
                    }
                    if (vanished == true) {
                        stats.setRank(StringUtils.capitalize(OceanusDiscord.getPermissions().getPrimaryGroup(target.getPlayer().getWorld().getName(), target.getPlayer())));
                        response.addField("Player Rank:", stats.getRank(), true);
                        response.addField("Last Online:","<t:" +  String.valueOf(stats.getLastOnline() / 1000) + ":D>", true);
                    } else {
                        stats.setRank(StringUtils.capitalize(OceanusDiscord.getPermissions().getPrimaryGroup(target.getPlayer().getWorld().getName(), target.getPlayer())));
                        stats.setCurrentIsland(PlaceholderAPI.setPlaceholders(target.getPlayer(), "%ezrankspro_rank%"));
                        stats.setPrestige(PlaceholderAPI.setPlaceholders(target.getPlayer(), "%ezprestige_prestige%"));
                        stats.setPlayTime(stats.getPlayTime() + ((System.currentTimeMillis() / 1000) - (stats.getLastOnline() / 1000)));
                        stats.setLastOnline(System.currentTimeMillis());
                        response.addField("Player Rank:", stats.getRank(), true);
                        response.addField("Last Online:", "Currently Online", true);
                    }
                    try {
                        this.mcPlugin.getDatabase().updatePlayerStats(stats);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    response.addField("Player Rank:", stats.getRank(), true);
                    response.addField("Last Online:","<t:" +  String.valueOf(stats.getLastOnline() / 1000) + ":D>", true);
                }
                response.addField("Join Date:", "<t:" + String.valueOf(stats.getJoinDate() / 1000) + ":D>", true);
                response.addField("Current Island:",stats.getCurrentIsland(), true);
                response.addField("Prestige Level:", stats.getPrestige(), true);
                BigDecimal bd;
                bd = new BigDecimal(Double.toString(stats.getPlayTime() / 86400.0));
                response.addField("Playtime:", bd.setScale(2, RoundingMode.HALF_UP) + " Days", true);
                response.addField("Total Kills:", String.valueOf(stats.getKills()), true);
                response.addField("Blocks Broken:", String.valueOf(stats.getBlocksBroken()), true);
                response.setFooter("OceanusMC", mcPlugin.getConfig().getString("icon-url"));
                response.setColor(new Color(6, 161, 161));
            }

            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
        }
    }
    public static String roundDecimalUsingBigDecimal(String strValue, int decimalPlace) {
        return new BigDecimal(strValue).setScale(decimalPlace, RoundingMode.HALF_UP).toPlainString();
    }
}