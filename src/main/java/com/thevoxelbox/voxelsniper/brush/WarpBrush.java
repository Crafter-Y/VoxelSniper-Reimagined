package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.util.VoxelMessage;
import com.thevoxelbox.voxelsniper.voxelsniper.entity.player.IPlayer;
import com.thevoxelbox.voxelsniper.voxelsniper.location.VoxelLocation;

/**
 * @author MikeMatrix
 */
public class WarpBrush extends Brush {

    /**
     *
     */
    public WarpBrush() {
        this.setName("Warp");
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        IPlayer player = v.owner().getPlayer();
        VoxelLocation location = this.getLastBlock().getLocation();
        VoxelLocation playerLocation = player.getLocation();
        location.setPitch(playerLocation.getPitch());
        location.setYaw(playerLocation.getYaw());

        player.teleport(location);
    }

    @Override
    protected final void powder(final SnipeData v) {
        IPlayer player = v.owner().getPlayer();
        VoxelLocation location = this.getLastBlock().getLocation();
        VoxelLocation playerLocation = player.getLocation();
        location.setPitch(playerLocation.getPitch());
        location.setYaw(playerLocation.getYaw());

        getWorld().strikeLightning(location);
        player.teleport(location);
        getWorld().strikeLightning(location);
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.warp";
    }
}
