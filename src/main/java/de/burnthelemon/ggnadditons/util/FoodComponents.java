package de.burnthelemon.ggnadditons.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FoodComponents {

    public static FoodComponent basicFood = new FoodComponent() {
        @Override
        public int getNutrition() {
            return 0;
        }

        @Override
        public void setNutrition(int i) {

        }

        @Override
        public float getSaturation() {
            return 0;
        }

        @Override
        public void setSaturation(float v) {

        }

        @Override
        public boolean canAlwaysEat() {
            return false;
        }

        @Override
        public void setCanAlwaysEat(boolean b) {

        }

        @Override
        public float getEatSeconds() {
            return 0;
        }

        @Override
        public void setEatSeconds(float v) {

        }

        @Override
        public @Nullable ItemStack getUsingConvertsTo() {
            return null;
        }

        @Override
        public void setUsingConvertsTo(@Nullable ItemStack itemStack) {

        }

        @Override
        public @NotNull List<FoodEffect> getEffects() {
            return List.of();
        }

        @Override
        public void setEffects(@NotNull List<FoodEffect> list) {

        }

        @NotNull
        @Override
        public FoodEffect addEffect(@NotNull PotionEffect potionEffect, float v) {
            return null;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            return Map.of();
        }
    };
}
