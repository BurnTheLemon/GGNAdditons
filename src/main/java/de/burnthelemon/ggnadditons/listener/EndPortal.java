package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public class EndPortal implements Listener {
   FileConfiguration config = Main.getPlugin().getDefaultConfig().getCfg();
   String prefix;

   public EndPortal() {
      this.prefix = this.config.get("plugin.prefix").toString();
   }

   @EventHandler
   public void onTravelThroughPortal(EntityPortalEvent e) {
      if (!(e.getEntity() instanceof Player) && e.getEntity().getWorld().getName().equals("GGN5_the_end")) {
         if (this.config.getLocation("world.endreturnpoint") == null) {
            return;
         }

         e.getEntity().teleport(this.config.getLocation("world.endreturnpoint"));
         e.setCancelled(true);
      }

   }
}
