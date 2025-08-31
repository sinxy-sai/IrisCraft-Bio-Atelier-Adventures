package com.bnuz.mod;

import com.bnuz.mod.network.PaintTableUpdatePacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BnuzMod implements ModInitializer {
	public static final String MOD_ID = "bnuz-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModBlocks.initialize();
		ModItemGroups.initialize();
		ModItems.initialize();
		ModEntityTypes.initialize();
		ModEventHandlers.register();
		ModScreenHandlers.registerAll();
		PaintTableUpdatePacket.register();
		BiomeModifications.addFeature(
				BiomeSelectors.tag(ModTags.Biomes.HAS_IRIS), // 使用自定义标签
				GenerationStep.Feature.VEGETAL_DECORATION,
				ModPlacedFeatures.IRIS_PATCH_KEY
		);

		LOGGER.info("Hello Fabric world!");
	}


}