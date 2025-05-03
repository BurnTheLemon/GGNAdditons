package de.burnthelemon.zestkit.features.chat;

import de.burnthelemon.zestkit.hooks.database.MembershipDatabase;
import de.burnthelemon.zestkit.util.TeamPrefix;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

    public class ChatEventHandler implements Listener {

        MembershipDatabase membershipDatabase = new MembershipDatabase();

    @EventHandler
    public void handle(AsyncChatEvent event) {
        //
        Player player = event.getPlayer();
        Component chatMessage = event.message();
        Component getPlayerTeamPrefix = MiniMessage.miniMessage().deserialize("<white>" + TeamPrefix.playerPrefix(player) + "</white>");
        int getPlayerRange = membershipDatabase.getPlayerChatRange(player.getUniqueId());
        //

        //
    }
}
