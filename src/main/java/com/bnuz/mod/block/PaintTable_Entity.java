package com.bnuz.mod.block;

import com.bnuz.mod.ImplementedInventory;
import com.bnuz.mod.ModEntityTypes;

import com.bnuz.mod.ModItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PaintTable_Entity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private int paintTime; // 当前绘制进度
    private final int paintTimeTotal = 100; // 总绘制时间，100游戏刻，10s

    // 跟踪正在交互的玩家
    private final List<UUID> interactingPlayers = new ArrayList<>();

    // 属性委托同步数据到客户端
    public final PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> paintTime;
                case 1 -> paintTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) paintTime = value;
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public PaintTable_Entity(BlockPos pos, BlockState state) {
        super(ModEntityTypes.Paint_Table, pos, state);
    }

    /**
     * 添加正在交互的玩家
     * @param playerUUID 玩家UUID
     */
    public void addInteractingPlayer(UUID playerUUID) {
        if (!interactingPlayers.contains(playerUUID)) {
            interactingPlayers.add(playerUUID);
        }
    }

    /**
     * 移除不再交互的玩家
     * @param playerUUID 玩家UUID
     */
    public void removeInteractingPlayer(UUID playerUUID) {
        interactingPlayers.remove(playerUUID);
    }

    /**
     * 检查是否有玩家正在与工作台交互
     * @return 如果有玩家正在交互则返回true
     */
    public boolean hasInteractingPlayers() {
        return !interactingPlayers.isEmpty();
    }


    public static void tick(World world, BlockPos pos, BlockState state, PaintTable_Entity entity) {
        if (world.isClient) return;

        // 只有当有玩家正在交互且选择了图片时才进行绘制
        if (entity.hasInteractingPlayers() && entity.pixelData != null) {
            boolean canPaint = canPaint(entity);

            if (canPaint) {
                entity.paintTime++;
                if (entity.paintTime >= entity.paintTimeTotal) {
                    createPainting(entity);
                    entity.paintTime = 0;
                }
                markDirty(world, pos, state);
            } else {
                entity.paintTime = 0;
                markDirty(world, pos, state);
            }
        } else if (entity.paintTime > 0) {
            // 如果没有玩家交互或未选择图片但进度条不为0，则重置进度
            entity.paintTime = 0;
            markDirty(world, pos, state);
        }
    }

    private static boolean canPaint(PaintTable_Entity entity) {
        ItemStack silkSlot = entity.getStack(SILK_SLOT);
        ItemStack eColiSlot = entity.getStack(ECOLI_SLOT);
        ItemStack hydrogelSlot = entity.getStack(HYDROGEL_SLOT);
        ItemStack outputSlot = entity.getStack(OUTPUT_SLOT);

        // 检查输入材料是否齐全
        boolean hasMaterials = silkSlot.isOf(ModItems.SILK) && !silkSlot.isEmpty() &&
                eColiSlot.isOf(ModItems.E_COLI) && !eColiSlot.isEmpty() &&
                hydrogelSlot.isOf(ModItems.HYDROGEL) && !hydrogelSlot.isEmpty();

        // 检查输出槽是否有空间
        boolean hasOutputSpace = outputSlot.isEmpty() ||
                (outputSlot.isOf(ModItems.CUSTOM_PAINTING) &&
                        outputSlot.getCount() < outputSlot.getMaxCount());
        return hasMaterials && hasOutputSpace && entity.pixelData != null;
    }

    private int[] pixelData; // 存储接收到的像素数据

    // 添加设置像素数据的方法
    public void setPixelData(int[] pixelData) {
        System.out.println("服务器接收到像素数据，长度：" + pixelData.length);
        this.pixelData = pixelData;
        markDirty();
    }


    private static void createPainting(PaintTable_Entity entity) {
        System.out.println("尝试生成画作...");
        // 消耗输入材料
        entity.getStack(SILK_SLOT).decrement(1);
        entity.getStack(ECOLI_SLOT).decrement(1);
        entity.getStack(HYDROGEL_SLOT).decrement(1);

        // 使用存储的像素数据创建画作
        if (entity.pixelData != null && entity.pixelData.length == 4096) {
            // 生成唯一文件名
            String fileName = UUID.randomUUID() + ".png";

            // 使用游戏运行目录下的子文件夹
            File outputDir = new File("./bnuz-mod/dynamic_painting/");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);

            try {
                // 创建16x16的BufferedImage
                BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < 64; y++) {
                    for (int x = 0; x < 64; x++) {
                        image.setRGB(x, y, entity.pixelData[y * 64 + x]);
                    }
                }

                // 保存图片
                ImageIO.write(image, "PNG", outputFile);
                System.out.println("图片已保存到: " + outputFile.getAbsolutePath());

                // 创建画作物品
                ItemStack painting = new ItemStack(ModItems.CUSTOM_PAINTING);
                NbtCompound nbt = painting.getOrCreateNbt();
                nbt.putIntArray("pixels", entity.pixelData);
                nbt.putString("imagePath", outputFile.getAbsolutePath()); // 使用资源标识符格式
                System.out.println("画作物品 NBT 已设置: " + nbt.toString());

                entity.setStack(OUTPUT_SLOT, painting);
            } catch (IOException e) {
                System.err.println("保存图片时出错: " + e.getMessage());
                e.printStackTrace();
            }

            // 清空像素数据
            entity.pixelData = null;
        } else {
            System.err.println("无效的像素数据");
        }
    }

    // 实现 Inventory 接口方法
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt("PaintTime", paintTime);
        if (pixelData != null) {
            nbt.putIntArray("PixelData", pixelData);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        paintTime = nbt.getInt("PaintTime");
        if (nbt.contains("PixelData")) {
            pixelData = nbt.getIntArray("PixelData");
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.paint_table");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // 玩家打开界面时添加到交互列表
        addInteractingPlayer(player.getUuid());
        return new PaintTable_ScreenHandler(syncId, playerInventory, this, player);
    }

    @Override
    public void markRemoved() {
        // 方块实体被移除时清空交互玩家列表
        interactingPlayers.clear();
        super.markRemoved();
    }

    public static final int SILK_SLOT = 0;      // 丝绸输入槽
    public static final int ECOLI_SLOT = 1;     // 大肠杆菌输入槽
    public static final int HYDROGEL_SLOT = 2;  // 水凝胶输入槽
    public static final int OUTPUT_SLOT = 3;    // 输出槽
}
