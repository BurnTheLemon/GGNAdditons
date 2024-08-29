package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import de.burnthelemon.ggnadditons.util.DirectMessageComponent;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat implements Listener {

    private final FileConfiguration config;
    private final LuckPerms api;
    private final CustomFormattingTags customFormattingTags;
    private final String discordChannelId = "1242988275317211156";
    private final DirectMessageComponent directMessageComponent = new DirectMessageComponent();

    // New fields
    private Instant serverStartTime;
    private Instant lastUpdateTime;
    private final Set<UUID> uniquePlayers = new HashSet<>();

    // New flag to track if Discord is ready
    private boolean discordReady = false;

    public Chat() {
        this.config = Main.getPlugin().getDefaultConfig().getCfg();
        this.api = LuckPermsProvider.get();
        this.customFormattingTags = new CustomFormattingTags();
        this.serverStartTime = Instant.now();
        this.lastUpdateTime = Instant.now();
        DiscordSRV.api.subscribe(this);

        // Scheduler to update status every 30 seconds
        Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), this::updateDiscordStatus, 0L, (20L*60)*10);
        Main.getPlugin().getLogger().log(Level.INFO, "=> Discord Scheduler Started for every 10 Minutes");

        // Send a message to Discord on startup
        sendStartupMessage();
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener(Main.getPlugin()));
        Main.getPlugin().getLogger().info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        lastUpdateTime = Instant.now();

        // Set the flag to true when Discord is ready
        discordReady = true;

        // Send a message to Discord once JDA is ready
        sendStartupMessage();
    }

    private void updateDiscordStatus() {
        // Check if Discord is ready before updating the status
        if (!discordReady) {
            return;
        }

        //Main.getPlugin().getLogger().log(Level.INFO, "=> Discord Scheduler was Activated running pulse");

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            int playerCount = Bukkit.getOnlinePlayers().size();
            int uniquePlayerCount = uniquePlayers.size();
            Duration uptime = Duration.between(serverStartTime, Instant.now());
            String uptimeFormatted = String.format("%d minutes", uptime.toMinutes());

            // Convert lastUpdateTime to a formatted string
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

            // Update the channel's topic instead of sending a message
            channel.getManager().setTopic(status).queue(
                    success -> {
                        // Code to run if the update was successful
                        Main.getPlugin().getLogger().log(Level.INFO, "Channel topic updated!");
                    },
                    failure -> {
                        // Code to run if the update failed
                        Main.getPlugin().getLogger().log(Level.INFO, "Failed to update channel topic: " + failure.getMessage());
                    }
            );
            //Main.getPlugin().getLogger().log(Level.INFO, "=> Succesfully Queued: " + status);
        } else
            Main.getPlugin().getLogger().log(Level.INFO, "=> Cannot find Channel ID");
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        // Base Vars
        Player player = e.getPlayer();
        String teamPrefix = api.getGroupManager().getGroup(
                api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getDisplayName() == null ?
                api.getGroupManager().getGroup(api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getName() :
                api.getGroupManager().getGroup(api.getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup()).getDisplayName();
        String channelGroupIcon = "<hover:show_text:'Global Chat'>\ue005</hover>";

        // Assembling the Raw Message
        Component message = e.message();
        String username = null;

        // Raw message
        String stringMessage = LegacyComponentSerializer.legacyAmpersand().serialize(message);

        // Global message layout
        Component chatMessage = MiniMessage.miniMessage().deserialize(
                channelGroupIcon + "<hover:show_text:'" + player.getName() + "'> " +
                        MiniMessage.miniMessage().serialize(e.getPlayer().displayName()) + "</hover><reset> " +
                        MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(teamPrefix == null ? "" : teamPrefix)) +
                        "<reset>> " +
                        stringMessage, customFormattingTags.getCustomTags()
        );

        // Private Message Check
        if (stringMessage.startsWith("@")) {
            if (stringMessage.contains(" "))
                username = stringMessage.substring(1, stringMessage.indexOf(" "));
        }


        //check if the player has a nickname
        String nickedUser = getPlayerByDisplayName(username) == null ? PlainTextComponentSerializer.plainText().serialize(getPlayerByDisplayName(username).displayName()) : username ;
        if(nickedUser != null) {
            String dmString = stringMessage.substring(stringMessage.indexOf(" ") + 1);

            if(getPlayerByDisplayName(username).getName() == null) return;

            directMessageComponent.sendMessage(player, getPlayerByDisplayName(username).getName(), dmString);

            // Cancel the event to prevent it from being processed by other plugins like DiscordSRV
            e.setCancelled(true);
            return;
        }


        // Private Message
        if (username != null && Bukkit.getPlayer(username) != null) {
            Player receiver = Bukkit.getPlayer(username);
            String dmString = stringMessage.substring(stringMessage.indexOf(" ") + 1);

            directMessageComponent.sendMessage(player, receiver.getName(), dmString);

            // Cancel the event to prevent it from being processed by other plugins like DiscordSRV
            e.setCancelled(true);
            return;
        }

        // Global message
        // Regex to find occurrences of @ followed by a word (the player name)
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(MiniMessage.miniMessage().serialize(chatMessage));

        while (matcher.find()) {
            String playerName = matcher.group(1);
            Player targetPlayer = Bukkit.getPlayer(playerName);

            // Check if the player is online
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.playSound(targetPlayer, Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.PLAYERS, 1, 1);
            }
        }

        // Public chat message rendering
        e.renderer((cplayer, cdisplayName, coreMessage, cviewer) -> chatMessage);

        // Allow DiscordSRV to process public messages normally
        String discordStringMessage = stringMessage.replaceAll("<[^>]*>", "");
        discordStringMessage = discordStringMessage.replaceAll("@", "");
        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);

        if (discordStringMessage.isEmpty() || discordStringMessage.startsWith("\\"))
            discordStringMessage = "*Redacted*";

        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.CYAN);
            embed.setAuthor(player.getName() + " | " + teamPrefix.replaceAll("&[0-9a-fA-Fk-oK-OrR]", ""), null, "https://minotar.net/avatar/" + player.getName() + "/40.png");
            embed.setDescription(discordStringMessage);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    public static Player getPlayerByDisplayName(String displayName) {
        // Iterate over all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Check if the player's display name matches the given display name
            if (PlainTextComponentSerializer.plainText().serialize(player.displayName()).equalsIgnoreCase(PlainTextComponentSerializer.plainText().serialize(player.displayName())) ) {
                return player; // Player found
            }
        }
        return null; // Player not found
    }


    @EventHandler
    public void preJoinEvent(PlayerLoginEvent e) {
        Main.getPlugin().getLogger().log(Level.INFO,e.getPlayer().getName() + " Tried to join the Server but the Server was not ready yet.");

        if (!discordReady) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, MiniMessage.miniMessage().deserialize("<red><b>Failed to Connect\n</b><gray>The Server is not ready yet."));
            sendDiscordFullEmbed(e.getPlayer(), "Warning", e.getPlayer().getName() + " has tried to join but was denied\nThe Server was not ready yet. ", Color.RED);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        uniquePlayers.add(e.getPlayer().getUniqueId());
        updateDiscordStatus();

        sendDiscordFullEmbed(e.getPlayer(), "Player Joined", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().name()) + " has joined the Server", Color.GREEN);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        sendDiscordFullEmbed(e.getPlayer(), "Player Left", PlainTextComponentSerializer.plainText().serialize(e.getPlayer().name()) + " has left the Server", Color.RED);
        updateDiscordStatus();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String deathMessage = LegacyComponentSerializer.legacyAmpersand().serialize(event.deathMessage());

        sendDiscordEmbed(player, deathMessage, player.getName() + "died at: \n" +
                        "World: " + player.getLocation().getWorld().getName() + "\n " +
                        "X: " + Math.round(player.getLocation().getX()) + " " +
                        "Y: " + Math.round(player.getLocation().getY()) + " " +
                        "Z: " + Math.round(player.getLocation().getZ())
                , Color.BLACK);
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();

        // Get the key of the advancement
        NamespacedKey key = advancement.getKey();
        String namespace = key.getNamespace();
        String path = key.getKey();

        // Filter to ensure it's from the Minecraft namespace
        if (!namespace.equals("minecraft")) {
            return;
        }

        // Check if the advancement is related to crafting recipes
        if (isCraftingAdvancement(path)) {
            return;
        }

        // Send debugging messages
        //player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Advancement Key:</red> " + key.toString()));

        // Get the human-readable name of the advancement
        String advancementName = getAdvancementDisplayName(advancement);

        // Send the Discord embed
        sendDiscordEmbed(player, "Advancement Earned", player.getName() + " has made the advancement: " + advancementName, Color.ORANGE);
    }

    // Helper method to determine if the advancement is related to crafting recipes
    private boolean isCraftingAdvancement(String path) {
        // List of crafting-related advancements (you can expand this list based on your needs)
        return path.contains("craft") || path.contains("recipe");
    }

    private String getAdvancementDisplayName(Advancement advancement) {
        // Get the display name as a Component
        Component displayNameComponent = advancement.displayName();

        // Convert the Component to a plain text string

        return PlainTextComponentSerializer.plainText().serialize(displayNameComponent);
    }

    @Subscribe
    public void onDiscordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals(discordChannelId)) {
            String rawMessage = event.getMessage().getContentDisplay();
            String author = event.getAuthor().getName();
            String discordMessage = "<hover:show_text:'Send from discord by that user'><gradient:#7289da:#1e2124><#7289da>\uE007</#7289da></gradient></hover><reset><white> "+ author +"<reset>><gray> " + rawMessage;
            Bukkit.getServer().sendMessage(MiniMessage.miniMessage().deserialize(discordMessage));
        }
    }

    private void sendDiscordEmbed(Player player, String title, String description, Color color) {
        if (!discordReady) return;

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


    private void sendDiscordFullEmbed(Player player, String title, String description, Color color) {
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

    private void sendStartupMessage() {
        if (!discordReady) return;

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Server Status");
            embed.setDescription("The Server started");
            embed.setColor(Color.GREEN);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }


    public static void sendShutdownMessage() {
        if (!DiscordSRV.isReady) return; // Ensure DiscordSRV is available

        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById("1270533269166882986"); // Your channel ID
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Server Status");
            embed.setDescription("The Server shut down");
            embed.setColor(Color.RED);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();

        // Check if the plugin being disabled is DiscordSRV
        if (plugin.getName().equals("DiscordSRV")) {
            // Send the shutdown message to Discord
            Chat.sendShutdownMessage();
        }
    }
}
