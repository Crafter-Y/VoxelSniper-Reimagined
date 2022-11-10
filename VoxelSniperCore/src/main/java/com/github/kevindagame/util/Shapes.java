package com.github.kevindagame.util;

import com.github.kevindagame.snipe.Undo;
import com.github.kevindagame.voxelsniper.block.BlockFace;
import com.github.kevindagame.voxelsniper.block.IBlock;
import com.github.kevindagame.voxelsniper.location.VoxelLocation;
import com.github.kevindagame.voxelsniper.vector.VoxelVector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Shapes {
    public static Set<VoxelLocation> ball(VoxelLocation location, double radius, boolean smooth) {
        Set<VoxelLocation> positions = new HashSet<>();
        double SMOOTH_SPHERE_VALUE = 0.5;
        int VOXEL_SPHERE_VALUE = 0;

        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();
        var radiusSquared = Math.pow(radius + (smooth ? SMOOTH_SPHERE_VALUE : VOXEL_SPHERE_VALUE), 2);
        for (int x = (int) (bx - radius); x <= bx + radius; x++) {
            for (int y = (int) (by - radius); y <= by + radius; y++) {
                for (int z = (int) (bz - radius); z <= bz + radius; z++) {

                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
                    if (distance <= radiusSquared) {
                        positions.add(new VoxelLocation(location.getWorld(), x, y, z));
                    }
                }
            }
        }

        return positions;
    }

    public static Set<VoxelLocation> voxel(VoxelLocation location, int brushSize) {
        Set<VoxelLocation> positions = new HashSet<>();
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                for (int y = brushSize; y >= -brushSize; y--) {
                    positions.add(new VoxelLocation(location.getWorld(), location.getX() + x, location.getY() + z, location.getZ() + y));
                }
            }
        }
        return positions;
    }

    public static Set<VoxelLocation> disc(VoxelLocation location, int brushSize, boolean smooth) {
        Set<VoxelLocation> positions = new HashSet<>();
        double SMOOTH_CIRCLE_VALUE = 0.5;
        double VOXEL_CIRCLE_VALUE = 0.0;
        final double radiusSquared = (brushSize + (smooth ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE)) * (brushSize + (smooth ? SMOOTH_CIRCLE_VALUE : VOXEL_CIRCLE_VALUE));
        final VoxelVector centerPoint = location.toVector();
        final VoxelVector currentPoint = centerPoint.clone();

        for (int x = -brushSize; x <= brushSize; x++) {
            currentPoint.setX(centerPoint.getX() + x);
            for (int z = -brushSize; z <= brushSize; z++) {
                currentPoint.setZ(centerPoint.getZ() + z);
                if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
                    positions.add(new VoxelLocation(location.getWorld(), currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ()));
                }
            }
        }
        return positions;
    }

    public static Set<VoxelLocation> voxelDisc(VoxelLocation location, int brushSize) {
        Set<VoxelLocation> positions = new HashSet<>();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int z = brushSize; z >= -brushSize; z--) {
                positions.add(new VoxelLocation(location.getWorld(), location.getX() + x, location.getY(), location.getZ() + z));
            }
        }
        return positions;
    }

    public static Set<VoxelLocation> cylinder(VoxelLocation location, int brushSize, int height, int shift, boolean smooth) {
        Set<VoxelLocation> positions = new HashSet<>();
        for (int y = 0; y < height; y++) {
            positions.addAll(disc(new VoxelLocation(location.getWorld(), location.getX(), location.getY() + y - (height / 2.0) + 1 + shift, location.getZ()), brushSize, smooth));
        }
        return positions;
    }

    public static Set<VoxelLocation> discFace(VoxelLocation location, int brushSize, boolean smooth, BlockFace face) {
        Set<VoxelLocation> disc = disc(location, brushSize, smooth);
        return face(location, disc, face);
    }

    public static Set<VoxelLocation> voxelDiscFace(VoxelLocation location, int brushSize, BlockFace face) {
        Set<VoxelLocation> voxelDisc = voxelDisc(location, brushSize);
        return face(location, voxelDisc, face);
    }

    public static Set<VoxelLocation> face(VoxelLocation center, Set<VoxelLocation> locations, BlockFace face) {
        if(face == BlockFace.UP || face == BlockFace.DOWN) return locations;

        Set<VoxelLocation> positions = new HashSet<>();
        for (var pos : locations) {
            VoxelVector diff = center.toVector().subtract(pos.toVector());
            switch (face) {
                case NORTH, SOUTH ->
                        positions.add(new VoxelLocation(center.getWorld(), center.getX() + diff.getBlockX(), center.getY() + diff.getBlockZ(), center.getZ()));
                case EAST, WEST ->
                        positions.add(new VoxelLocation(center.getWorld(), center.getX(), center.getY() + diff.getBlockX(), center.getZ() + diff.getBlockZ()));
            }
        }
        return positions;
    }

    public static Set<VoxelLocation> dome(VoxelLocation location, int brushSize, int height) {

        final Set<VoxelLocation> positions = new HashSet<>();
        final int absoluteHeight = Math.abs(height);
        final boolean negative = height < 0;

        final Undo undo = new Undo();

        final int brushSizeTimesVoxelHeight = brushSize * absoluteHeight;
        final double stepScale = ((brushSize * brushSize) + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 5.0;

        final double stepSize = 1 / stepScale;

        for (double u = 0; u <= Math.PI / 2; u += stepSize) {
            final double y = absoluteHeight * Math.sin(u);
            for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize) {
                final double x = brushSize * Math.cos(u) * Math.cos(stepV);
                final double z = brushSize * Math.cos(u) * Math.sin(stepV);

                final double targetBlockX = location.getX() + 0.5;
                final double targetBlockZ = location.getZ() + 0.5;
                final int targetY = ((int) Math.floor(location.getY() + (negative ? -y : y)));
                final int currentBlockXAdd = ((int) Math.floor(targetBlockX + x));
                final int currentBlockZAdd = ((int) Math.floor(targetBlockZ + z));
                final int currentBlockXSubtract = ((int) Math.floor(targetBlockX - x));
                final int currentBlockZSubtract = ((int) Math.floor(targetBlockZ - z));
                positions.add(new VoxelLocation(location.getWorld(), currentBlockXAdd, targetY, currentBlockZAdd));
                positions.add(new VoxelLocation(location.getWorld(), currentBlockXSubtract, targetY, currentBlockZAdd));
                positions.add(new VoxelLocation(location.getWorld(), currentBlockXAdd, targetY, currentBlockZSubtract));
                positions.add(new VoxelLocation(location.getWorld(), currentBlockXSubtract, targetY, currentBlockZSubtract));

            }
        }
        return positions;
    }

}
