package de.burnthelemon.ggnadditons.config;

import de.burnthelemon.ggnadditons.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DefaultConfig {
   private final File file = new File(Main.getPlugin().getDataFolder().getPath(), "default.yml");
   private final FileConfiguration cfg;

   public DefaultConfig() {
      this.cfg = YamlConfiguration.loadConfiguration(this.file);
   }

   public void createFile() {
      if (!this.file.exists()) {
         try {
            this.file.getParentFile().mkdir();
            this.file.createNewFile();
         } catch (IOException var2) {
            var2.printStackTrace();
         }
      }

      this.addDefaults();
   }

   private void addDefaults() {
      this.cfg.options().copyDefaults(true);
      this.cfg.addDefault("plugin.prefix", "<white>[<gradient:blue:red:green>GGN<white>] <white>");
   }

   public void save() {
      try {
         this.cfg.save(this.file);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public FileConfiguration getCfg() {
      return this.cfg;
   }
}
