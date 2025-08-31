package com.bnuz.mod.render;

import com.bnuz.mod.BnuzMod;
import com.bnuz.mod.entity.ClothingDesignerEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class ClothingDesignerRenderer extends BipedEntityRenderer<ClothingDesignerEntity, PlayerEntityModel<ClothingDesignerEntity>> {
    public ClothingDesignerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,
                new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM), false),  // ← 关键
                0.5f);

        this.model.jacket.visible      = true;
        this.model.leftSleeve.visible  = true;
        this.model.rightSleeve.visible = true;
        this.model.leftPants.visible   = true;
        this.model.rightPants.visible  = true;
        this.model.hat.visible         = true;
    }

    @Override
    public Identifier getTexture(ClothingDesignerEntity entity) {
        return new Identifier(BnuzMod.MOD_ID, "textures/entity/clothing_designer.png");
    }
}
