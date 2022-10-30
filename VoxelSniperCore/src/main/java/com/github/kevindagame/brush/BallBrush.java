package com.github.kevindagame.brush;

import com.github.kevindagame.brush.perform.PerformerBrush;
import com.github.kevindagame.snipe.SnipeData;
import com.github.kevindagame.util.Messages;
import com.github.kevindagame.util.VoxelMessage;
import com.github.kevindagame.voxelsniper.block.IBlock;
import com.github.kevindagame.voxelsniper.location.VoxelLocation;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * A brush that creates a solid ball. http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ball_Brush
 *
 * @author Piotr
 */
public class BallBrush extends PerformerBrush {


    private boolean smoothSphere = false;

    public BallBrush() {
        this.setName("Ball");
    }

    private void ball(final SnipeData v, IBlock targetBlock) {
        var time = System.currentTimeMillis();
        final int brushSize = v.getBrushSize();
        var positions = this.sphere(targetBlock.getLocation(), brushSize, this.smoothSphere);
        positions.forEach(position -> this.currentPerformer.perform(position.getBlock()));
        v.owner().storeUndo(this.currentPerformer.getUndo());
        var minX = targetBlock.getX() - brushSize;
        var minZ = targetBlock.getZ() - brushSize;
        var maxX = targetBlock.getX() + brushSize;
        var maxZ = targetBlock.getZ() + brushSize;
        var minChunk = new VoxelLocation(this.getWorld(), minX, 0, minZ).getChunk();
        var maxChunk = new VoxelLocation(this.getWorld(), maxX, 255, maxZ).getChunk();
        for (int x = minChunk.getX(); x <= maxChunk.getX(); x++) {
            for (int z = minChunk.getZ(); z <= maxChunk.getZ(); z++) {
                this.getWorld().refreshChunk(x, z);
            }
        }

        v.sendMessage("brush took " + (System.currentTimeMillis() - time) + "ms");
    }


    @Override
    protected final void arrow(final SnipeData v) {
        this.ball(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.ball(v, this.getLastBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.sendMessage(Messages.BALLBRUSH_USAGE.replace("%triggerHandle%", triggerHandle));
            return;
        }
        if (params[0].equalsIgnoreCase("smooth")) {
            this.smoothSphere = !this.smoothSphere;
            v.sendMessage(Messages.SMOOTHSPHERE_ALGORITHM.replace("%smoothSphere%", String.valueOf(this.smoothSphere)));
            return;
        }

        v.sendMessage(Messages.BRUSH_INVALID_PARAM.replace("%triggerHandle%", triggerHandle));
        sendPerformerMessage(triggerHandle, v);
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("smooth"));

        arguments.addAll(super.registerArguments());
        return arguments;
    }
    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ball";
    }
}
