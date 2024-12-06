package de.burnthelemon.ggnadditons.features.secretStuff;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SizeStick implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(event.getAction().isLeftClick()) return;

        if (isDebugStick(item, "<debug> Size")) {
            double scale = getScaleFromLore(item);
            if (scale > 0) {
                scaleEntity(player, scale);
            }
        } else if (isDebugStick(item, "<debug> Gravity")) {
            double gravity = getScaleFromLore(item);
            if (gravity > -5) {
                modifyEntityAttribute(player, Attribute.GRAVITY, gravity);
            }
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if (isDebugStick(item, "<debug> Size")) {
                double scale = getScaleFromLore(item);
                if (scale > 0) {
                    if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
                    scaleEntity(livingEntity, scale);
                    event.setCancelled(true);
                }
            } else if (isDebugStick(item, "<debug> Gravity")) {
                double gravity = getScaleFromLore(item);
                if (gravity > 0) {
                    if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
                    modifyEntityAttribute(livingEntity, Attribute.GRAVITY, gravity);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isDebugStick(ItemStack item, String debugName) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        if (item.getItemMeta().hasDisplayName()) {
            Component displayName = item.getItemMeta().displayName();
            if (displayName == null) return false;
            String plainTextName = PlainTextComponentSerializer.plainText().serialize(displayName);
            return plainTextName.equals(debugName);
        }
        return false;
    }

    private double getScaleFromLore(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return -1;
        }

        List<Component> loreComponents = item.getItemMeta().lore();
        if (loreComponents == null || loreComponents.isEmpty()) {
            return -1;
        }

        String loreText = PlainTextComponentSerializer.plainText().serialize(loreComponents.get(0));
        try {
            return Double.parseDouble(loreText);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void scaleEntity(LivingEntity entity, double scale) {
        modifyEntityAttribute(entity, Attribute.SCALE, scale);
    }

    private void modifyEntityAttribute(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(value);
        }
    }
}
