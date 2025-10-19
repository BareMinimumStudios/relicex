package com.github.clevernucleus.relicex.item;

import java.util.List;
import java.util.UUID;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.bibireden.playerex.components.PlayerEXComponents;
import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DragonStoneItem extends Item {
	public DragonStoneItem() {
		super((new FabricItemSettings()).maxCount(1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.add(this));
	}
	
	private static boolean safety(PlayerEntity user, ItemStack stack) {
		GameProfile profile = user.getGameProfile();
		
		if(profile == null) return false;
		UUID uuid = profile.getId();
		
		if(uuid == null) return false;
		NbtCompound tag = stack.getOrCreateNbt();
		
		if(tag.contains("Users", NbtElement.LIST_TYPE)) {
			NbtList users = tag.getList("Users", NbtElement.INT_ARRAY_TYPE);
			
			for(int i = 0; i < users.size(); i++) {
				UUID uuid2 = NbtHelper.toUuid(users.get(i));
				if(uuid.equals(uuid2)) return true;
			}
			
			users.add(NbtHelper.fromUuid(uuid));
		} else {
			NbtList users = new NbtList();
			users.add(NbtHelper.fromUuid(uuid));
			tag.put("Users", users);
		}
		
		return false;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.relicex.dragon_stone").formatted(Formatting.GRAY));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.getItemCooldownManager().set(this, 20);
		return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, user)
			.map((value) -> {
				if(!(value > 0.0D)) return super.use(world, user, hand);
				ItemStack itemStack = user.getStackInHand(hand);

				if(safety(user, itemStack)) {
					if(world.isClient) {
						user.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.NEUTRAL, 0.75F, 1.0F);
					} else {
						user.getComponent(PlayerEXComponents.PLAYER_DATA).reset(0);

						if(!user.isCreative()) itemStack.decrement(1);
					}

					return TypedActionResult.success(itemStack, world.isClient);
				}

				if(world.isClient) {
					user.sendMessage(Text.translatable("message.relicex.dragon_stone"), true);
				}

				return super.use(world, user, hand);
			})
			.orElse(super.use(world, user, hand));
	}
}
