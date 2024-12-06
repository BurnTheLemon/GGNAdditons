package de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class BaseNamePattern implements PlayerNamePattern {
    @Override
    public Component apply(Player player, String newName) {
        return MiniMessage.miniMessage().deserialize(newName);
    }
}
