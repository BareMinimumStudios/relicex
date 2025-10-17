package com.github.clevernucleus.relicex.item;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.api.PlayerEXAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.bibireden.playerex.api.event.PlayerEXSoundEvents;
import com.bibireden.playerex.components.PlayerEXComponents;
import com.bibireden.playerex.components.player.IPlayerDataComponent;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OrbOfRegretItem extends Item {
	private final boolean greater;
	
	public OrbOfRegretItem(final boolean greater) {
		super((new FabricItemSettings()).maxCount(1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.add(this));
		this.greater = greater;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.relicex." + (this.greater ? "greater" : "lesser") + "_orb_of_regret").formatted(Formatting.GRAY));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return this.greater ? Rarity.EPIC : Rarity.RARE;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, user)
			.map((value) -> {
				ItemStack stack = user.getStackInHand(hand);
				IPlayerDataComponent component = user.getComponent(PlayerEXComponents.PLAYER_DATA);

				AtomicInteger refundPoints = new AtomicInteger();

				PlayerEXAPI.getRefundConditions().forEach((condition) -> refundPoints.getAndAdd(condition.invoke(component, user).intValue()));
				if (value < 0.0 || refundPoints.get() < 0) return super.use(world, user, hand);

				if(world.isClient) {
					user.playSound(PlayerEXSoundEvents.REFUND_SOUND, SoundCategory.NEUTRAL, PlayerEX.CONFIG.getSoundSettings().getSkillUpVolume(), 1F);
				} else {
					component.addRefundablePoints(this.greater ? refundPoints.get() : 1);
					if(!user.isCreative()) stack.decrement(1);
				}

				return TypedActionResult.success(stack, world.isClient);
			})
			.orElse(super.use(world, user, hand));
	}
}
