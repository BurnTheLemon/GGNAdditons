package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.commands.ResourcePackCommand;
import de.burnthelemon.ggnadditons.util.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.Bukkit.getServer;

public class JoinQuitListener implements Listener {
    private final DatabaseManager databaseManager;
    private Set<String> onlinePlayers = new HashSet<>();
    private final ResourcePackCommand resourcePackCommand = new ResourcePackCommand();

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 30 * 1000L; // 2 minutes in milliseconds


    public JoinQuitListener() {
        this.databaseManager = new DatabaseManager(); // Instantiate DatabaseManager here
        Bukkit.getOnlinePlayers().forEach(p -> onlinePlayers.add("@"+MiniMessage.miniMessage().serialize(p.name())));
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerAutoComplete);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        resourcePackCommand.applySubscribedResourcePacks(e.getPlayer());

        String playerName = databaseManager.getPlayerName(e.getPlayer().getUniqueId().toString()) == null ? MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) : databaseManager.getPlayerName(e.getPlayer().getUniqueId().toString());
        e.getPlayer().displayName(MiniMessage.miniMessage().deserialize(playerName));
        onlinePlayers.add("@"+MiniMessage.miniMessage().serialize(e.getPlayer().name()));


        e.joinMessage(MiniMessage.miniMessage().deserialize("<gray>[<green>+</green>]<yellow> " + MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) + "<reset><yellow> joined the game"));
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerAutoComplete);


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
                            "<gray>[<blue>G</blue><red>G</red><green>N</green>] <reset><gray>Welcome back, "
                                    + PlainTextComponentSerializer.plainText().serialize(player.displayName()))));
                }
            }.runTaskLater(Main.getPlugin(), 2 * 20L); // 2 seconds delay
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.quitMessage(MiniMessage.miniMessage().deserialize("<gray>[<red>-</red>]<yellow> " + MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) + "<reset><yellow> left the game"));
        onlinePlayers.remove("@"+MiniMessage.miniMessage().serialize(e.getPlayer().name()));
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerAutoComplete);



    }

    public void refreshPlayerAutoComplete(Player player) {
        player.removeCustomChatCompletions(onlinePlayers);
        player.addCustomChatCompletions(onlinePlayers);
        //player.sendMessage(Arrays.toString(onlinePlayers.toArray()));
    }
}
