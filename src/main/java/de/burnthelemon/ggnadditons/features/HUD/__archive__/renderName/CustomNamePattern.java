package de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class CustomNamePattern implements PlayerNamePattern {
    private final String prefix;
    private final String suffix;

    public CustomNamePattern(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public Component apply(Player player, String newName) {
        return MiniMessage.miniMessage().deserialize(prefix + newName + suffix);
    }
}
