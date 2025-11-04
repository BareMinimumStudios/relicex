package com.github.clevernucleus.relicex.util;

import java.util.ArrayList;
import java.util.List;

import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.config.RelicExConfig;
import com.github.clevernucleus.relicex.impl.RarityManager;
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
        float minRarity = settings.minRarityPercentage / 100.0f;
        float maxRarity = settings.maxRarityPercentage / 100.0f;
        
        for (Item relic : RelicEx.RELICS) {
            Identifier relicId = Registries.ITEM.getId(relic);
            WeightProperty weight = RelicEx.RARITY_MANAGER.weight(relicId);
            
            if (weight != null) {
                float relicRarity = weight.rarity();
                if (relicRarity >= minRarity && relicRarity <= maxRarity) {
                    filteredRelics.add(relic);
                }
            } else {
                // If no weight is defined, treat as common (0% rarity)
                if (minRarity <= 0.0f) {
                    filteredRelics.add(relic);
                }
            }
        }
        
        return filteredRelics.isEmpty() ? RelicEx.RELICS : filteredRelics;
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
        
        return settings.relicChanceMultiplier / 100.0f;
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
        
        // then custom
        for (RelicExConfig.DimensionRaritySettings customSettings : config.customDimensionSettings) {
            if (dimensionId.equals(customSettings.dimensionId)) {
                return customSettings;
            }
        }
        
        return null;
    }
    
    public static String getDimensionIdFromLootTableId(Identifier lootTableId) {
        String path = lootTableId.toString();

        RarityManager.LOGGER.info("got: " + path);
        
        if (path.contains("minecraft:")) {
            if (path.contains("nether") || path.contains("bastion") || path.contains("fortress")) {
                return "minecraft:the_nether";
            } else if (path.contains("end_city_treasure")) {
                return "minecraft:the_end";
            } else {
                return "minecraft:overworld";
            }
        } else {
            return null;
        }
    }
}