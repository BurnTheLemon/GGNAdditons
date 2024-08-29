package de.burnthelemon.ggnadditons.config;

import de.burnthelemon.ggnadditons.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BadgeConfig {
   private final File file = new File(Main.getPlugin().getDataFolder(), "badges.yml");
   private final FileConfiguration cfg;

   public BadgeConfig() {
      this.cfg = YamlConfiguration.loadConfiguration(this.file);
      createFile();
   }

   public void createFile() {
      if (!this.file.exists()) {
         try {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
            addDefaults();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private void addDefaults() {
      FileConfiguration config = getCfg();
      // Set up default structure if needed
      if (!config.contains("badges")) {
         config.createSection("badges");
      }
      save();
   }

   public void save() {
      try {
         this.cfg.save(this.file);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public FileConfiguration getCfg() {
      return this.cfg;
   }

   public void addBadge(String name, String hoverText, String text, boolean locked) {
      FileConfiguration config = getCfg();
      Map<String, Object> badgeData = new HashMap<>();
      badgeData.put("hoverText", hoverText);
      badgeData.put("text", text);
      badgeData.put("locked", locked);
      config.set("badges." + name, badgeData);
      save();
   }

   public void removeBadge(String name) {
      FileConfiguration config = getCfg();
      config.set("badges." + name, null);
      save();
   }

   public boolean badgeExists(String name) {
      return getCfg().contains("badges." + name);
   }

   public Map<String, Object> getBadge(String name) {
      return getCfg().getConfigurationSection("badges." + name).getValues(false);
   }

   public Map<String, Map<String, Object>> getAllBadges() {
      Map<String, Map<String, Object>> badges = new HashMap<>();
      FileConfiguration config = getCfg();
      for (String key : config.getConfigurationSection("badges").getKeys(false)) {
         badges.put(key, getBadge(key));
      }
      return badges;
   }
}
