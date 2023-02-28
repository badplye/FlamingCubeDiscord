package com.oceanusmc.dev.badplye.oceanusdiscord.Utilities;

import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class PlayerFetcher {
    public static UUID getUUID(String name) {
        MojangProfile profile = null;
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            profile = gson.fromJson(response.toString(), MojangProfile.class);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        String input = profile.getId();
        String output = input.substring(0, 8) + "-" + input.substring(8, 12) + "-" + input.substring(12, 16) + "-" + input.substring(16, 20) + "-" + input.substring(20);
        return UUID.fromString(output);
    }
    public static boolean isVanished(Player player) {
        boolean vanished = false;

        // Remember, there might be more than one plugin that adds the "vanished" metadata. We want to consider them all
        for (MetadataValue value : player.getMetadata("vanished")) {
            vanished |= value.asBoolean(); // But we only care if AT LEAST one of them is true.
        }

        return vanished;
    }
}
class MojangProfile {
    private String id;
    private String name;

    public String getId() {
        return id;
    }
}
