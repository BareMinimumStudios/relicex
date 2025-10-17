package com.github.clevernucleus.relicex.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.RelicExRecipes;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class RarityManager implements SimpleResourceReloadListener<RarityManager.Weights> {
	public record Weights(Map<Identifier, RelicExRecipes.Weights> packedWeights) {}

	private static final Gson GSON = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();
	private static final int PATH_SUFFIX_LENGTH = ".json".length();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String DIRECTORY = "weights";
	private static final Identifier ID = new Identifier(RelicEx.MODID, DIRECTORY);

	private final Map<Identifier, RelicExRecipes.Weights> cachedWeightMap;

	@Override
	public CompletableFuture<Weights> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, RelicExRecipes.Weights> cache = new HashMap<>();
			int length = DIRECTORY.length() + 1;

			manager.findResources(DIRECTORY, id -> id.getPath().endsWith(".json")).forEach((resource, value) -> {
				String path = resource.getPath();
				Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));

				try {
					BufferedReader reader = value.getReader();
					GSON.<Map<String, RelicExRecipes.Weights>>fromJson(reader, new TypeToken<Map<String, RelicExRecipes.Weights>>() {}.getType())
						.forEach((k, v) -> {
							Identifier id = Identifier.tryParse(k);
							if (id == null || v == null) {
								LOGGER.warn("Failed to parse weight from asset file {} from {} :: [{}:{}]?", identifier, resource, id, v);
								return;
							};
							cache.putIfAbsent(id, v);
						});
				} catch(IOException | IllegalArgumentException exception) {
					LOGGER.error("Couldn't parse asset file {} from {}", identifier, resource, exception);
				}
			});

			return new Weights(cache);
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Weights data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> data.packedWeights.forEach(this.cachedWeightMap::putIfAbsent), executor);
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}
	
	public RarityManager() {
		this.cachedWeightMap = new HashMap<>();
	}
	
	public Collection<Identifier> keys() {
		return this.cachedWeightMap.keySet();	
	}
	
	public @NotNull WeightProperty weight(final Identifier identifier) {
		return new WeightProperty(this.cachedWeightMap.getOrDefault(identifier, null));
	}
}
