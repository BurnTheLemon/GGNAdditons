package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import de.burnthelemon.ggnadditons.util.TabMODT;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Random;

public class HUDCosmetics implements Listener {

    HashMap<Player,Integer> playerHudNumber = new HashMap<>();

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        setCustomTab(e.getPlayer());
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
        Integer randomMODTNumber = random.nextInt(TabMODT.customMOTD.size());
        playerHudNumber.put(player,randomMODTNumber);

        String color1 = generateRandomHexColor();
        String color2 = generateRandomHexColor();

        //Set footer
        player.sendPlayerListFooter(MiniMessage.miniMessage().deserialize(
        "<gradient:" + color1 + ":" + color2 + ">" +
                TabMODT.customMOTD.get(randomMODTNumber) +
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

}
