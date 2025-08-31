package com.bnuz.mod;

import com.bnuz.mod.block.PaintTable;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModBlocks {
    public static Block register(Block block,String name,boolean shouldRegisterItem) {
        Identifier id = new Identifier(BnuzMod.MOD_ID, name);
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }


    //绘画合成台
    public static final Block PAINT_TABLE = register(
            new PaintTable(AbstractBlock.Settings.copy(Blocks.STONE)),
            "paint_table",
            true
    );

    // 鸢尾花
    public static final Block IRIS = register(
            new TallFlowerBlock(FabricBlockSettings.copyOf(Blocks.LILAC)),
            "iris",
            true
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.CUSTOM_ITEM_GROUP_KEY).register((itemGroup)->{
            itemGroup.add(ModBlocks.PAINT_TABLE.asItem());
            itemGroup.add(ModBlocks.IRIS.asItem());

        });
    }
}
