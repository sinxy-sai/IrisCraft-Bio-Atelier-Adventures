package com.bnuz.mod;


import com.bnuz.mod.render.SyntheticBiologistRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import com.bnuz.mod.gui.PaintTable_Screen;
import com.bnuz.mod.render.CustomPaintingRenderer;
import com.bnuz.mod.render.ClothingDesignerRenderer;

@Environment(EnvType.CLIENT)
public class BnuzModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IRIS, RenderLayer.getCutout());

		ScreenRegistry.register(
				ModScreenHandlers.PAINT_TABLE_SCREEN_HANDLER,
				PaintTable_Screen::new
		);

		// 在 onInitializeClient 方法中添加
		EntityRendererRegistry.register(ModEntityTypes.CUSTOM_PAINTING, CustomPaintingRenderer::new);

		// 注册自定义实体渲染器
		EntityRendererRegistry.register(ModEntityTypes.CLOTHING_DESIGNER, ClothingDesignerRenderer::new);

		EntityRendererRegistry.register(ModEntityTypes.SYNTHETIC_BIOLOGIST, SyntheticBiologistRenderer::new);

	}
}