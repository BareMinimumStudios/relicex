package com.github.clevernucleus.relicex.impl;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.item.ArmorItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum RelicType {
	HEAD(ArmorItem.Type.HELMET, tooltip -> {}),
	BODY(ArmorItem.Type.CHESTPLATE, tooltip -> {}),
	AMULET(null, tooltip -> {
		tooltip.remove(Text.translatable("trinkets.tooltip.slots.single", Text.translatable("trinkets.slot.chest.necklace").formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
		appendTooltip(tooltip, Text.translatable("trinkets.tooltip.attributes.all").formatted(Formatting.GRAY));
	}),
	RING(null, tooltip -> {
		tooltip.remove(Text.translatable("trinkets.tooltip.attributes.single", Text.translatable("trinkets.slot.offhand.ring").formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
		tooltip.remove(Text.translatable("trinkets.tooltip.attributes.single", Text.translatable("trinkets.slot.hand.ring").formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
		appendTooltip(tooltip, Text.translatable("trinkets.tooltip.slots.single", Text.translatable("trinkets.slot.offhand.ring").formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
		
		List<Text> distinct = tooltip.stream().distinct().toList();
		tooltip.clear();
		tooltip.addAll(distinct);
	});
	
	private final ArmorItem.Type armorItemType;
	private final Consumer<List<Text>> tooltip;
	
	RelicType(final ArmorItem.Type type, final Consumer<List<Text>> tooltip) {
		this.armorItemType = type;
		this.tooltip = tooltip;
	}
	
	private static void appendTooltip(final List<Text> tooltip, final Text text) {
		int index = Math.max(0, tooltip.indexOf(text));
		tooltip.set(index, Text.empty());
		tooltip.add(index + 1, Text.translatable("tooltip.relicex.worn").formatted(Formatting.GRAY));
	}
	
	public ArmorItem.Type getType() {
		return this.armorItemType;
	}
	
	public Consumer<List<Text>> tooltip() {
		return this.tooltip;
	}
}
