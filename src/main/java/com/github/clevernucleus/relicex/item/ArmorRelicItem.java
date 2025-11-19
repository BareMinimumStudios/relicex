package com.github.clevernucleus.relicex.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.bibireden.data_attributes.api.item.ItemHelper;
import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.impl.EntityAttributeCollection;
import com.github.clevernucleus.relicex.impl.Rareness;
import com.github.clevernucleus.relicex.impl.RelicType;
import com.github.clevernucleus.relicex.models.armor.RelicArmorModel;
import com.github.clevernucleus.relicex.renderers.RelicArmorRenderer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import mod.azure.azurelibarmor.animatable.GeoItem;
import mod.azure.azurelibarmor.animatable.client.RenderProvider;
import mod.azure.azurelibarmor.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelibarmor.util.AzureLibUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ArmorRelicItem extends ArmorItem implements ItemHelper, GeoItem {
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public ArmorRelicItem(RelicType type) {
		super(ArmorMaterials.CHAIN, type.getType(), (new FabricItemSettings()).maxCount(1));
	}
	
	/**
	 * Registers all rareness variants of this armor item to the COMBAT item group
	 * Items are reconstructed every time the creative menu is opened
	 */
	public static void registerAllVariants(Item armorItem, RelicType armorType) {
		if (armorType.getType() == null) return;
		
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
			for (Rareness rareness : Rareness.values()) {
				ItemStack stack = new ItemStack(armorItem);
				
				NbtCompound tag = stack.getOrCreateNbt();
				tag.putString(EntityAttributeCollection.KEY_RARENESS, rareness.key());
				
				EntityAttributeCollection collection = new EntityAttributeCollection();
				collection.writeToNbt(tag);
				
				content.add(stack);
			}
		});
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound tag = stack.getNbt();
		
		if(tag == null || !tag.contains(EntityAttributeCollection.KEY_RARENESS, NbtElement.STRING_TYPE)) return;
		Rareness rareness = Rareness.fromKey(tag.getString(EntityAttributeCollection.KEY_RARENESS));
		tooltip.add(rareness.formatted());
	}
	
	@Override
	public void onStackCreated(ItemStack itemStack, int count) {
		NbtCompound tag = itemStack.getOrCreateNbt();
		EntityAttributeCollection collection = new EntityAttributeCollection();
		collection.writeToNbt(tag);
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		NbtCompound tag = stack.getOrCreateNbt();
		Multimap<EntityAttribute, EntityAttributeModifier> modifiers = ArrayListMultimap.create();
		EntityAttributeCollection.readFromNbt(tag, this.getSlotType().getName(), modifiers, ArrayListMultimap.create());
		
		return slot == this.getSlotType() ? modifiers : super.getAttributeModifiers(stack, slot);
	}
	
	@Override
	public int getEnchantability() {
		return ArmorMaterials.GOLD.getEnchantability();
	}
	
	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(RelicEx.RELIC_SHARD);
	}
	
	@Override
	public Integer getProtection(ItemStack itemStack) {
		return (int)EntityAttributeCollection.getValueIfArmor(itemStack.getOrCreateNbt(), EntityAttributes.GENERIC_ARMOR, 0.0F);
	}
	
	@Override
	public Float getToughness(ItemStack itemStack) {
		return EntityAttributeCollection.getValueIfArmor(itemStack.getOrCreateNbt(), EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.0F);
	}
	
	@Override
	public SoundEvent getEquipSound(ItemStack itemStack) {
		NbtCompound tag = itemStack.getNbt();
		
		if(tag != null && tag.contains(EntityAttributeCollection.KEY_RARENESS, NbtElement.STRING_TYPE)) {
			Rareness rareness = Rareness.fromKey(tag.getString(EntityAttributeCollection.KEY_RARENESS));
			return rareness.equipSound();
		}
		
		return Rareness.COMMON.equipSound();
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private RelicArmorRenderer renderer;
			private Rareness lastRareness;

			@SuppressWarnings("unchecked")
			@Override
			public @NotNull BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
				var tag = itemStack.getNbt();
				Rareness currentRareness;
				
				if (tag != null && tag.contains(EntityAttributeCollection.KEY_RARENESS, NbtElement.STRING_TYPE)) {
					currentRareness = Rareness.fromKey(tag.getString(EntityAttributeCollection.KEY_RARENESS));
				} else {
					currentRareness = Rareness.COMMON;
				}
				
				if (renderer == null || lastRareness != currentRareness) {
					renderer = new RelicArmorRenderer(new RelicArmorModel(currentRareness));
					lastRareness = currentRareness;
				}
				
				renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return (BipedEntityModel<LivingEntity>) this.renderer;
			}
		});
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return renderProvider;
	}

	@Override
	public void registerControllers(mod.azure.azurelibarmor.core.animation.AnimatableManager.ControllerRegistrar controllerRegistrar) {}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
