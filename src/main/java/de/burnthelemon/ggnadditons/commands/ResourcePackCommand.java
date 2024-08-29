package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class ResourcePackCommand implements CommandExecutor {

    private final FileConfiguration config;

    public ResourcePackCommand() {
        this.config = Main.getPlugin().getDefaultConfig().getCfg();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if(args.length == 0) {
            loadResourcePack(player,"default");
            return true;
        }

/*
        if (args.length == 1) {
            player.sendMessage("Usage: /resourcepack <load|unload|list|subscribe|unsubscribe> [name]");
            return true;
        }
*/
        String action = args[0].toLowerCase();

        switch (action) {
            case "load":
                if (args.length < 2) {
                    player.sendMessage("Usage: /resourcepack load <name>");
                    return true;
                }
                String loadName = args[1];
                if (isResourcePackActive(player, loadName)) {
                    player.sendMessage("Resource pack '" + loadName + "' is already active.");
                    return true;
                }
                if (loadResourcePack(player, loadName)) {
                    player.sendMessage("Resource pack '" + loadName + "' has been loaded.");
                } else {
                    player.sendMessage("Failed to load resource pack '" + loadName + "'. Please ensure it exists in the configuration.");
                }
                break;
            case "unload":
                if (args.length < 2) {
                    player.sendMessage("Usage: /resourcepack unload <name>");
                    return true;
                }
                String unloadName = args[1];
                if (unloadResourcePack(player, unloadName)) {
                    player.sendMessage("Resource pack '" + unloadName + "' has been unloaded.");
                } else {
                    player.sendMessage("Failed to unload resource pack '" + unloadName + "'. Please ensure it is currently loaded.");
                }
                break;
            case "subscribe":
                if (args.length < 2) {
                    player.sendMessage("Usage: /resourcepack subscribe <name>");
                    return true;
                }
                String subscribeName = args[1];
                if (subscribeResourcePack(player, subscribeName)) {
                    player.sendMessage("You have subscribed to resource pack '" + subscribeName + "'. It will be applied on every login.");
                } else {
                    player.sendMessage("Failed to subscribe to resource pack '" + subscribeName + "'. Please ensure it exists in the configuration.");
                }
                break;
            case "unsubscribe":
                if (args.length < 2) {
                    player.sendMessage("Usage: /resourcepack unsubscribe <name>");
                    return true;
                }
                String unsubscribeName = args[1];
                if (unsubscribeResourcePack(player, unsubscribeName)) {
                    player.sendMessage("You have unsubscribed from resource pack '" + unsubscribeName + "' please relog");
                } else {
                    player.sendMessage("Failed to unsubscribe from resource pack '" + unsubscribeName + "'. Please ensure it is currently subscribed.");
                }
                break;
            case "list":
                handleListCommand(player);
                break;
            default:
                player.sendMessage("Usage: /resourcepack <load|unload|list|subscribe|unsubscribe> [name]");
                break;
        }

        return true;
    }

    private boolean loadResourcePack(Player player, String name) {
        if (!config.contains("resourcepacks." + name)) {
            return false;
        }

        String url = config.getString("resourcepacks." + name + ".url");
        String hash = config.getString("resourcepacks." + name + ".hash");

        if (url == null || hash == null) {
            return false;
        }

        try {
            player.setResourcePack(url, sha1HashStringToBytes(hash));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean unloadResourcePack(Player player, String name) {
        // Note: You cannot unload a specific resource pack from the player directly.
        // Instead, we clear all and reapply only the subscribed packs.
        try {
            if (isResourcePackActive(player, name)) {
                player.clearResourcePacks();
                applySubscribedResourcePacks(player); // Reapply only the subscribed packs
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean subscribeResourcePack(Player player, String name) {
        if (!config.contains("resourcepacks." + name)) {
            return false; // Resource pack does not exist
        }

        List<String> subscribedPacks = config.getStringList("subscribed." + player.getUniqueId().toString());
        if (!subscribedPacks.contains(name)) {
            subscribedPacks.add(name);
            config.set("subscribed." + player.getUniqueId().toString(), subscribedPacks);
            Main.getPlugin().getDefaultConfig().save();
            applySubscribedResourcePacks(player); // Apply the resource packs immediately
        }
        return true;
    }

    private boolean unsubscribeResourcePack(Player player, String name) {
        List<String> subscribedPacks = config.getStringList("subscribed." + player.getUniqueId().toString());

        // Check if the resource pack is actually subscribed
        if (!subscribedPacks.contains(name)) {
            return false; // Resource pack is not subscribed
        }

        // Attempt to unload the resource pack if it's currently active
        boolean isUnloaded = unloadResourcePack(player, name);

        // Only proceed if the resource pack was successfully unloaded
        if (isUnloaded) {
            // Remove from the subscribed list
            subscribedPacks.remove(name);
            config.set("subscribed." + player.getUniqueId().toString(), subscribedPacks);
            Main.getPlugin().getDefaultConfig().save();
        }

        return isUnloaded; // Return whether the resource pack was successfully unloaded and unsubscribed
    }

    private boolean isResourcePackActive(Player player, String name) {
        return config.getStringList("subscribed." + player.getUniqueId().toString()).contains(name);
    }

    private void handleListCommand(Player player) {
        Set<String> resourcePacks = config.getConfigurationSection("resourcepacks").getKeys(false);

        if (resourcePacks.isEmpty()) {
            player.sendMessage("No resource packs found.");
        } else {
            player.sendMessage("Available resource packs:");
            for (String packName : resourcePacks) {
                player.sendMessage(" - " + packName);
            }
        }
    }

    public static byte[] sha1HashStringToBytes(String sha1Hash) {
        if (sha1Hash.length() != 40) {
            throw new IllegalArgumentException("Invalid SHA-1 hash string length: " + sha1Hash);
        }

        byte[] bytes = new byte[20];
        for (int i = 0; i < 40; i += 2) {
            String hexPair = sha1Hash.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(hexPair, 16);
        }
        return bytes;
    }

    public void applySubscribedResourcePacks(Player player) {
        List<String> subscribedPacks = config.getStringList("subscribed." + player.getUniqueId().toString());

        for (String packName : subscribedPacks) {
            if (config.contains("resourcepacks." + packName)) {
                loadResourcePack(player, packName);
            }
        }
    }
}
