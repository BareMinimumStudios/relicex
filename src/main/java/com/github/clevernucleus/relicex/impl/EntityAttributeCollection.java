package com.github.clevernucleus.relicex.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.bibireden.data_attributes.api.util.RandDistribution;
import com.github.clevernucleus.relicex.RelicEx;
import com.google.common.collect.Multimap;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public final class EntityAttributeCollection {
	public static final String KEY_ATTRIBUTES = "Attributes";
	public static final String KEY_NAME = "Name";
	public static final String KEY_VALUE = "Value";
	public static final String KEY_OPERATION = "Operation";
	public static final String KEY_RARENESS = "Rareness";
	private final Map<Identifier, Consumer<BiConsumer<Double, Operation>>> values;
	
	public EntityAttributeCollection() {
		this.values = new HashMap<>();
	}
	
	private static float randomAttribute(EntityAttributeCollection collection) {
		RandDistribution<Identifier> distributor = new RandDistribution<>(null);
		RandDistribution<Pair<Double, Float>> values = new RandDistribution<>(new Pair<>(0.0D, 1.0F));
		
		for(Identifier identifier : RelicEx.RARITY_MANAGER.keys()) {
			WeightProperty weight = RelicEx.RARITY_MANAGER.weight(identifier);
			distributor.add(identifier, weight.rarity());
		}
		
		Identifier identifier = distributor.getDistributedRandom();
		if(identifier == null) return 1.0F;
		
		WeightProperty weight = RelicEx.RARITY_MANAGER.weight(identifier);
		if(weight == null) return 1.0F;
		
		return weight.processValue((op, mult, min, max, incr) -> {
			for(double i = min; i < max; i += incr) {
				float w = 1.0F - (float)(i / max);
				values.add(new Pair<Double, Float>(i, w), w);
			}
			
			Pair<Double, Float> pair = values.getDistributedRandom();
			double value = pair.getLeft();
			float multiplier = pair.getRight();
			
			if(!collection.values.containsKey(identifier)) {
				collection.values.put(identifier, consumer -> consumer.accept(value, op));
				return (0.5F * multiplier) + (0.2F * mult);
			}
			
			return 1.0F;
		}) + (0.3F * weight.rarity());
	}
	
	public static void readFromNbt(NbtCompound tag, String slot, Multimap<EntityAttribute, EntityAttributeModifier> modifiersNBT, Multimap<EntityAttribute, EntityAttributeModifier> modifiersITM) {
		if(!tag.contains(KEY_ATTRIBUTES, NbtElement.LIST_TYPE)) return;
		NbtList list = tag.getList(KEY_ATTRIBUTES, NbtElement.COMPOUND_TYPE);
		
		for(int i = 0; i < list.size(); i++) {
			NbtCompound entry = list.getCompound(i);
			Identifier identifier = new Identifier(entry.getString(KEY_NAME));

			EntityAttribute attribute = Registries.ATTRIBUTE.get(identifier);
			if (attribute == null) continue;

			Operation operation = Operation.fromId(entry.getByte(KEY_OPERATION));
			EntityAttributeModifier modifier = new EntityAttributeModifier(SlotKey.from(slot).uuid(), "RelicEx Modifier", entry.getDouble(KEY_VALUE), operation);
			modifiersNBT.put(attribute, modifier);
		}
		
		for(EntityAttribute attributeITM : modifiersITM.keySet()) {
			var collectionITM = modifiersITM.get(attributeITM);
			
			if(modifiersNBT.containsKey(attributeITM)) {
				List<EntityAttributeModifier> temp = new ArrayList<>();
				var collectionNBT = modifiersNBT.get(attributeITM);
				
				for(EntityAttributeModifier modifierITM : collectionITM) {
					Operation operationITM = modifierITM.getOperation();
					
					for(EntityAttributeModifier modifierNBT : collectionNBT) {
						Operation operationNBT = modifierNBT.getOperation();
						
						if(operationITM == operationNBT) {
							EntityAttributeModifier modifier3 = new EntityAttributeModifier(modifierITM.getId(), "RelicEx Modifier", Math.max(modifierITM.getValue(), modifierNBT.getValue()), operationITM);
							temp.add(modifier3);
						} else {
							temp.add(modifierITM);
							temp.add(modifierNBT);
						}
					}
				}
				
				modifiersNBT.removeAll(attributeITM);
				modifiersNBT.putAll(attributeITM, temp);
			} else {
				modifiersNBT.putAll(attributeITM, collectionITM);
			}
		}
	}
	
	public static float getValueIfArmor(NbtCompound tag, EntityAttribute attributeIn, float fallback) {
		if(!tag.contains(KEY_ATTRIBUTES, NbtElement.LIST_TYPE)) return fallback;
		NbtList list = tag.getList(KEY_ATTRIBUTES, NbtElement.COMPOUND_TYPE);
		
		for(int i = 0; i < list.size(); i++) {
			NbtCompound entry = list.getCompound(i);
			Identifier identifier = new Identifier(entry.getString(KEY_NAME));
			EntityAttribute attribute = Registries.ATTRIBUTE.get(identifier);
			
			if(attribute == null || !attribute.equals(attributeIn) || (int)entry.getByte(KEY_OPERATION) != Operation.ADDITION.getId()) continue;
			return (float)entry.getDouble(KEY_VALUE);
		}
		
		return fallback;
	}
	
	public void writeToNbt(NbtCompound tag) {
		if(tag.contains(KEY_ATTRIBUTES, NbtElement.LIST_TYPE)) return;
		float weight = randomAttribute(this);
		float random = (float)Math.random();
		
		for(int i = 0; i < 4; i++) {
			if(random > weight) continue;
			weight *= randomAttribute(this);
		}
		
		NbtList list = new NbtList();
		
		this.values.forEach((key, consumer) -> {
			NbtCompound entry = new NbtCompound();
			entry.putString(KEY_NAME, key.toString());
			consumer.accept((value, operation) -> {
				entry.putDouble(KEY_VALUE, value);
				entry.putByte(KEY_OPERATION, (byte)operation.getId());
			});
			list.add(entry);
		});
		
		Rareness rareness = Rareness.fromWeight(weight);
		tag.putString(KEY_RARENESS, rareness.toString());
		tag.put(KEY_ATTRIBUTES, list);
	}
}
