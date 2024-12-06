package de.burnthelemon.ggnadditons.features.HUD.__archive__.tabDisplay;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

public class HudNumberTabDecorator implements TabDisplayDecorator {
    private final TabDisplayDecorator decorated;
    private final HashMap<Player, Integer> playerHudNumber = new HashMap<>();

    public HudNumberTabDecorator(TabDisplayDecorator decorated) {
        this.decorated = decorated;
    }

    @Override
    public void setCustomTab(Player player) {
        decorated.setCustomTab(player);
        setHudNumber(player);
    }

    private void setHudNumber(Player player) {
        Random random = new Random();
        int randomNumber = random.nextInt(100); // Example logic for HUD number
        playerHudNumber.put(player, randomNumber);
    }

    public int getHudNumber(Player player) {
        return playerHudNumber.getOrDefault(player, -1);
    }
}
