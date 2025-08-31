package com.bnuz.mod.gui;

import com.bnuz.mod.BnuzMod;
import com.bnuz.mod.block.PaintTable;
import com.bnuz.mod.block.PaintTable_ScreenHandler;
import com.bnuz.mod.network.PaintTableUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;


public class PaintTable_Screen extends HandledScreen<PaintTable_ScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(BnuzMod.MOD_ID, "textures/gui/paint_table.png");

    // 上传按钮的位置和大小
    private static final int UPLOAD_BUTTON_X = 76;
    private static final int UPLOAD_BUTTON_Y = 69;
    private static final int UPLOAD_BUTTON_WIDTH = 24;
    private static final int UPLOAD_BUTTON_HEIGHT = 12;

    // 箭头动画的位置
    private static final int ARROW_X = 75;
    private static final int ARROW_Y = 42;

    public PaintTable_Screen(PaintTable_ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // 居中标题
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        // 添加上传图片按钮
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("container.button.paint_table.upload"), button -> {
            // 打开文件选择器
            openFileChooser();
        }).dimensions(this.x + UPLOAD_BUTTON_X, this.y + UPLOAD_BUTTON_Y, UPLOAD_BUTTON_WIDTH, UPLOAD_BUTTON_HEIGHT).build());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制主背景
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

        // 绘制绘制进度（箭头）
        int progress = handler.getPaintProgress();
        if (progress > 0) {
            context.drawTexture(TEXTURE, this.x + ARROW_X, this.y + ARROW_Y, 176, 0, progress, 1);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    /**
     * 打开文件选择器选择图片
     */
    private void openFileChooser() {
        // 在新线程中打开文件选择器以避免阻塞Minecraft主线程
        new Thread(() -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                // 准备过滤器模式
                String[] filterPatternsArray = {"*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"};
                PointerBuffer filterPatterns = stack.mallocPointer(filterPatternsArray.length);

                for (String pattern : filterPatternsArray) {
                    ByteBuffer patternBuffer = stack.UTF8(pattern);
                    filterPatterns.put(patternBuffer);
                }
                filterPatterns.flip();

                // 准备其他参数
                ByteBuffer title = stack.UTF8("选择图片");
                ByteBuffer defaultPath = stack.UTF8("");
                ByteBuffer filterDesc = stack.UTF8("图片文件");

                // 调用 TinyFileDialogs
                String path = TinyFileDialogs.tinyfd_openFileDialog(
                        title,
                        defaultPath,
                        filterPatterns,
                        filterDesc,
                        false
                );

                if (path != null && !path.isEmpty()) {
                    try {
                        // 读取图片数据
                        java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(path));
                        if (img != null) {
                            // 缩放图片到16x16
                            java.awt.Image scaled = img.getScaledInstance(64, 64, java.awt.Image.SCALE_AREA_AVERAGING);
                            java.awt.image.BufferedImage buf =
                                    new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                            buf.getGraphics().drawImage(scaled, 0, 0, null);

                            // 提取像素数据
                            int[] pixels = new int[4096];
                            for (int y = 0; y < 64; y++) {
                                for (int x = 0; x < 64; x++) {
                                    pixels[y * 64 + x] = buf.getRGB(x, y);
                                }
                            }

                            // 在主线程中发送像素数据到服务器
                            if (this.client != null) {
                                this.client.execute(() -> {
                                    // 这里需要创建一个网络包来发送像素数据到服务器
                                    // 假设我们有一个方法 sendPixelDataToServer(int[] pixels)
                                    sendPixelDataToServer(pixels);
                                    if (this.client.player != null) {
                                        this.client.player.sendMessage(Text.literal("已选择并发送图片数据"), false);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("读取图片时出错: " + e.getMessage());
                        e.printStackTrace();

                        // 向玩家显示错误消息
                        if (this.client != null && this.client.player != null) {
                            this.client.player.sendMessage(Text.literal("无法读取图片: " + e.getMessage()), false);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("打开文件选择器时出错: " + e.getMessage());
                e.printStackTrace();

                // 向玩家显示错误消息
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("无法打开文件选择器: " + e.getMessage()), false);
                }
            }
        }).start();
    }

    private void sendPixelDataToServer(int[] pixels) {
        // 获取方块实体的位置
        BlockPos pos = handler.getBlockEntity().getPos();

        // 创建并发送网络包
        PaintTableUpdatePacket packet = new PaintTableUpdatePacket(pos, pixels);

        // 创建 PacketByteBuf
        PacketByteBuf buf = PacketByteBufs.create();
        PaintTableUpdatePacket.encode(packet, buf);
        // 发送数据包
        ClientPlayNetworking.send(PaintTableUpdatePacket.ID, buf);
    }
}
