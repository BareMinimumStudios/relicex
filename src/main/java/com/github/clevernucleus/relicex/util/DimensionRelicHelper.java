package com.github.clevernucleus.relicex.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.config.RelicExConfig;
import com.github.clevernucleus.relicex.impl.WeightProperty;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class DimensionRelicHelper {
    
    public static List<Item> getFilteredRelicsForDimension(String dimensionId) {
        RelicExConfig config = RelicEx.config();
        
        if (!config.enableDimensionSpecificRarities) {
            return RelicEx.RELICS;
        }
        
        RelicExConfig.DimensionRaritySettings settings = getDimensionSettings(dimensionId, config);
        if (settings == null) {
            return RelicEx.RELICS;
        }
        
        List<Item> filteredRelics = new ArrayList<>();
        Random random = new Random();
        
        for (Item relic : RelicEx.RELICS) {
            Identifier relicId = Registries.ITEM.getId(relic);
            WeightProperty weight = RelicEx.RARITY_MANAGER.weight(relicId);
            
            if (weight != null) {
                float relicRarity = weight.rarity();
                int chanceForRarity = getChanceForRarity(relicRarity, settings);
                
                // Roll against the percentage chance for this rarity
                if (random.nextInt(100) < chanceForRarity) {
                    filteredRelics.add(relic);
                }
            } else {
                // If no weight is defined, treat as common (0% rarity)
                if (random.nextInt(100) < settings.commonRelicChance) {
                    filteredRelics.add(relic);
                }
            }
        }
        
        return filteredRelics.isEmpty() ? RelicEx.RELICS : filteredRelics;
    }
    
    private static int getChanceForRarity(float relicRarity, RelicExConfig.DimensionRaritySettings settings) {
        // Map rarity values to the appropriate chance setting
        // These ranges match the typical rarity system in your mod
        if (relicRarity <= 0.05f) { // 0-5% = Common
            return settings.commonRelicChance;
        } else if (relicRarity <= 0.15f) { // 6-15% = Uncommon
            return settings.uncommonRelicChance;
        } else if (relicRarity <= 0.30f) { // 16-30% = Rare
            return settings.rareRelicChance;
        } else if (relicRarity <= 0.50f) { // 31-50% = Epic
            return settings.epicRelicChance;
        } else if (relicRarity <= 0.70f) { // 51-70% = Mythical
            return settings.mythicalRelicChance;
        } else if (relicRarity <= 0.90f) { // 71-90% = Legendary
            return settings.legendaryRelicChance;
        } else { // 91-100% = Immortal
            return settings.immortalRelicChance;
        }
    }
    
    public static float getRelicChanceMultiplierForDimension(String dimensionId) {
        RelicExConfig config = RelicEx.config();
        
        if (!config.enableDimensionSpecificRarities) {
            return 1.0f;
        }
        
        RelicExConfig.DimensionRaritySettings settings = getDimensionSettings(dimensionId, config);
        if (settings == null) {
            return 1.0f;
        }
        
        return settings.relicChanceMultiplier;
    }
    
    private static RelicExConfig.DimensionRaritySettings getDimensionSettings(String dimensionId, RelicExConfig config) {
        // Check built-in dimensions first
        if ("minecraft:overworld".equals(dimensionId)) {
            return config.overworldSettings;
        } else if ("minecraft:the_nether".equals(dimensionId)) {
            return config.netherSettings;
        } else if ("minecraft:the_end".equals(dimensionId)) {
            return config.endSettings;
        }
        
        // Check custom dimension settings
        for (RelicExConfig.DimensionRaritySettings customSettings : config.customDimensionSettings) {
            if (dimensionId.equals(customSettings.dimensionId)) {
                return customSettings;
            }
        }
        
        return null;
    }
    
    public static String getDimensionIdFromLootTableId(Identifier lootTableId) {
        String path = lootTableId.toString();
        
        // Extract dimension from loot table path
        // Most chest loot tables follow the pattern: namespace:chests/structure_name
        // We'll need to infer dimension from the structure or use a mapping
        
        if (path.contains("nether") || path.contains("bastion") || path.contains("fortress")) {
            return "minecraft:the_nether";
        } else if (path.contains("end") || path.contains("city")) {
            return "minecraft:the_end";
        } else {
            return "minecraft:overworld";
        }
    }
}