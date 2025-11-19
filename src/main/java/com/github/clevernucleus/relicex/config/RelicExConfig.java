package com.github.clevernucleus.relicex.config;

import java.util.ArrayList;
import java.util.HashMap;
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
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public boolean enableDimensionSpecificRarities = true;
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings overworldSettings = new DimensionRaritySettings("minecraft:overworld", 100, 100, 80, 50, 30, 15, 5, 1); // Common to rare relics more likely
	
	@ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings netherSettings = new DimensionRaritySettings("minecraft:the_nether", 100, 50, 70, 90, 80, 60, 30, 10); // Mid-tier relics more common
	
    @ConfigEntry.Gui.CollapsibleObject
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public DimensionRaritySettings endSettings = new DimensionRaritySettings("minecraft:the_end", 100, 10, 30, 60, 90, 100, 80, 50); // Rare to legendary relics more likely
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Category("dimension_rarity")
	public List<DimensionRaritySettings> customDimensionSettings = new ArrayList<>();
	
	public static class DimensionRaritySettings {
		@ConfigEntry.Gui.Tooltip
		public String dimensionId;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 200)
		@ConfigEntry.Gui.Tooltip
		public int relicChanceMultiplier;
		
		// Individual rarity percentages (0-100%)
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int commonRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int uncommonRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int rareRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int epicRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int mythicalRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int legendaryRelicChance;
		
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		@ConfigEntry.Gui.Tooltip
		public int immortalRelicChance;
		
		public DimensionRaritySettings() {
			this("", 100, 100, 100, 100, 100, 100, 100, 100);
		}
		
		public DimensionRaritySettings(String dimension, int relicMultiplier, int common, int uncommon, int rare, int epic, int mythical, int legendary, int immortal) {
			this.dimensionId = dimension;
			this.relicChanceMultiplier = relicMultiplier;
			this.commonRelicChance = common;
			this.uncommonRelicChance = uncommon;
			this.rareRelicChance = rare;
			this.epicRelicChance = epic;
			this.mythicalRelicChance = mythical;
			this.legendaryRelicChance = legendary;
			this.immortalRelicChance = immortal;
		}
	}
}
