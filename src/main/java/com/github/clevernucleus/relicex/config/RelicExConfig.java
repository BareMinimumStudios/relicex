package com.github.clevernucleus.relicex.config;

import java.util.ArrayList;
import java.util.List;

import com.github.clevernucleus.relicex.RelicEx;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = RelicEx.MODID)
public final class RelicExConfig implements ConfigData {
	
	@ConfigEntry.Gui.Tooltip
	public boolean chestsHaveLoot = true;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int chestsHaveRelicChance = 15;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int chestsHaveLesserOrbChance = 5;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int chestsHaveGreaterOrbChance = 1;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int chestsHaveTomeChance = 5;
	
	@ConfigEntry.Gui.Tooltip
	public boolean dropsOnlyFromPlayerKills = false;
	
	@ConfigEntry.Gui.Tooltip
	public boolean dragonDropsStone = true;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobsDropLootChance = 5;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobDropIsRelicChance = 50;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobDropIsPotionChance = 30;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobDropIsLesserOrbChance = 5;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobDropIsGreaterOrbChance = 1;
	
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip
	public int mobDropIsTomeChance = 10;
	
	@ConfigEntry.Gui.Tooltip
	public List<String> mobDropBlacklist = new ArrayList<String>();
	
	// Dimension-specific rarity settings
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public boolean enableDimensionSpecificRarities = true;
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings overworldSettings = new DimensionRaritySettings(0.0f, 50.0f, "minecraft:overworld");
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings netherSettings = new DimensionRaritySettings(25.0f, 75.0f, "minecraft:the_nether");
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings endSettings = new DimensionRaritySettings(50.0f, 100.0f, "minecraft:the_end");
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public List<DimensionRaritySettings> customDimensionSettings = new ArrayList<>();
	
	public static class DimensionRaritySettings {
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public float minRarityPercentage;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public float maxRarityPercentage;
		
		@ConfigEntry.Gui.Tooltip
		public String dimensionId;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int relicChanceMultiplier = 100;
		
		public DimensionRaritySettings() {
			this(0.0f, 100.0f, "");
		}
		
		public DimensionRaritySettings(float minRarity, float maxRarity, String dimension) {
			this.minRarityPercentage = minRarity;
			this.maxRarityPercentage = maxRarity;
			this.dimensionId = dimension;
		}
	}
}
