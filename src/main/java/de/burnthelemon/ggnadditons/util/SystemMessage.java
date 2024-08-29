package de.burnthelemon.ggnadditons.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public abstract class SystemMessage {

    public static void sendServerMessage(Player player, Component component) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:red:green>[GGN]</gradient> ").append(component));
    }
}
