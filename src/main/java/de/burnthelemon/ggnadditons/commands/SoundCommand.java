package de.burnthelemon.ggnadditons.commands;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SoundCommand implements CommandExecutor {

    // Define a map of sound names to Sound enums
    private final Map<String, net.kyori.adventure.sound.Sound> sounds = new HashMap<>();

    // Constructor to initialize the sound map
    public SoundCommand() {
        // Register predefined sounds

        sounds.put("vineboom", net.kyori.adventure.sound.Sound.sound(Key.key("minecraft", "vineboom"), net.kyori.adventure.sound.Sound.Source.PLAYER, 0.25f, 1f)); // Example sound
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if the command is "sound" and if there is an argument
        if (command.getName().equalsIgnoreCase("sound") && args.length == 1) {
            if (commandSender instanceof Player player) {
                String soundName = args[0].toLowerCase();
                net.kyori.adventure.sound.Sound sound = sounds.get(soundName);

                if (sound != null) {
                    playSoundForNearbyPlayers(player.getLocation(), sound);
                } else {
                    player.sendMessage("Sound not found. Available sounds: " + String.join(", ", sounds.keySet()));
                }
                return true;
            } else {
                commandSender.sendMessage("Only players can use this command.");
                return true;
            }
        }
        return false; // Command not recognized
    }

    private void playSoundForNearbyPlayers(Location location, Sound sound) {
        // Play sound for all players within 30 blocks
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(location) <= 30) {
                player.playSound(sound, Sound.Emitter.self());
            }
        }
    }
}
