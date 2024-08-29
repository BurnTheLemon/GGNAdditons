package de.burnthelemon.ggnadditons;

import java.util.logging.Level;

import de.burnthelemon.ggnadditons.commands.*;
import de.burnthelemon.ggnadditons.commands.commandhandler.CommandManager;
import de.burnthelemon.ggnadditons.config.BadgeConfig;
import de.burnthelemon.ggnadditons.listener.Chat;
import de.burnthelemon.ggnadditons.config.DefaultConfig;
import de.burnthelemon.ggnadditons.listener.HUDCosmetics;
import de.burnthelemon.ggnadditons.listener.JoinQuitListener;
import de.burnthelemon.ggnadditons.listener.SizeStick;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
   private static Main plugin;
   private DefaultConfig defaultConfig;
   private BadgeConfig badgeConfig;
   private CommandManager commandManager;


   public void onEnable() {
      plugin = this;
      this.defaultConfig = new DefaultConfig();
      this.badgeConfig = new BadgeConfig();
      this.defaultConfig.createFile();
      this.badgeConfig.createFile();

      registerCommands();
      registerEvents();
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => PLUGIN OK");

   }

   public void onDisable() {
      Chat.sendShutdownMessage();
      getPlugin().getLogger().log(Level.INFO,"Plugin is disabling itself.");
   }

   public void registerCommands() {
      commandManager = new CommandManager(this);
      commandManager.registerCommand(TestCommand.class);
      commandManager.registerCommand(Admin.class);


      getCommand("msg").setExecutor(new DirectMessageCommand());
      getCommand("roll").setExecutor(new Dice());
      getCommand("renameitem").setExecutor(new RenameItem());
      getCommand("modifyitem").setExecutor(new ModifyItem());
      getCommand("name").setExecutor(new Rename());
      getCommand("badge").setExecutor(new BadgeCommand());

      getCommand("ressourcepackmanager").setExecutor(new RessourcePackManagerCommand());
      getCommand("ressourcepack").setExecutor(new ResourcePackCommand());


      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Commands Registered");
   }

   public void registerEvents() {
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Events Registered");
      getServer().getPluginManager().registerEvents(new JoinQuitListener(),this);
      getServer().getPluginManager().registerEvents(new Chat(),this);
      getServer().getPluginManager().registerEvents(new HUDCosmetics(),this);
      getServer().getPluginManager().registerEvents(new SizeStick(),this);

   }

   public static Main getPlugin() {
      return plugin;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

}
