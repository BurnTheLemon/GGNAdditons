package de.burnthelemon.zestkit.features.nicknames;

import de.burnthelemon.zestkit.hooks.database.PlayerDatabase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NickNameHandler implements Listener {
    private final PlayerDatabase playerDataManager = new PlayerDatabase();

    @EventHandler
    public void applyJoin(PlayerJoinEvent e) {
        String nickName = playerDataManager.getPlayerNickname(e.getPlayer().getUniqueId());
        if(nickName != null) {
            e.getPlayer().displayName(MiniMessage.miniMessage().deserialize(nickName));
        }
        e.joinMessage(MiniMessage.miniMessage().deserialize(
                "<gray>[<green>+</green>]<yellow> " +
                        MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) +
                        "<reset><yellow> joined the game"
        ));
    }

    @EventHandler
    public void applyLeave(PlayerQuitEvent e) {
        String nickName = playerDataManager.getPlayerNickname(e.getPlayer().getUniqueId());
        if(nickName != null) {
            e.getPlayer().displayName(MiniMessage.miniMessage().deserialize(nickName));
        }
        e.quitMessage(MiniMessage.miniMessage().deserialize(
                "<gray>[<red>-</red>]<yellow> " +
                        MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) +
                        "<reset><yellow> joined the game"
        ));
    }
}
