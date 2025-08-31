package com.bnuz.mod;

import com.bnuz.mod.block.PaintTable_Entity;
import com.bnuz.mod.entity.ClothingDesignerEntity;
import com.bnuz.mod.entity.CustomPaintingEntity;
import com.bnuz.mod.entity.SyntheticBiologistEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.entity.BlockEntityType;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

import static com.bnuz.mod.BnuzMod.MOD_ID;


public class ModEntityTypes {
    public static <T extends BlockEntityType<?>> T register(String path,T blockEntityType){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(MOD_ID,path),blockEntityType);
    }

    // 注册自定义实体
    public static final BlockEntityType<PaintTable_Entity> Paint_Table = register(
            "paint_table",
            // 对于 1.21.2 及以上的版本，
            // 请将 `BlockEntityType.Builder` 替换为 `FabricBlockEntityTypeBuilder`。
            BlockEntityType.Builder.create(PaintTable_Entity::new, ModBlocks.PAINT_TABLE).build()
    );


    // 添加以下代码
    public static final EntityType<CustomPaintingEntity> CUSTOM_PAINTING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "custom_painting"),
            EntityType.Builder.<CustomPaintingEntity>create(CustomPaintingEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f)
                    .maxTrackingRange(10)
                    .trackingTickInterval(Integer.MAX_VALUE)
                    .build(MOD_ID + ":custom_painting")
    );


    public static final EntityType<ClothingDesignerEntity> CLOTHING_DESIGNER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "clothing_designer"),
            EntityType.Builder.create(ClothingDesignerEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.95f).spawnableFarFromPlayer()
                    .build(MOD_ID + ":clothing_designer")
    );

    public static final EntityType<SyntheticBiologistEntity> SYNTHETIC_BIOLOGIST = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "synthetic_biologist"),
            EntityType.Builder.create(SyntheticBiologistEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.95f).spawnableFarFromPlayer()
                    .build(MOD_ID + ":synthetic_biologist")
    );


    public static void initialize() {
        FabricDefaultAttributeRegistry.register(
                CLOTHING_DESIGNER,
                MobEntity.createMobAttributes()
                        .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
        );
        FabricDefaultAttributeRegistry.register(
                SYNTHETIC_BIOLOGIST,
                MobEntity.createMobAttributes()
                        .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS,
                        BiomeKeys.SAVANNA,
                        BiomeKeys.SNOWY_PLAINS
                ),
                SpawnGroup.CREATURE,
                CLOTHING_DESIGNER,
                25, 3, 4
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS,
                        BiomeKeys.SAVANNA,
                        BiomeKeys.SNOWY_PLAINS
                ),
                SpawnGroup.CREATURE,
                SYNTHETIC_BIOLOGIST,
                25, 3, 4
        );

    }

}
