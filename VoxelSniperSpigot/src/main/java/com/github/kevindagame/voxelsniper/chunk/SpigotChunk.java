package com.github.kevindagame.voxelsniper.chunk;

import com.github.kevindagame.voxelsniper.SpigotVoxelSniper;
import com.github.kevindagame.voxelsniper.entity.SpigotEntity;
import com.github.kevindagame.voxelsniper.entity.IEntity;
import com.github.kevindagame.voxelsniper.material.SpigotMaterial;
import com.github.kevindagame.voxelsniper.material.VoxelMaterial;
import com.github.kevindagame.voxelsniper.world.IWorld;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;

import java.util.Arrays;

public class SpigotChunk implements IChunk {
    private final Chunk chunk;

    public SpigotChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int getX() {
        return chunk.getX();
    }

    @Override
    public int getZ() {
        return chunk.getZ();
    }

    @Override
    public IWorld getWorld() {
        return SpigotVoxelSniper.getInstance().getWorld(chunk.getWorld());
    }

    @Override
    public Iterable<? extends IEntity> getEntities() {
        return Arrays.stream(chunk.getEntities()).map(SpigotEntity::fromSpigotEntity).toList();
    }

    @Override
    public void setBlockInNativeChunk(int x, int y, int z, VoxelMaterial material, boolean applyPhysics) {
        var nmsChunk = ((CraftChunk) chunk ).getHandle();
        BlockPosition bp = new BlockPosition(x, y, z);
        Material mat = ((SpigotMaterial) material.getIMaterial()).material();
        IBlockData ibd = CraftMagicNumbers.getBlock(mat).m();
        nmsChunk.a(bp, ibd, applyPhysics);
    }
}
