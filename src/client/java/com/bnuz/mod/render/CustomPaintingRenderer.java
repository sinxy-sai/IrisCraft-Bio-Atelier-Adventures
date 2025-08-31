package com.bnuz.mod.render;

import com.bnuz.mod.entity.CustomPaintingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomPaintingRenderer extends EntityRenderer<CustomPaintingEntity> {
    // 使用一个默认纹理作为占位符
    private static final Identifier DEFAULT_TEXTURE = new Identifier("bnuz-mod", "textures/entity/custom_painting.png");

    public CustomPaintingRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    private final Map<String, Identifier> textureCache = new HashMap<>();

    @Override
    public Identifier getTexture(CustomPaintingEntity entity) {
        String imagePath = entity.getImagePath();
        System.out.println("尝试加载画作纹理，路径: " + imagePath);
        if (imagePath == null || imagePath.isEmpty()) {
            System.out.println("图片路径不存在: " + imagePath);
            return DEFAULT_TEXTURE;
        }

        // 检查纹理是否已缓存
        if (textureCache.containsKey(imagePath)) {
            System.out.println("使用缓存的纹理: " + imagePath);
            return textureCache.get(imagePath);
        }

        try {
            // 构建文件路径
            File imageFile = new File(imagePath);
            System.out.println("尝试加载文件: " + imageFile.getAbsolutePath());
            System.out.println("文件存在: " + imageFile.exists());
            if (!imageFile.exists()) {
                System.out.println("图片文件不存在: " + imagePath);
                return DEFAULT_TEXTURE;
            }

            // 读取图像
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            if (bufferedImage == null) {
                System.out.println("无法读取图片: " + imagePath);
                return DEFAULT_TEXTURE;
            }

            // 转换为NativeImage
            NativeImage nativeImage = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), false);
//            for (int y = 0; y < bufferedImage.getHeight(); y++) {
//                for (int x = 0; x < bufferedImage.getWidth(); x++) {
//                    int rgb = bufferedImage.getRGB(x, y);
//                    // 确保颜色格式正确
//                    nativeImage.setColor(x, y, (0xFF << 24) | (rgb & 0x00FFFFFF));
//                }
//            }
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    int argb = bufferedImage.getRGB(bufferedImage.getWidth()-1-x, bufferedImage.getHeight() - 1 - y); // 翻转Y坐标
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                    nativeImage.setColor(x, y, abgr);
                }
            }

            // 创建纹理
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);

            // 注册纹理
            Identifier textureId = new Identifier("bnuz-mod", "dynamic/" + UUID.randomUUID().toString());
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);

            // 缓存纹理
            textureCache.put(imagePath, textureId);
            System.out.println("成功加载纹理: " + imagePath);

            return textureId;
        } catch (IOException e) {
            System.out.println("加载纹理时出错: " + e.getMessage());
            e.printStackTrace();
            return DEFAULT_TEXTURE;
        }
    }

//    @Override
//    public Identifier getTexture(CustomPaintingEntity entity){
//        return DEFAULT_TEXTURE;
//    }

    @Override
    public void render(CustomPaintingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 根据画作的方向调整旋转
        Direction direction = entity.getHorizontalFacing();
        switch (direction) {
            case SOUTH:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                break;
            case WEST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                break;
            case EAST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
                break;
            case NORTH:
            default:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.0F));
                break;
        }

        matrices.translate(0.0, 0.5, 0.0); // 调整位置使其居中

        // 渲染画作平面
        renderPainting(entity,matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();

        // 调用父类渲染方法（渲染名称标签等）
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderPainting(CustomPaintingEntity entity,MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        // 调整大小和位置
        matrices.scale(1.0F, 1.0F, 1.0F);
        matrices.translate(-0.5, -1.0, 0.0);

        // 获取当前变换矩阵
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        // 获取顶点消费者
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                net.minecraft.client.render.RenderLayer.getEntitySolid(getTexture(entity))
        );

        // 定义画作的四个顶点
        // 左下角
        vertexConsumer.vertex(positionMatrix, 0.0F, 1.0F, -0.01F)
                .color(255, 255, 255, 255)
                .texture(0.0F, 1.0F)
                .overlay(overlay)
                .light(light)
                .normal(normalMatrix, 0.0F, 0.0F, -1.0F)
                .next();

        // 右下角
        vertexConsumer.vertex(positionMatrix, 1.0F, 1.0F, -0.01F)
                .color(255, 255, 255, 255)
                .texture(1.0F, 1.0F)
                .overlay(overlay)
                .light(light)
                .normal(normalMatrix, 0.0F, 0.0F, -1.0F)
                .next();

        // 右上角
        vertexConsumer.vertex(positionMatrix, 1.0F, 0.0F, -0.01F)
                .color(255, 255, 255, 255)
                .texture(1.0F, 0.0F)
                .overlay(overlay)
                .light(light)
                .normal(normalMatrix, 0.0F, 0.0F, -1.0F)
                .next();

        // 左上角
        vertexConsumer.vertex(positionMatrix, 0.0F, 0.0F, -0.01F)
                .color(255, 255, 255, 255)
                .texture(0.0F, 0.0F)
                .overlay(overlay)
                .light(light)
                .normal(normalMatrix, 0.0F, 0.0F, -1.0F)
                .next();

        matrices.pop();
    }
}

