package com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats;

import com.oceanusmc.dev.badplye.oceanusdiscord.OceanusDiscord;
import com.oceanusmc.dev.badplye.oceanusdiscord.Utilities.PlayerFetcher;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Listeners implements Listener {
    private final OceanusDiscord mcPlugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    public Listeners(OceanusDiscord mcPlugin) {
        this.mcPlugin = mcPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent e) throws SQLException {
        Player p = e.getPlayer();
        PlayerStats stats = null;
        try {
            stats = this.mcPlugin.getDatabase().findPlayerStatsByUUID(String.valueOf(PlayerFetcher.getUUID(p.getName())));
            ;
        } catch (SQLException exception){
            exception.printStackTrace();
            log.severe("[OceanusDiscord] SQL Exception");
        }
        if (stats == null) {
            String uuid = String.valueOf(PlayerFetcher.getUUID(p.getName()));
            String rank = OceanusDiscord.getPermissions().getPrimaryGroup(p.getWorld().getName(), p);
            String rankFormatted = StringUtils.capitalize(rank);
            long lastPlayed = p.getFirstPlayed();
            long joinDate = p.getFirstPlayed();
            stats = new PlayerStats(uuid, rankFormatted, lastPlayed, joinDate, "A", "1", 0, 0, 0, 0);
            this.mcPlugin.getDatabase().createPlayerStats(stats);
            PlayerStats finalStats1 = stats;
            Bukkit.getScheduler().runTaskLater(this.mcPlugin, new Runnable() {
                @Override
                public void run() {
                    finalStats1.setCurrentIsland(PlaceholderAPI.setPlaceholders(p, "%ezrankspro_rank%"));
                    finalStats1.setPrestige(PlaceholderAPI.setPlaceholders(p, "%ezprestige_prestige%"));
                    try {
                        mcPlugin.getDatabase().updatePlayerStats(finalStats1);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, 10);
        } else {
            String rank = OceanusDiscord.getPermissions().getPrimaryGroup(p.getWorld().getName(), p);
            String rankFormatted = rank.substring(0, 1).toUpperCase() + rank.substring(1);
            stats.setRank(rankFormatted);
            stats.setLastOnline(System.currentTimeMillis());
            PlayerStats finalStats = stats;
            Bukkit.getScheduler().runTaskLater(this.mcPlugin, new Runnable() {
                @Override
                public void run() {
                    finalStats.setCurrentIsland(PlaceholderAPI.setPlaceholders(p, "%ezrankspro_rank%"));
                    finalStats.setPrestige(PlaceholderAPI.setPlaceholders(p, "%ezprestige_prestige%"));
                    try {
                        mcPlugin.getDatabase().updatePlayerStats(finalStats);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, 10);
            this.mcPlugin.getDatabase().updatePlayerStats(stats);

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws SQLException {
        Player p = e.getPlayer();


        try {
            PlayerStats stats = this.mcPlugin.getDatabase().findPlayerStatsByUUID(String.valueOf(PlayerFetcher.getUUID(p.getName())));

            stats.setPlayTime(stats.getPlayTime() + ((System.currentTimeMillis() / 1000) - (stats.getLastOnline() / 1000)));
            stats.setLastOnline(System.currentTimeMillis());
            String rank = OceanusDiscord.getPermissions().getPrimaryGroup(p.getWorld().getName(), p);
            String rankFormatted = rank.substring(0, 1).toUpperCase() + rank.substring(1);
            stats.setLastOnline(System.currentTimeMillis());
            stats.setRank(rankFormatted);
            this.mcPlugin.getDatabase().updatePlayerStats(stats);
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    @EventHandler
    public void onKill(PlayerDeathEvent e) throws SQLException {
        Player p = e.getEntity().getKiller();


        try {
            PlayerStats stats = this.mcPlugin.getDatabase().findPlayerStatsByUUID(String.valueOf(PlayerFetcher.getUUID(p.getName())));
            stats.setKills(stats.getKills() + 1);
            this.mcPlugin.getDatabase().updatePlayerStats(stats);
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) throws SQLException {
        Player p = e.getPlayer();


        try {
            PlayerStats stats = this.mcPlugin.getDatabase().findPlayerStatsByUUID(String.valueOf(PlayerFetcher.getUUID(p.getName())));
            stats.setBlocksBroken(stats.getBlocksBroken() + 1);
            this.mcPlugin.getDatabase().updatePlayerStats(stats);
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }

}
