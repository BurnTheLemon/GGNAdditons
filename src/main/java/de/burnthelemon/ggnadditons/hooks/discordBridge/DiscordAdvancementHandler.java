package de.burnthelemon.ggnadditons.hooks.discordBridge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.awt.*;

public class DiscordAdvancementHandler implements Listener {
    DiscordManager discordManager = DiscordManager.getInstance();

    // Helper method to determine if the advancement is related to crafting recipes
    private boolean isCraftingAdvancement(String path) {
        // List of crafting-related advancements (you can expand this list based on your needs)
        return path.contains("craft") || path.contains("recipe");
    }

    private String getAdvancementDisplayName(Advancement advancement) {
        // Get the display name as a Component
        Component displayNameComponent = advancement.displayName();

        // Convert the Component to a plain text string

        return PlainTextComponentSerializer.plainText().serialize(displayNameComponent);
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();

        // Get the key of the advancement
        NamespacedKey key = advancement.getKey();
        String namespace = key.getNamespace();
        String path = key.getKey();

        // Filter to ensure it's from the Minecraft namespace
        if (!namespace.equals("minecraft")) {
            return;
        }

        // Check if the advancement is related to crafting recipes
        if (isCraftingAdvancement(path)) {
            return;
        }

        // Send debugging messages
        //player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Advancement Key:</red> " + key.toString()));

        // Get the human-readable name of the advancement
        String advancementName = getAdvancementDisplayName(advancement);

        // Send the Discord embed
        discordManager.sendDiscordEmbed(player, "Advancement Earned", player.getName() + " has made the advancement: " + advancementName, Color.ORANGE);
    }


}
