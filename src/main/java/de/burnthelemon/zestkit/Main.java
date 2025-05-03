package de.burnthelemon.zestkit;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.burnthelemon.zestkit.config.DefaultConfig;
import de.burnthelemon.zestkit.hooks.discordBridge.DiscordManager;
import de.burnthelemon.zestkit.util.LoggerUtility;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
   public static final String PLUGINPREFIX = "ZestKit";

   private static Main plugin;
   private DefaultConfig defaultConfig;
   DiscordManager discordManager;

   public void onEnable() {
      plugin = this;
      this.defaultConfig = new DefaultConfig();
      this.defaultConfig.createFile();
      discordManager = DiscordManager.getInstance();

      registerCommands();
      registerEvents();
      LoggerUtility.log(Level.INFO, "Plugin is enabled");
   }

   public void onDisable() {
      if (plugin.getName().equals("DiscordSRV")) {
         discordManager.sendShutdownMessage();
      }

      LoggerUtility.log(Level.INFO,"Plugin is shutting down...");
   }



   public void registerCommands() {
      try {
         //getCommand("msg").setExecutor(new DirectMessageCommand());
      } catch(Exception ex) {
         LoggerUtility.log(Level.SEVERE, "Fatal Error one of the following commands have not been initialized." + Arrays.toString(ex.getStackTrace()));
         return;
      }
      LoggerUtility.log(Level.INFO,"Command have been registered");
   }



   public void registerEvents() {
      try {
         //getServer().getPluginManager().registerEvents(new JoinQuitListener(),this);
      } catch(Exception ex) {
         LoggerUtility.log(Level.SEVERE, "Fatal Error one of the following events have not been initialized." + Arrays.toString(ex.getStackTrace()));
         return;
      }
      LoggerUtility.log(Level.INFO,"Events have been registered");
      discordManager.sendStartupMessage(); // Send Message Discord Integration is live.
   }



   public static Main getPlugin() {
      return plugin;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

}
