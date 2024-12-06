package de.burnthelemon.ggnadditons.features.HUD.__archive__.tabDisplay;

import de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName.PlayerNamePattern;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PingTabDecorator implements TabDisplayDecorator {
    private final TabDisplayDecorator decorated;
    private final PlayerNamePattern playerNamePattern;

    public PingTabDecorator(TabDisplayDecorator decorated, PlayerNamePattern playerNamePattern) {
        this.decorated = decorated;
        this.playerNamePattern = playerNamePattern;
    }

    @Override
    public void setCustomTab(Player player) {
        decorated.setCustomTab(player);
        setPlayerPingInHud(player);
    }

    private void setPlayerPingInHud(Player player) {
        // Use the pattern to apply the ping suffix
        Component modifiedName = playerNamePattern.apply(player, player.displayName().toString());
        player.playerListName(modifiedName);
    }
}
