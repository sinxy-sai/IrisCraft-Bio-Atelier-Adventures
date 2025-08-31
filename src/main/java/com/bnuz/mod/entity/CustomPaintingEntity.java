package com.bnuz.mod.entity;

import com.bnuz.mod.ModEntityTypes;
import com.bnuz.mod.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CustomPaintingEntity extends ItemFrameEntity {
    private int[] pixelData;
    private String imagePath;

    private static final TrackedData<String> IMAGE_PATH =
            DataTracker.registerData(CustomPaintingEntity.class, TrackedDataHandlerRegistry.STRING);

    // 这个构造函数用于实体注册
    public CustomPaintingEntity(EntityType<? extends ItemFrameEntity> entityType, World world) {
        super(entityType, world);
        this.setInvulnerable(true);
    }

    // 这个构造函数用于从物品创建实体
    public CustomPaintingEntity(World world, BlockPos pos, Direction facing) {
        this(ModEntityTypes.CUSTOM_PAINTING, world);
        this.attachmentPos = pos;
        this.setFacing(facing);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IMAGE_PATH, "");
    }


    public void setPixelData(int[] pixelData) {
        this.pixelData = pixelData;
    }

    public int[] getPixelData() {
        return pixelData;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.dataTracker.set(IMAGE_PATH, imagePath == null ? "" : imagePath);
    }

//    public String getImagePath() {
//        return imagePath;
//    }

    public String getImagePath() {
        return this.dataTracker.get(IMAGE_PATH);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (pixelData != null) {
            nbt.putIntArray("PixelData", pixelData);
        }
        if (imagePath != null) {
            nbt.putString("ImagePath", imagePath);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("PixelData")) {
            pixelData = nbt.getIntArray("PixelData");
        }
        if (nbt.contains("ImagePath")) {
            imagePath = nbt.getString("ImagePath");
        }
    }

    @Override
    public ItemStack getHeldItemStack() {
        ItemStack stack = new ItemStack(ModItems.CUSTOM_PAINTING);

        NbtCompound nbt = new NbtCompound();
        if (this.pixelData != null) {
            nbt.putIntArray("pixels", this.pixelData);
        }
        if (this.imagePath != null) {
            nbt.putString("imagePath", this.imagePath);
        }

        stack.setNbt(nbt);
        return stack;
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        if (this.getWorld().isClient) return false;

        // 执行掉落逻辑
        this.onBreak(source.getAttacker());
        this.discard(); // 移除实体
        return true;
    }

    @Override
    public void onBreak(@Nullable Entity breaker) {
        if (this.getWorld().isClient) return;

        // 构造掉落物
        ItemStack drop = new ItemStack(ModItems.CUSTOM_PAINTING);

        NbtCompound nbt = new NbtCompound();
        if (this.pixelData != null) {
            nbt.putIntArray("pixels", this.pixelData);
        }
        if (this.imagePath != null) {
            nbt.putString("imagePath", this.imagePath);
        }
        drop.setNbt(nbt);

        // 生成掉落物实体
        ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), drop);
        this.getWorld().spawnEntity(itemEntity);
    }
}