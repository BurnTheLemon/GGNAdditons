package de.burnthelemon.ggnadditons.features.HUD;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TabDisplayFormatter implements Listener {

    private static final int PING_UPDATE_INTERVAL_TICKS = 60; // 3 seconds
    private static final String TAB_HEADER = "<newline><newline><newline><newline><newline><newline>\uE004";

    private final Map<Player, Integer> playerHudNumbers = new HashMap<>();
    private final Random random = new Random();

    public TabDisplayFormatter() {
        startPingScheduler();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setCustomTab(player);
        updatePlayerPingInTab(player);
    }

    /**
     * Sets a custom tab header and footer for the given player.
     *
     * @param player The player whose tab should be customized.
     */
    public void setCustomTab(Player player) {
        // Set header
        player.sendPlayerListHeader(deserializeMessage(TAB_HEADER));

        // Store a semi-persistent random value for the player
        int randomMotdIndex = random.nextInt(TabMessageOfTheDayInputList.customMOTD.size());
        playerHudNumbers.put(player, randomMotdIndex);

        // Generate random gradient colors
        String color1 = generateRandomHexColor();
        String color2 = generateRandomHexColor();

        // Replace placeholders and set footer
        String footer = replacePlaceholders(
                TabMessageOfTheDayInputList.customMOTD.get(randomMotdIndex),
                player
        );
        player.sendPlayerListFooter(deserializeMessage(
                String.format("<gradient:%s:%s>%s</gradient>", color1, color2, footer)
        ));
    }

    /**
     * Gets the custom MOTD index for the player.
     *
     * @param player The player.
     * @return The MOTD index.
     */
    public int getCustomMotdIndex(Player player) {
        return playerHudNumbers.getOrDefault(player, -1);
    }

    /**
     * Starts a periodic task to update player pings in the tab list.
     */
    public static void startPingScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(TabDisplayFormatter::updatePlayerPingInTab);
            }
        }.runTaskTimer(Main.getPlugin(), 0L, PING_UPDATE_INTERVAL_TICKS);
    }

    /**
     * Updates the player's ping display in the tab list.
     *
     * @param player The player whose ping should be updated.
     */
    private static void updatePlayerPingInTab(Player player) {
        player.playerListName(
                player.displayName().append(
                        MiniMessage.miniMessage().deserialize("<white> " + player.getPing() + "ms<reset>")
                )
        );
    }

    /**
     * Replaces placeholders in the MOTD text.
     *
     * @param text   The text with placeholders.
     * @param player The player whose data will be used for replacement.
     * @return The text with placeholders replaced.
     */
    private String replacePlaceholders(String text, Player player) {
        return text
                .replaceAll("%p", "<white>" + PlainTextComponentSerializer.plainText().serialize(player.displayName()) + "</white>")
                .replaceAll("%st_deaths", "<white> " + player.getStatistic(Statistic.DEATHS) + " </white>")
                .replaceAll("%st_pkills", "<white> " + player.getStatistic(Statistic.PLAYER_KILLS) + " </white>")
                .replaceAll("%st_mkills", "<white> " + player.getStatistic(Statistic.MOB_KILLS) + " </white>")
                .replaceAll("%st_timesincedeath", "<white> " + player.getStatistic(Statistic.TIME_SINCE_DEATH) + " </white>");
    }

    /**
     * Generates a random hex color code.
     *
     * @return A random hex color code.
     */
    private String generateRandomHexColor() {
        return String.format("#%02x%02x%02x", random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * Deserializes a MiniMessage string with custom tags.
     *
     * @param message The message string.
     * @return The deserialized message.
     */
    private static net.kyori.adventure.text.Component deserializeMessage(String message) {
        return MiniMessage.miniMessage().deserialize(message, CustomFormattingTags.getCustomTags());
    }
}
