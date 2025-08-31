package com.bnuz.mod;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModTags {
    public static class Biomes {
        public static final TagKey<Biome> HAS_IRIS = TagKey.of(
                RegistryKeys.BIOME,
                new Identifier(BnuzMod.MOD_ID, "has_flower/iris")
        );
    }
}
