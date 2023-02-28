package com.oceanusmc.dev.badplye.oceanusdiscord.Modules.Moderation;

import com.oceanusmc.dev.badplye.oceanusdiscord.Logs.Moderation;
import com.oceanusmc.dev.badplye.oceanusdiscord.OceanusDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Mute extends ListenerAdapter {
    private final OceanusDiscord mcPlugin;
    public Mute(OceanusDiscord oceanusDiscord){
        this.mcPlugin = oceanusDiscord;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // check if the command is "mute"
        if (!event.getName().equalsIgnoreCase("mute")) {
            return;
        }
        event.deferReply().queue();
        EmbedBuilder response = new EmbedBuilder();

        // get the user who sent the command and the guild they sent it in
        User sender = event.getUser();
        Guild guild = event.getGuild();
        // get the target user, ban duration, and reason for the ban
        User target = event.getOption("user").getAsUser();

        // check if the user has permission to punish
        List<Role> senderRoles = event.getMember().getRoles();
        List<Role> targetRoles = event.getOption("user").getAsMember().getRoles();
        if(senderRoles.get(0).getPosition() <= targetRoles.get(0).getPosition()) {
            response.setTitle("Permission Error");
            response.setDescription("This user has a equal or higher rank than you!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }

        boolean exempt = false;
        for(Role role : senderRoles) {
            if(mcPlugin.getConfig().getString("proof-exempt").contains(role.getId())) {
                exempt = true;
            }
        }
        if(event.getOption("proof") == null && exempt != true) {
            response.setTitle("Permission Error");
            response.setDescription("Your rank requires proof to be included in this punishment!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }

        List<Role> mutedRoles = guild.getRolesByName("Muted", false);
        if (mutedRoles.isEmpty()) {
            response.setTitle("Runtime Error");
            response.setDescription("No muted role exists!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }


        String reason = event.getOption("reason").getAsString();

        Random random = new Random();
        String id = "D" + String.format("%05d", System.currentTimeMillis()) + String.format("%02d", random.nextInt(100));

        // create an embed message containing the ban information

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Punishment Notice");
        embed.setDescription("You have been **muted** in the " + guild.getName() + " discord!");
        embed.setThumbnail(mcPlugin.getConfig().getString("icon-url"));
        embed.setFooter("Want to appeal this punishment? Go to " + mcPlugin.getConfig().getString("appeal-link") + " to start your appeal!", "https://cdn.discordapp.com/attachments/774074424315215872/1055979750973247498/m128.png");
        embed.addField("Reason", event.getOption("reason").getAsString(), false);
        embed.addField("Punishment ID", id, true);
        embed.addField("Duration", "Permanent", true);
        embed.addField("Muted By", sender.getAsMention(), true);
        embed.setColor(Color.RED);

        // send the embed message to the target user
        event.getOption("user").getAsUser().openPrivateChannel().queue((channel) ->
        {
            channel.sendMessageEmbeds(embed.build()).submit();
        });

        // ban the target user
        if (!mutedRoles.isEmpty()) {
            Role mutedRole = mutedRoles.get(0);
            guild.addRoleToMember(target, mutedRole).queue();
        }

        Guild staffDiscord = event.getJDA().getGuildById(mcPlugin.getConfig().getString("staff-guild"));
        TextChannel logChannel = staffDiscord.getTextChannelById(mcPlugin.getConfig().getString("mod-log"));

        if(!exempt || event.getOption("proof") != null) {
            logChannel.sendMessageEmbeds(Moderation.staffLog(mcPlugin, id, "Mute", "Permanent", target, sender, event.getOption("reason").getAsString(), event.getOption("proof").getAsString()).build()).queue();
        } else {
            logChannel.sendMessageEmbeds(Moderation.staffLog(mcPlugin, id, "Mute", "Permanent", target, sender, event.getOption("reason").getAsString()).build()).queue();

        }

        // send a message to the channel to confirm that the user was banned
        response.setTitle("Success");
        response.setDescription(target.getName() + " has been muted!");
        response.setColor(Color.GREEN);
        event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
    }
}

