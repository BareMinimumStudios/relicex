package com.github.clevernucleus.relicex;

import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Identifier;

public class RelicExRecipes {
    public static final String RECIPE_SYNC_ID = "recipe_sync";
    public static final OwoNetChannel RECIPE_SYNC_CHANNEL = OwoNetChannel.create(Identifier.of(RelicEx.MODID, RECIPE_SYNC_ID));

    public record Weights(float relativeWeighting, float additionChance, float additionMin, float additionMax, float additionIncrement, float multiplierMin, float multiplierMax, float multiplierIncrement) {}
}
