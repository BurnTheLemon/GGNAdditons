package de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface PlayerNamePattern {
    Component apply(Player player, String newName);
}
