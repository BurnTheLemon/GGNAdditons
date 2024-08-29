package de.burnthelemon.ggnadditons.config;


import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.HashSet;

public class BACKUP_Chat implements Listener {

   FileConfiguration config = Main.getPlugin().getDefaultConfig().getCfg();
   LuckPerms api = LuckPermsProvider.get();
   CustomFormattingTags customFormattingTags = new CustomFormattingTags();

   @EventHandler
   public void onChat(AsyncChatEvent e) {
       //Base Vars
       Player player = e.getPlayer();
       String teamPrefix = api.getGroupManager().getGroup(
               api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getDisplayName() == null ?
                    api.getGroupManager().getGroup(api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getName() :
                    api.getGroupManager().getGroup(api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getDisplayName();
       String channelGroupIcon = "<hover:show_text:'Global Chat'>\ue005</hover>";

       //Assembling the Raw Message
       Component message = e.message();
       String username = null;

       //Raw message
       String stringMessage = LegacyComponentSerializer.legacyAmpersand().serialize(message);

       //Global message layout
       Component chatMessage = MiniMessage.miniMessage().deserialize(
               channelGroupIcon + "<hover:show_text:'"+player.getName()+"'> " +
                       MiniMessage.miniMessage().serialize(player.displayName()) + "</hover><reset> " +
                       MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(teamPrefix == null ? "" : teamPrefix)) +
                       "<reset>> " +
                       stringMessage,customFormattingTags.getCustomTags()
       );

       //Private Message Check
       if(stringMessage.startsWith("@")) {
            if(stringMessage.contains(" "))
                username = stringMessage.substring(1, stringMessage.indexOf(" "));
        }

        //Private Message
       if(username != null && Bukkit.getPlayer(username) != null) {
           Player user = Bukkit.getPlayer(username);
           String dmString = stringMessage.substring(stringMessage.indexOf(" ") + 1);

           //Private Message Layout
           Component dmMessage = MiniMessage.miniMessage().deserialize(
                   "<gray>[<yellow>" +
                           MiniMessage.miniMessage().serialize(player.displayName()) +
                           "<gray>-></gray>" +
                           MiniMessage.miniMessage().serialize(user.displayName()) +
                           "</yellow>]<reset> " +
                           dmString,customFormattingTags.getCustomTags()
           );

           //Send message only to user and sender
           user.sendMessage(dmMessage);
           player.sendMessage(dmMessage);
           e.setCancelled(true);

       } else
           e.renderer((cplayer, cdisplayName, coreMessage, cviewer) ->
                   chatMessage
           );
   }


   public static Collection<Player> players(Location location, double radius) {
      double sqrRadius = radius * radius;
      World world = location.getWorld();
      Collection<Player> result = new HashSet<>();

       for (Player player : world.getPlayers()) {
           if (player.getLocation().distanceSquared(location) <= sqrRadius) {
               result.add(player);
           }
       }

      return result;
   }









   /*
   NORMAL
    */
}
