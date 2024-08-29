package de.burnthelemon.ggnadditons.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class DirectMessageComponent {
    /**
     * Sends a direct message from one player to another.
     *
     * @param sender The player sending the message.
     * @param receiverName The name of the player receiving the message.
     * @param message The message to send.
     */
    public void sendMessage(Player sender, String receiverName, String message) {
        Player receiver = Bukkit.getPlayerExact(receiverName);
        if (receiver != null) {
            // Customize the format of the message as needed
            String formattedMessage = String.format("<gray>[<yellow>%s <gray>-></gray> %s</yellow>]</gray><reset> %s", sender.getName(), receiver.getName(), message);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage,CustomFormattingTags.getCustomTags()));
            receiver.sendMessage(MiniMessage.miniMessage().deserialize(formattedMessage,CustomFormattingTags.getCustomTags()));
            receiver.playSound(receiver, Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.PLAYERS,1,1);
        } else {
            sender.sendMessage("Player not found.");
        }
    }
}
