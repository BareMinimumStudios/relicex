package com.github.clevernucleus.relicex.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.github.clevernucleus.relicex.RelicEx;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class RarityManager implements SimpleResourceReloadListener<RarityManager.Weights> {
	public record Weights(Map<Identifier, String> packedWeights) {}

	private static final Gson GSON = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();
	private static final int PATH_SUFFIX_LENGTH = ".json".length();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String DIRECTORY = "weights";
	private static final Identifier ID = new Identifier(RelicEx.MODID, DIRECTORY);

	private final Map<Identifier, WeightProperty> cachedWeightMap;

	@Override
	public CompletableFuture<Weights> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, String> cache = new HashMap<>();
			int length = DIRECTORY.length() + 1;

			manager.findResources(DIRECTORY, id -> id.getPath().endsWith(".json")).forEach((resource, value) -> {
				String path = resource.getPath();
				Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));

				try {
					BufferedReader reader = value.getReader();
					GSON.<Map<String, String>>fromJson(reader, new TypeToken<Map<String, String>>() {}.getType())
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
		return CompletableFuture.runAsync(() -> {
			data.packedWeights.forEach((id, packedWeight) -> {
				if (packedWeight.isEmpty()) return;

				String[] strings = packedWeight.split(":");

				if(strings.length != 8) return;
				WeightProperty property = new WeightProperty(strings);
				this.cachedWeightMap.putIfAbsent(id, property);
			});
		}, executor);
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
	
	public @Nullable WeightProperty weight(final Identifier identifier) {
		return this.cachedWeightMap.getOrDefault(identifier, null);
	}
}
