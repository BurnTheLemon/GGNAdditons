package de.burnthelemon.ggnadditons.commands;

import de.burnthelemon.ggnadditons.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

public class RessourcePackManagerCommand implements CommandExecutor {

    private final FileConfiguration config;
    private static final String RESOURCEPACK_META_KEY = "activeResourcePack";

    public RessourcePackManagerCommand() {
        this.config = Main.getPlugin().getDefaultConfig().getCfg();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("ggn.mrp")) {
            player.sendMessage("You do not have the permission to do this");
            return true;
        }

        // Check if the command is valid
        if (args.length < 1) {
            sender.sendMessage("Usage: /manageressourcepacks <add/remove/list>");
            return true;
        }

        String action = args[0];

        switch (action.toLowerCase()) {
            case "add":
                return handleAddCommand(player, sender, args);
            case "remove":
                return handleRemoveCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            default:
                sender.sendMessage("Unknown action: " + action);
                return true;
        }
    }

    private boolean handleAddCommand(Player player, CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /manageressourcepacks add <name> <url> [hash]");
            return true;
        }

        String name = args[1];
        String url = args[2];
        String hash;

        // Check if hash is provided; if not, generate it
        if (args.length == 4) {
            hash = args[3];
        } else {
            hash = urlToSha1Hash(url);
            if (hash == null) {
                sender.sendMessage("Failed to generate hash from the URL: " + url);
                return true;
            }
        }

        addResourcePack(name, url, hash);
        setActiveResourcePack(player, hash);
        sender.sendMessage("Resource pack added: " + name);
        return true;
    }

    private boolean handleRemoveCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /manageressourcepacks remove <name>");
            return true;
        }
        String name = args[1];

        if (removeResourcePack(name)) {
            sender.sendMessage("Resource pack removed: " + name);
        } else {
            sender.sendMessage("Resource pack '" + name + "' does not exist.");
        }
        return true;
    }

    private boolean handleListCommand(CommandSender sender) {
        Set<String> resourcePacks = config.getConfigurationSection("resourcepacks").getKeys(false);

        if (resourcePacks.isEmpty()) {
            sender.sendMessage("No resource packs found.");
        } else {
            sender.sendMessage("List of resource packs:");
            for (String packName : resourcePacks) {
                String url = config.getString("resourcepacks." + packName + ".url");
                String hash = config.getString("resourcepacks." + packName + ".hash");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<green>" + packName +
                                "</green><newline><yellow>URL:</yellow> <gray>=</gray> <gold>" +
                                url +
                                "</gold><newline>" +
                                "<yellow>Hash:</yellow> <gray>=</gray> <gold>" +
                                hash + "</gold>"
                ));
            }
        }
        return true;
    }

    private void addResourcePack(String name, String url, String hash) {
        config.set("resourcepacks." + name + ".url", url);
        config.set("resourcepacks." + name + ".hash", hash);
        Main.getPlugin().getDefaultConfig().save();
    }

    private boolean removeResourcePack(String name) {
        if (config.contains("resourcepacks." + name)) {
            config.set("resourcepacks." + name, null);
            Main.getPlugin().getDefaultConfig().save();
            return true;
        }
        return false;
    }

    public static String urlToSha1Hash(String url) {
        try {
            // Download the file from the URL
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Check if the response code is OK (200)
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            // Get the input stream of the file
            InputStream inputStream = connection.getInputStream();

            // Create a MessageDigest instance for SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            // Read the file data and update the digest
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            // Close the input stream
            inputStream.close();

            // Generate the SHA-1 hash
            byte[] hashBytes = digest.digest();

            // Convert the hash to a hexadecimal string
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(40);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void setActiveResourcePack(Player player, String hash) {
        player.setMetadata(RESOURCEPACK_META_KEY, new FixedMetadataValue(Main.getPlugin(), hash));
    }

    private boolean isResourcePackActive(Player player, String hash) {
        List<MetadataValue> metadata = player.getMetadata(RESOURCEPACK_META_KEY);
        for (MetadataValue value : metadata) {
            if (value.asString().equals(hash)) {
                return true;
            }
        }
        return false;
    }
}
