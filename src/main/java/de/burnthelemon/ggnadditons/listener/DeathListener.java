package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.util.DiscordManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.*;
import java.util.Objects;

public class DeathListener implements Listener {
    DiscordManager discordManager = DiscordManager.getInstance();;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String deathMessage = LegacyComponentSerializer.legacyAmpersand().serialize(Objects.requireNonNull(event.deathMessage()));

        discordManager.sendDiscordEmbed(player, deathMessage, player.getName() + " died at: \n" +
                        "World: " + player.getLocation().getWorld().getName() + "\n " +
                        "X: " + Math.round(player.getLocation().getX()) + " " +
                        "Y: " + Math.round(player.getLocation().getY()) + " " +
                        "Z: " + Math.round(player.getLocation().getZ())
                , Color.BLACK);
    }
}
