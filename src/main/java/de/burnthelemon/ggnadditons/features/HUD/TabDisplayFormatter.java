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
import java.util.Random;

public class TabDisplayFormatter implements Listener {

    HashMap<Player,Integer> playerHudNumber = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        setCustomTab(e.getPlayer());
        setPlayerPingInHud(e.getPlayer());
    }


    public void refreshCustomTab(Player player) {
        //empty for now
    }

    public void setCustomTab(Player player) {
        //Set header
        player.sendPlayerListHeader(MiniMessage.miniMessage().deserialize(
                "<newline><newline><newline><newline><newline><newline>" +
                        "\uE004"
                , CustomFormattingTags.getCustomTags()));

        //calculation for storing a semi-persistent value
        Random random = new Random();
        int randomMODTNumber = random.nextInt(TabMessageOfTheDayInputList.customMOTD.size());
        playerHudNumber.put(player,randomMODTNumber);

        String color1 = generateRandomHexColor();
        String color2 = generateRandomHexColor();

        //Set footer
        player.sendPlayerListFooter(MiniMessage.miniMessage().deserialize(
        "<gradient:" + color1 + ":" + color2 + ">" +
                TabMessageOfTheDayInputList.customMOTD.get(randomMODTNumber)
                        .replaceAll("%p","<white>" + PlainTextComponentSerializer.plainText().serialize(player.displayName()) + "</white>")
                        .replaceAll("%st_deaths","<white> " + player.getStatistic(Statistic.DEATHS) + " </white>")
                        .replaceAll("%st_pkills","<white> " + player.getStatistic(Statistic.PLAYER_KILLS) + " </white>")
                        .replaceAll("%st_mkills","<white> " + player.getStatistic(Statistic.MOB_KILLS) + " </white>")
                        .replaceAll("%st_timesincedeath","<white> " + player.getStatistic(Statistic.TIME_SINCE_DEATH) + " </white>") +
                "</gradient>"
        ,CustomFormattingTags.getCustomTags()));
    }

    public int getCustomTab(Player player) {
        return playerHudNumber.get(player);
    }

    private String generateRandomHexColor() {
        Random random = new Random();

        // Generate random values for Red, Green, and Blue components (0-255)
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // Convert RGB values to a hex color code
        return String.format("#%02x%02x%02x", red, green, blue);
    }

    public static void startPingScheduler() {
        Bukkit.getOnlinePlayers().forEach(TabDisplayFormatter::setPlayerPingInHud);
        new BukkitRunnable() {
            @Override
            public void run() {
                // Code to run every 5 seconds
                Bukkit.getOnlinePlayers().forEach(TabDisplayFormatter::setPlayerPingInHud);
            }
        }.runTaskTimer(Main.getPlugin(), 0L, 60L); // 100 ticks = 5 seconds
    }

    private static void setPlayerPingInHud(Player p) {
        p.playerListName(p.displayName().append(MiniMessage.miniMessage().deserialize("<white> " + p.getPing() + "ms<reset>")));
    }

}
