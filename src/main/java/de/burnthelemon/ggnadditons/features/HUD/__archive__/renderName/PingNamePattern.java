package de.burnthelemon.ggnadditons.features.HUD.__archive__.renderName;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PingNamePattern implements PlayerNamePattern {
    @Override
    public Component apply(Player player, String baseName) {
        // Base name + ping suffix
        String pingSuffix = " <white>" + player.getPing() + "ms<reset>";
        return MiniMessage.miniMessage().deserialize(baseName + pingSuffix);
    }
}
