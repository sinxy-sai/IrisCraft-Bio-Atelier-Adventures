//package com.bnuz.mod;
//
//import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
//import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
//import net.minecraft.data.server.recipe.RecipeExporter;
//import net.minecraft.item.Items;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.util.Identifier;
//
//public class ModRecipeGenerator extends FabricRecipeProvider {
//    public ModRecipeGenerator(FabricDataOutput output) {
//        super(output);
//    }
//
//    @Override
//    public void generate(RecipeExporter exporter) {
//        // Paint Table 合成配方
//        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.PAINT_TABLE)
//                .pattern("III")
//                .pattern("IFI")
//                .pattern("III")
//                .input('I', Items.IRON_INGOT)
//                .input('F', ModBlocks.IRIS)
//                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
//                .criterion(hasItem(ModBlocks.IRIS), conditionsFromItem(ModBlocks.IRIS))
//                .offerTo(exporter, new Identifier(BnuzMod.MOD_ID, "paint_table"));
//    }
//}
