package de.burnthelemon.ggnadditons;

import java.util.logging.Level;

import de.burnthelemon.ggnadditons.commands.*;
import de.burnthelemon.ggnadditons.commands.commandhandler.CommandManager;
import de.burnthelemon.ggnadditons.config.BadgeConfig;
import de.burnthelemon.ggnadditons.listener.*;
import de.burnthelemon.ggnadditons.config.DefaultConfig;
import de.burnthelemon.ggnadditons.util.DiscordManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
   private static Main plugin;
   private DefaultConfig defaultConfig;
   private BadgeConfig badgeConfig;
   private CommandManager commandManager;
   DiscordManager discordManager;

   public void onEnable() {
      plugin = this;
      this.defaultConfig = new DefaultConfig();
      this.badgeConfig = new BadgeConfig();
      this.defaultConfig.createFile();
      this.badgeConfig.createFile();
      discordManager = DiscordManager.getInstance();

      registerCommands();
      registerEvents();
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => PLUGIN OK");
      HUDCosmetics.startPingScheduler();
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => PING SCHEDULER OK");
   }

   public void onDisable() {
      // Check if the plugin being disabled is DiscordSRV
      if (plugin.getName().equals("DiscordSRV")) {
         discordManager.sendShutdownMessage();
      }

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
      getCommand("ping").setExecutor(new Ping());
      getCommand("sound").setExecutor(new SoundCommand());

      getCommand("ressourcepackmanager").setExecutor(new RessourcePackManagerCommand());
      getCommand("ressourcepack").setExecutor(new ResourcePackCommand());


      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Commands Registered");
   }

   public void registerEvents() {
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Events Registered");
      getServer().getPluginManager().registerEvents(new JoinQuitListener(),this);
      getServer().getPluginManager().registerEvents(new DiscordAdvancementHandler(), this);
      getServer().getPluginManager().registerEvents(new DeathListener(), this);
      getServer().getPluginManager().registerEvents(new Chat(),this);
      getServer().getPluginManager().registerEvents(new HUDCosmetics(),this);
      getServer().getPluginManager().registerEvents(new SizeStick(),this);
      getServer().getPluginManager().registerEvents(new ShulkerBoxPackager(),this);
      discordManager.sendStartupMessage(); // Send Message Discord Integration is live.

   }

   public static Main getPlugin() {
      return plugin;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

}
