package com.oceanusmc.dev.badplye.oceanusdiscord;

import com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats.Listeners;
import com.oceanusmc.dev.badplye.oceanusdiscord.Modules.PlayerStats.PlayerInfo;
import com.oceanusmc.dev.badplye.oceanusdiscord.Modules.Moderation.*;
import com.oceanusmc.dev.badplye.oceanusdiscord.Utilities.Database.StatsDB;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class OceanusDiscord extends JavaPlugin {
    public static net.milkbowl.vault.permission.Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    public static JDA jda;
    private StatsDB db;
    private static final Logger log = Logger.getLogger("Minecraft");
    public OceanusDiscord pluginInstance = this;

    @Override
    public void onEnable() {
        log.info("[OceanusDiscord] Loading configuration...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        log.info("[OceanusDiscord] Config loaded!");

        setupPermissions();
        setupChat();

        try{
            db = new StatsDB(this);
            db.initializeDatabase();
        } catch (SQLException ex){
            log.severe("[OceanusDiscord] Unable to connect to database!!!");
            getServer().getPluginManager().disablePlugin(this);
        }

        log.info("[OceanusDiscord] Registering listeners...");
        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        log.info("[OceanusDiscord] Starting discord bot...");
        try {
            jda = JDABuilder.createDefault(getConfig().getString("token"))
                    .setStatus(OnlineStatus.IDLE)
                    .addEventListeners(new Warn(this))
                    .addEventListeners(new TempMute(this))
                    .addEventListeners(new Mute(this))
                    .addEventListeners(new TempBan(this))
                    .addEventListeners(new Ban(this))
                    .addEventListeners(new PlayerInfo(this))
                    .setActivity(Activity.watching("OceanusMC"))
                    .build().awaitReady();
            log.info("[OceanusDiscord] Bot loaded!");
        } catch (InterruptedException e) {
            log.severe("[OceanusDiscord] Error loading bot!");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        Guild guild = jda.getGuildById(getConfig().getString("public-guild"));

        jda.upsertCommand("info", "Bot information").queue();

        if(guild != null) {
            guild.upsertCommand("stats", "Player information")
                    .addOption(OptionType.STRING, "player", "Player to get statistics", true).queue();
            guild.upsertCommand("warn", "[Staff] Warn a user")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS, Permission.VOICE_MUTE_OTHERS))
                    .addOption(OptionType.USER, "user", "User to punish", true)
                    .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                    .addOption(OptionType.STRING, "proof", "Proof of infraction", false).queue();
            guild.upsertCommand("tempmute", "[Staff] Tempmute a user")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS, Permission.VOICE_MUTE_OTHERS))
                    .addOption(OptionType.USER, "user", "User to punish", true)
                    .addOption(OptionType.STRING, "duration", "Duration to punish", true)
                    .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                    .addOption(OptionType.STRING, "proof", "Proof of infraction", false).queue();
            guild.upsertCommand("mute", "[Staff] Mute a user")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS, Permission.VOICE_MUTE_OTHERS))
                    .addOption(OptionType.USER, "user", "User to punish", true)
                    .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                    .addOption(OptionType.STRING, "proof", "Proof of infraction", false).queue();
            guild.upsertCommand("ban", "[Staff] Ban a user")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                    .addOption(OptionType.USER, "user", "User to punish", true)
                    .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                    .addOption(OptionType.STRING, "proof", "Proof of infraction", false).queue();
            guild.upsertCommand("tempban", "[Staff] Tempban a user")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                    .addOption(OptionType.USER, "user", "User to punish", true)
                    .addOption(OptionType.STRING, "duration", "Duration to punish", true)
                    .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                    .addOption(OptionType.STRING, "proof", "Proof of infraction", false).queue();
            guild.upsertCommand("gcreate", "[Admin] Create a giveaway")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                    .addOption(OptionType.CHANNEL, "channel", "Channel", true)
                    .addOption(OptionType.STRING, "duration", "Duration", true)
                    .addOption(OptionType.STRING, "reward", "Reward", true)
                    .addOption(OptionType.INTEGER, "winners", "Amount of winners", true)
                    .addOption(OptionType.USER, "host", "Host", false).queue();
        }
        log.info("[OceanusDiscord] Plugin loaded!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public StatsDB getDatabase() {
        return db;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer()
                .getServicesManager().getRegistration(
                        net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer()
                .getServicesManager().getRegistration(
                        net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer()
                .getServicesManager().getRegistration(
                        net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static net.milkbowl.vault.permission.Permission getPermissions() {
        return permission;
    }

    public static Chat getChat() {
        return chat;
    }
}
