package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.PunishmentGetter;
import com.panstal.badplye.flamingcubediscord.MySQL.SQLGetter;
import com.panstal.badplye.flamingcubediscord.Utilities.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang.WordUtils;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public abstract class Punishment {

    protected boolean noticeSent;

    protected SlashCommandInteractionEvent event;
    protected final Main mcPlugin;
    protected PunishmentGetter database;

    protected User sender;
    protected User target;
    protected Guild guild;


    public Punishment(SlashCommandInteractionEvent cmdEvent, Main plugin) {

        this.mcPlugin = plugin;
        event = cmdEvent;
        this.database = new PunishmentGetter(plugin);

        // get the user who sent the command, the guild they sent it in, and the target of the command sent
        sender = event.getUser();
        guild = event.getGuild();
        target = event.getOption("user").getAsUser();
    }
    public abstract void executeCommand();

    public boolean checkPermissions() {
        // check if the user has permission to punish
        List<Role> senderRoles = event.getMember().getRoles();
        List<Role> targetRoles = event.getOption("user").getAsMember().getRoles();
        if(senderRoles.get(0).getPosition() <= targetRoles.get(0).getPosition()) {
            return false;
        }
        return true;
    }

    public boolean checkProof() {
        // check if proof was included in the punishment
        boolean exempt = false;
        List<Role> senderRoles = event.getMember().getRoles();
        for(Role role : senderRoles) {
            if(mcPlugin.getConfig().getString("proof-exempt").contains(role.getId())) {
                exempt = true;
            }
        }
        if(event.getOption("proof") == null && exempt != true) {
            return false;
        }
        return true;
    }
    public String generateID() {
        Random random = new Random();
        String id = "D" + String.format("%09d", System.currentTimeMillis()) + String.format("%02d", random.nextInt(100));
        return id;
    }

    public void sendNotice(User target, String type, String reason, String id, String duration) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment Notice");
        embed.setDescription("You have been **" + type.toLowerCase() + "** from the " + guild.getName() + " discord!");
        embed.setThumbnail(mcPlugin.getConfig().getString("icon-url"));
        embed.setFooter(guild.getName(), mcPlugin.getConfig().getString("icon-url"));
        embed.addField("Reason", event.getOption("reason").getAsString(), false);
        embed.addField("Punishment ID", id, true);
        embed.addField("Duration", duration, true);
        embed.addField(WordUtils.capitalizeFully(type) + " By", sender.getAsMention(), true);
        embed.addField("", "Want to appeal this punishment? Go to " + mcPlugin.getConfig().getString("appeal-link") + " to start your appeal!", false);
        embed.setColor(Color.RED);

        CompletableFuture<Boolean> noticeSentFuture = new CompletableFuture<>();
        event.getOption("user").getAsUser().openPrivateChannel().queue(
                (channel) -> {
                    channel.sendMessageEmbeds(embed.build()).queue(
                        success -> {
                            // The embed was successfully sent
                            noticeSentFuture.complete(true);
                        },
                        failure -> {
                            // The embed failed to be delivered
                            noticeSentFuture.complete(false);
                        }
                    );
                });
        try {
            noticeSent = noticeSentFuture.get();
        } catch (Exception e) {
            // Handle any exceptions that occurred during the future retrieval
            e.printStackTrace();
        }
    }
    private void setNoticeSent(boolean result) {
        this.noticeSent = result;
    }
    public void sendExpires(User target, String id, long duration) {
        EmbedBuilder expires = new EmbedBuilder();
        expires.setColor(Color.RED);
        expires.setDescription("Punishment " + id + " expires <t:" + (event.getTimeCreated().toEpochSecond() + duration) + ":R>");
        target.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessageEmbeds(expires.build()).submit();
        });
    }

    public void sendLog() {}

}
