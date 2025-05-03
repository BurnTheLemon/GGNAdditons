package de.burnthelemon.zestkit.features.nicknames;

import de.burnthelemon.zestkit.hooks.database.PlayerDatabase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NickNameHandler implements Listener {
    private final PlayerDatabase playerDataManager = new PlayerDatabase();

    @EventHandler
    public void apply(PlayerJoinEvent e) {
        String nickName = playerDataManager.getPlayerNickName(e.getPlayer().getUniqueId());
        if(nickName != null) {
            e.getPlayer().displayName(MiniMessage.miniMessage().deserialize(nickName));
        }
    }
}
