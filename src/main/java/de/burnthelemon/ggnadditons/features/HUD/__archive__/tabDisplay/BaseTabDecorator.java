package de.burnthelemon.ggnadditons.features.HUD.__archive__.tabDisplay;

import org.bukkit.entity.Player;

public class BaseTabDecorator implements TabDisplayDecorator {
    @Override
    public void setCustomTab(Player player) {
        // No changes to the player's tab name; acts as a passthrough
        player.playerListName(player.displayName());
    }
}
