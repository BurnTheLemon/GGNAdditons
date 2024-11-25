package de.burnthelemon.ggnadditons;

import java.util.logging.Level;

import de.burnthelemon.ggnadditons.commands.*;
import de.burnthelemon.ggnadditons.commands.commandhandler.CommandManager;
import de.burnthelemon.ggnadditons.features.HUD.TabDisplayFormatter;
import de.burnthelemon.ggnadditons.features.JoinQuitListener;
import de.burnthelemon.ggnadditons.features.badgeSystem.BadgeConfig;
import de.burnthelemon.ggnadditons.features.chatSystem.ChatExecutor;
import de.burnthelemon.ggnadditons.features.chatSystem.ChatModeCommand;
import de.burnthelemon.ggnadditons.features.chatSystem.PlaySoundCommand;
import de.burnthelemon.ggnadditons.features.chatSystem.PlayerRenameCommand;
import de.burnthelemon.ggnadditons.features.secretStuff.SizeStick;
import de.burnthelemon.ggnadditons.hooks.discordBridge.DiscordAdvancementHandler;
import de.burnthelemon.ggnadditons.hooks.discordBridge.DiscordDeathListener;
import de.burnthelemon.ggnadditons.config.DefaultConfig;
import de.burnthelemon.ggnadditons.hooks.discordBridge.DiscordManager;
import de.burnthelemon.ggnadditons.features.badgeSystem.BadgeCommand;
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
      TabDisplayFormatter.startPingScheduler();
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
      commandManager.registerCommand(DefinePropertiesCommand.class);


      getCommand("msg").setExecutor(new DirectMessageCommand());
      getCommand("roll").setExecutor(new DiceCommand());
      getCommand("modifyitem").setExecutor(new ModifyItemCommand());
      getCommand("name").setExecutor(new PlayerRenameCommand());
      getCommand("badge").setExecutor(new BadgeCommand());
      getCommand("ping").setExecutor(new CheckPingCommand());
      getCommand("sound").setExecutor(new PlaySoundCommand());

      getCommand("ressourcepackmanager").setExecutor(new RessourcePackManagerCommand());
      getCommand("ressourcepack").setExecutor(new ResourcePackCommand());
      getCommand("chatmode").setExecutor(new ChatModeCommand());


      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Commands Registered");
   }

   public void registerEvents() {
      this.getServer().getLogger().log(Level.INFO, "[GGNAdditons] => Events Registered");
      getServer().getPluginManager().registerEvents(new JoinQuitListener(),this);
      getServer().getPluginManager().registerEvents(new DiscordAdvancementHandler(), this);
      getServer().getPluginManager().registerEvents(new DiscordDeathListener(), this);
      getServer().getPluginManager().registerEvents(new ChatExecutor(),this);
      getServer().getPluginManager().registerEvents(new TabDisplayFormatter(),this);
      getServer().getPluginManager().registerEvents(new SizeStick(),this);
      discordManager.sendStartupMessage(); // Send Message Discord Integration is live.

   }

   public static Main getPlugin() {
      return plugin;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

}
