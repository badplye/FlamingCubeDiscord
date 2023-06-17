package com.panstal.badplye.flamingcubediscord;

import com.panstal.badplye.flamingcubediscord.Commands.CommandManager;
import com.panstal.badplye.flamingcubediscord.MySQL.MySQL;
import com.panstal.badplye.flamingcubediscord.MySQL.PunishmentGetter;
import com.panstal.badplye.flamingcubediscord.Utilities.UnPunish;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    public static net.milkbowl.vault.permission.Permission permission = null;
    public static JDA jda;
    public MySQL SQL;
    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        log.info("[OceanusDiscord] Loading configuration...");

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        log.info("[OceanusDiscord] Config loaded!");

        log.info("[OceanusDiscord] Atempting to connect to MySQL database...");
        this.SQL = new MySQL(this);
        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            log.severe("[OceanusDiscord] Error connecting to MySQL database");
            throw new RuntimeException(e);
        }
        log.info("[OceanusDiscord] Connected to database");
        PreparedStatement ps;
        try {
            ps = this.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS fcdiscord_punishments " + "(id VARCHAR(18), date VARCHAR(10), type VARCHAR(16),  target VARCHAR(20), staff VARCHAR(20), reason VARCHAR(2000), proof VARCHAR(2000), expires BIGINT, active TINYINT, PRIMARY KEY (id))");
            ps.executeUpdate();
        } catch (SQLException e) {
            log.severe("[OceanusDiscord] Error setting up database");
            throw new RuntimeException(e);
        }

        log.info("[OceanusDiscord] Starting discord bot...");
        try {
            jda = JDABuilder.createDefault(getConfig().getString("token"))
                    .setStatus(OnlineStatus.IDLE)
                    .addEventListeners(new CommandManager(this))
                    .setActivity(Activity.streaming("FlamingCube", "https://flamingcube.net"))
                    .build().awaitReady();
            log.info("[OceanusDiscord] Bot loaded!");
        } catch (InterruptedException e) {
            log.severe("[OceanusDiscord] Error loading bot!");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                UnPunish unPunish = new UnPunish(SQL, Main.this);
                unPunish.execute();
                log.info("[OceanusDiscord] Checking fo Punishments");
            }
        }.runTaskTimer(this, 0, 1 * 60 * 20);


        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        SQL.disconnect();
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
    public Main returnInstance() {
        return this;
    }

}
