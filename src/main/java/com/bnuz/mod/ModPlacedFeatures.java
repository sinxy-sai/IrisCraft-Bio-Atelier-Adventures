package com.bnuz.mod;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> IRIS_PATCH_KEY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            new Identifier(BnuzMod.MOD_ID, "iris_patch")
    );
}
