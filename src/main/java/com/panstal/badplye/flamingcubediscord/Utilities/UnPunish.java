package com.panstal.badplye.flamingcubediscord.Utilities;

import com.panstal.badplye.flamingcubediscord.Logs.Moderation;
import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.MySQL.MySQL;
import com.panstal.badplye.flamingcubediscord.MySQL.PunishmentGetter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.panstal.badplye.flamingcubediscord.Main.jda;

public class UnPunish {
    MySQL SQL;
    PunishmentGetter getter;
    Main mcPlugin;
    public UnPunish(MySQL sql, Main plugin) {
        mcPlugin = plugin;
        this.SQL = sql;
        getter = new PunishmentGetter(plugin);
    }
    public void execute() {
        try {
            ArrayList<PunishmentRecord> recordList = getter.unPunishLookup();
            Guild guild = jda.getGuildById(mcPlugin.getConfig().getString("public-guild"));
            for(PunishmentRecord record : recordList) {
                User user = jda.retrieveUserById(record.getTarget()).complete();
                if(record.getType().contains("Temp-Mute")) {

                    List<Role> mutedRoles = guild.getRolesByName("Muted", false);
                    mcPlugin.getLogger().info(record.getTarget());
                    // List<Role> memberRoles = guild.getMember(user).getRoles();
                    for (Role mutedRole : mutedRoles) {
                        guild.removeRoleFromMember(user, mutedRole).queue();
                        mcPlugin.getLogger().info("Unmuted Someone");
                        record.setActive(false);
                        getter.updatePunishment(record);
                    }
                } else if (record.getType().contains("Temp-Ban")) {
                    guild.unban(jda.retrieveUserById(record.getTarget()).complete());
                    guild.unban(user).queue();
                    mcPlugin.getLogger().info("Unbanned Someone");
                    record.setActive(false);
                    getter.updatePunishment(record);
                }
            }
            mcPlugin.getLogger().info("Done with unpunish");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
