package com.panstal.badplye.flamingcubediscord.Commands;

import com.panstal.badplye.flamingcubediscord.Commands.Moderation.*;
import com.panstal.badplye.flamingcubediscord.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CommandManager extends ListenerAdapter {
    public CommandManager(Main main) {
        this.mcPlugin = main;
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if(command.equalsIgnoreCase("about")) { // command /about

        }

        if(command.equalsIgnoreCase("tempban")) {
            new TempBan(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("tempmute")) {
            new TempMute(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("warn")) {
            new Warn(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("ban")) {
            new Ban(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("mute")) {
            new Mute(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("unban")) {
            new UnBan(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("unmute")) {
            new UnMute(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("lookup")) {
            new Lookup(event, mcPlugin).executeCommand();
        }
        if(command.equalsIgnoreCase("history")) {
            new History(event, mcPlugin).executeCommand();
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("lookup", "Lookup a punishment")
                .addOption(OptionType.STRING, "id", "ID of punishment", true)
        );
        commandData.add(Commands.slash("history", "Lookup a punishment")
                .addOption(OptionType.STRING, "id", "ID of user", true)
        );
        commandData.add(Commands.slash("tempban", "Temporarily ban a player from the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "duration", "Duration to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                .addOption(OptionType.STRING, "proof", "Proof of infraction", false)
        );

        commandData.add(Commands.slash("tempmute", "Temporarily mute a player in the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "duration", "Duration to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                .addOption(OptionType.STRING, "proof", "Proof of infraction", false)
        );

        commandData.add(Commands.slash("ban", "Permanently ban a player from the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                .addOption(OptionType.STRING, "proof", "Proof of infraction", false)
        );

        commandData.add(Commands.slash("mute", "Permanently mute a player in the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                .addOption(OptionType.STRING, "proof", "Proof of infraction", false)
        );

        commandData.add(Commands.slash("warn", "Warn a player in the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
                .addOption(OptionType.STRING, "proof", "Proof of infraction", false)
        );

        commandData.add(Commands.slash("unmute", "Un-Mute a player in discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
        );

        commandData.add(Commands.slash("unban", "Un-Ban a player from the discord server.")
                .addOption(OptionType.USER, "user", "User to punish", true)
                .addOption(OptionType.STRING, "reason", "Reason for punishment", true)
        );
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        // event.getJDA().updateCommands();
    }

    public final Main mcPlugin;
}
