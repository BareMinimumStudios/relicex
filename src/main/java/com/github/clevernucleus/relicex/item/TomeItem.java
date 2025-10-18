package com.github.clevernucleus.relicex.item;

import java.util.List;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.PlayerEX;
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

public class TomeItem extends Item {
	public TomeItem() {
		super((new FabricItemSettings()));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.add(this));
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.relicex.tome").formatted(Formatting.GRAY));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.UNCOMMON;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, user)
			.map((value) -> {
				ItemStack stack = user.getStackInHand(hand);

				if (value >= PlayerEXAttributes.LEVEL.getMaxValue() - 1.0D) return super.use(world, user, hand);

				IPlayerDataComponent component = user.getComponent(PlayerEXComponents.PLAYER_DATA);
				if(world.isClient) {
					user.playSound(PlayerEXSoundEvents.SPEND_SOUND, SoundCategory.NEUTRAL, PlayerEX.CONFIG.getSoundSettings().getSkillUpVolume(), 1F);
				} else {
					component.levelUp(1, true);

					if(!user.isCreative()) stack.decrement(1);
				}

				return TypedActionResult.success(stack, world.isClient);
			})
			.orElse(super.use(world, user, hand));
	}
}
