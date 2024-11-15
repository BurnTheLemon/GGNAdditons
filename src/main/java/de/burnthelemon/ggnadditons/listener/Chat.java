package de.burnthelemon.ggnadditons.listener;

import de.burnthelemon.ggnadditons.Main;
import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import de.burnthelemon.ggnadditons.util.DirectMessageComponent;
import de.burnthelemon.ggnadditons.util.DiscordManager;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat implements Listener {

    private final FileConfiguration config;
    private final LuckPerms api;
    private final DirectMessageComponent directMessageComponent = new DirectMessageComponent();
    private final DiscordManager discordManager = DiscordManager.getInstance();

    public Chat() {
        this.config = Main.getPlugin().getDefaultConfig().getCfg();
        this.api = LuckPermsProvider.get();


        // Scheduler to update status every 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                discordManager.updateDiscordStatus();
            }
        }.runTaskTimer(Main.getPlugin(), 0L, 20L * 60 * 10);
        Main.getPlugin().getLogger().log(Level.INFO, "=> Discord Scheduler Started for every 10 Minutes");


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
                        stringMessage, CustomFormattingTags.getCustomTags());



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
        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(DiscordManager.discordChannelId);

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








}
