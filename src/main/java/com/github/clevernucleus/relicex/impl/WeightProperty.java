package com.github.clevernucleus.relicex.impl;

import java.util.EnumMap;
import java.util.Map;

import com.bibireden.data_attributes.api.util.RandDistribution;
import com.github.clevernucleus.relicex.RelicExRecipes;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public final class WeightProperty {
	private final Map<EntityAttributeModifier.Operation, Float[]> values = new EnumMap<>(EntityAttributeModifier.Operation.class);
	private float rarity;
	
	public WeightProperty(final RelicExRecipes.Weights weightsIn) {
		Float[] addition = new Float[4];
		Float[] multiplyTotal = new Float[4];

		this.rarity = weightsIn.relativeWeighting() * 0.01F;

		addition[0] = weightsIn.additionChance() * 0.01F;
		multiplyTotal[0] = 1.0F - addition[0];

		addition[1] = weightsIn.additionMin();
		addition[2] = weightsIn.additionMax();
		addition[3] = weightsIn.additionIncrement();

		multiplyTotal[1] = weightsIn.multiplierMin();
		multiplyTotal[2] = weightsIn.multiplierMax();
		multiplyTotal[3] = weightsIn.multiplierIncrement();

		this.values.put(EntityAttributeModifier.Operation.ADDITION, addition);
		this.values.put(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, multiplyTotal);
	}

	public float rarity() {
		return this.rarity;
	}
	
	public float processValue(final Processor processor) {
		RandDistribution<EntityAttributeModifier.Operation> distributor = new RandDistribution<>(EntityAttributeModifier.Operation.ADDITION);
		this.values.forEach((key, value) -> distributor.add(key, value[0]));
		EntityAttributeModifier.Operation operation = distributor.getDistributedRandom();
		Float[] values = this.values.get(operation);
		return processor.consume(operation, values[0], values[1], values[2], values[3]);
	}
	
	@FunctionalInterface
	public interface Processor {
		float consume(final EntityAttributeModifier.Operation operation, final float weight, final double minRoll, final double maxRoll, final double increment);
	}
}
