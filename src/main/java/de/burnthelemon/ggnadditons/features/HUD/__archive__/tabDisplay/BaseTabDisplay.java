package de.burnthelemon.ggnadditons.features.HUD.__archive__.tabDisplay;

import de.burnthelemon.ggnadditons.features.HUD.TabMessageOfTheDayInputList;
import de.burnthelemon.ggnadditons.util.CustomFormattingTags;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Random;

public class BaseTabDisplay implements TabDisplayDecorator {

    @Override
    public void setCustomTab(Player player) {
        Random random = new Random();
        String color1 = generateRandomHexColor();
        String color2 = generateRandomHexColor();
        int randomMOTDIndex = random.nextInt(TabMessageOfTheDayInputList.customMOTD.size());

        // Set header
        player.sendPlayerListHeader(MiniMessage.miniMessage().deserialize(
                "<newline><newline><newline><newline><newline><newline>" +
                        "\uE004", CustomFormattingTags.getCustomTags()));

        // Set footer
        player.sendPlayerListFooter(MiniMessage.miniMessage().deserialize(
                "<gradient:" + color1 + ":" + color2 + ">" +
                        TabMessageOfTheDayInputList.customMOTD.get(randomMOTDIndex)
                                .replaceAll("%p", "<white>" + PlainTextComponentSerializer.plainText().serialize(player.displayName()) + "</white>")
                                .replaceAll("%st_deaths", "<white> " + player.getStatistic(Statistic.DEATHS) + " </white>")
                                .replaceAll("%st_pkills", "<white> " + player.getStatistic(Statistic.PLAYER_KILLS) + " </white>")
                                .replaceAll("%st_mkills", "<white> " + player.getStatistic(Statistic.MOB_KILLS) + " </white>")
                                .replaceAll("%st_timesincedeath", "<white> " + player.getStatistic(Statistic.TIME_SINCE_DEATH) + " </white>") +
                        "</gradient>", CustomFormattingTags.getCustomTags()));
    }

    private String generateRandomHexColor() {
        Random random = new Random();
        return String.format("#%02x%02x%02x", random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
