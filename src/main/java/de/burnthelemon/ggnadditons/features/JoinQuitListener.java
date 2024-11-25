package de.burnthelemon.ggnadditons.features;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.commands.ResourcePackCommand;
import de.burnthelemon.ggnadditons.hooks.database.DatabaseHandler;
import de.burnthelemon.ggnadditons.hooks.discordBridge.DiscordManager;
import de.burnthelemon.ggnadditons.hooks.database.PlayerDataManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;
import java.util.logging.Level;

public class JoinQuitListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final ResourcePackCommand resourcePackCommand = new ResourcePackCommand();

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 30 * 1000L; // 2 minutes in milliseconds
    DiscordManager discordManager = DiscordManager.getInstance();

    public JoinQuitListener() {
        this.playerDataManager = new PlayerDataManager(new DatabaseHandler()); // Instantiate DatabaseManager here
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        resourcePackCommand.applySubscribedResourcePacks(e.getPlayer());

        String playerName = playerDataManager.getPlayerName(e.getPlayer().getUniqueId().toString()) == null ? MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) : playerDataManager.getPlayerName(e.getPlayer().getUniqueId().toString());
        e.getPlayer().displayName(MiniMessage.miniMessage().deserialize(playerName));


        e.joinMessage(MiniMessage.miniMessage().deserialize("<gray>[<green>+</green>]<yellow> " + MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) + "<reset><yellow> joined the game"));

        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (player.hasPlayedBefore()) {
            // Check if the player is on cooldown
            if (cooldowns.containsKey(playerUUID)) {
                long lastExecutionTime = cooldowns.get(playerUUID);
                if (currentTime - lastExecutionTime < COOLDOWN_TIME) {
                    return; // Still on cooldown, so don't run the task
                }
            }

            // Update the cooldown time
            cooldowns.put(playerUUID, currentTime);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gray>[<gradient:green:yellow>Toucan</gradient>] <reset><gray>Welcome back, "
                                    + PlainTextComponentSerializer.plainText().serialize(player.displayName()))));
                }
            }.runTaskLater(Main.getPlugin(), 2 * 20L); // 2 seconds delay
        }

        discordManager.uniquePlayers.add(e.getPlayer().getUniqueId());
        discordManager.updateDiscordStatus();


        discordManager.sendDiscordFullEmbed(e.getPlayer(), "Player Joined", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().name()) + " has joined the Server", Color.GREEN);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.quitMessage(MiniMessage.miniMessage().deserialize("<gray>[<red>-</red>]<yellow> " + MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) + "<reset><yellow> left the game"));

        discordManager.sendDiscordFullEmbed(e.getPlayer(), "Player Left", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().name()) + " has left the Server", Color.RED);
        discordManager.updateDiscordStatus();
    }

    @EventHandler
    public void preJoinEvent(PlayerLoginEvent e) {
        Main.getPlugin().getLogger().log(Level.INFO,e.getPlayer().getName() + " Tried to join the Server but the Server was not ready yet.");

        if (!discordManager.isDiscordReady) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, MiniMessage.miniMessage().deserialize("<red><b>Failed to Connect\n</b><gray>The Server is not ready yet."));
            discordManager.sendDiscordFullEmbed(e.getPlayer(), "Warning", e.getPlayer().getName() + " has tried to join but was denied\nThe Server was not ready yet. ", Color.RED);
        }
    }
}
