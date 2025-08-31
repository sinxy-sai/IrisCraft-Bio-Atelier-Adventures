package com.bnuz.mod;

import com.bnuz.mod.item.CustomPaintingItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
   public static Item register(Item item, RegistryKey<Item> registryKey){
       // Register the item.
       Item registeredItem = Registry.register(Registries.ITEM, registryKey.getValue(), item);
       // Return the registered item!
       return registeredItem;
   }

   // hydrogel 水凝胶
   public static final RegistryKey<Item> HYDROGEL_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"hydrogel"));
   public static final Item HYDROGEL = register(
           new Item(new Item.Settings()),
           HYDROGEL_KEY
   );

   // silk 丝绸
   public static final RegistryKey<Item> SILK_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"silk"));
   public static final Item SILK = register(
            new Item(new Item.Settings()),
            SILK_KEY
   );

   // e_coli 大肠杆菌
   public static final RegistryKey<Item> E_COLI_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"e_coli"));
   public static final Item E_COLI = register(
           new Item(new Item.Settings()),
           E_COLI_KEY
   );

   //custom_painting 画作
    public static final RegistryKey<Item> CUSTOM_PAINTING_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"custom_painting"));
    public static final Item CUSTOM_PAINTING = register(
            new CustomPaintingItem(new Item.Settings()),
            CUSTOM_PAINTING_KEY
    );


    //clothing_designer_spawn_egg 服装设计师生成蛋
    public static final RegistryKey<Item> CLOTHING_DESIGNER_SPAWN_EGG_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"clothing_designer_spawn_egg"));
    public static final Item CLOTHING_DESIGNER_SPAWN_EGG = register(
            new SpawnEggItem(ModEntityTypes.CLOTHING_DESIGNER, 0xFFFFFF, 0x000000, new FabricItemSettings()),
            CLOTHING_DESIGNER_SPAWN_EGG_KEY
    );

    //synthetic_biologist_spawn_egg 合成生物学家生成蛋
    public static final RegistryKey<Item> SYNTHETIC_BIOLOGIST_SPAWN_EGG_KEY = RegistryKey.of(RegistryKeys.ITEM,new Identifier(BnuzMod.MOD_ID,"synthetic_biologist_spawn_egg"));
    public static final Item SYNTHETIC_BIOLOGIST_SPAWN_EGG = register(
            new SpawnEggItem(ModEntityTypes.SYNTHETIC_BIOLOGIST, 0xCFCFCF, 0xa33434, new FabricItemSettings()),
            SYNTHETIC_BIOLOGIST_SPAWN_EGG_KEY
    );


   public static void initialize(){
       ItemGroupEvents.modifyEntriesEvent(ModItemGroups.CUSTOM_ITEM_GROUP_KEY).register((itemGroup)->{
           itemGroup.add(HYDROGEL);
           itemGroup.add(SILK);
           itemGroup.add(E_COLI);
//           itemGroup.add(IRIS);
           itemGroup.add(CLOTHING_DESIGNER_SPAWN_EGG);
           itemGroup.add(SYNTHETIC_BIOLOGIST_SPAWN_EGG);
       });
   }
}
