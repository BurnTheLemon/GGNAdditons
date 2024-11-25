package de.burnthelemon.ggnadditons.hooks.discordBridge;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.features.chatSystem.ChatExecutor;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordManager {

    public static boolean isDiscordReady = false;
    public static String discordChannelId = "1306869962622763069";

    private Instant serverStartTime;
    private Instant lastUpdateTime;
    public final Set<UUID> uniquePlayers = new HashSet<>();

    // Singleton instance
    private static DiscordManager instance;

    // Private constructor to prevent instantiation from outside
    private DiscordManager() {
        DiscordSRV.api.subscribe(this);
        serverStartTime = Instant.now();
        lastUpdateTime = Instant.now();
        Main.getPlugin().getLogger().info("Loading Discord Manager");
    }

    // Public method to get the singleton instance
    public static DiscordManager getInstance() {
        if (instance == null) {
            instance = new DiscordManager();
        }
        return instance;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener(Main.getPlugin()));
        Main.getPlugin().getLogger().info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        lastUpdateTime = Instant.now();

        // Set the flag to true when Discord is ready
        isDiscordReady = true;

        // Send a message to Discord once JDA is ready
        sendStartupMessage();
    }

    public void updateDiscordStatus() {
        // Check if Discord is ready before updating the status
        if (!isDiscordReady) {
            return;
        }

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            int playerCount = Bukkit.getOnlinePlayers().size();
            int uniquePlayerCount = uniquePlayers.size();
            Duration uptime = Duration.between(serverStartTime, Instant.now());
            String uptimeFormatted = String.format("%d minutes", uptime.toMinutes());

            ZonedDateTime zonedLastUpdateTime = lastUpdateTime.atZone(ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d. MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            String lastUpdateFormatted = zonedLastUpdateTime.format(formatter);

            String status = String.format(
                    "%d/%d players online | %d unique players ever joined | Server online for %s | Last update: %s",
                    playerCount,
                    Bukkit.getMaxPlayers(),
                    uniquePlayerCount,
                    uptimeFormatted,
                    lastUpdateFormatted
            );

            channel.getManager().setTopic(status).queue(
                    success -> {
                        Main.getPlugin().getLogger().log(Level.INFO, "Channel topic updated!");
                    },
                    failure -> {
                        Main.getPlugin().getLogger().log(Level.INFO, "Failed to update channel topic: " + failure.getMessage());
                    }
            );
        } else {
            Main.getPlugin().getLogger().log(Level.INFO, "=> Cannot find Channel ID");
        }
    }

    public void sendDiscordEmbed(Player player, String title, String description, Color color) {
        if (!isDiscordReady) return;

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(title);
            embed.setDescription(description);
            embed.setColor(color);
            embed.setAuthor(player.getName(), null, "https://minotar.net/avatar/" + player.getName() + "/40.png");
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Subscribe
    public void onDiscordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals(discordChannelId)) {
            String rawMessage = event.getMessage().getContentDisplay();
            String author = event.getAuthor().getName();
            String discordMessage = "<hover:show_text:'Sent from discord by that user'><gradient:#7289da:#1e2124><#7289da>\uE007</#7289da></gradient></hover><reset><white> " + author + "<reset>><gray> " + rawMessage;
            Bukkit.getServer().sendMessage(MiniMessage.miniMessage().deserialize(discordMessage));
        }
    }

    public void sendDiscordMessage(Player player,String discordStringMessage) {
        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.CYAN);
            embed.setAuthor(player.getName() + " | " + ChatExecutor.getPlayerTeamPrefix(player).replaceAll("&[0-9a-fA-Fk-oK-OrR]", ""), null, "https://minotar.net/avatar/" + player.getName() + "/40.png");
            embed.setDescription(discordStringMessage);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void sendDiscordFullEmbed(Player player, String title, String description, Color color) {
        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(title);
            embed.setDescription(description);
            embed.setColor(color);
            embed.setThumbnail("https://minotar.net/avatar/" + player.getName() + "/50.png");

            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void sendStartupMessage() {
        if (!isDiscordReady) return;

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Server Status");
            embed.setDescription("The Server started");
            embed.setColor(Color.GREEN);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void sendShutdownMessage() {
        if (!DiscordSRV.isReady) return;

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById("1270533269166882986");
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Server Status");
            embed.setDescription("The Server shut down");
            embed.setColor(Color.RED);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
