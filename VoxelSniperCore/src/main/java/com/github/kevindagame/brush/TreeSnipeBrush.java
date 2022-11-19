package com.github.kevindagame.brush;

import com.github.kevindagame.snipe.SnipeData;
import com.github.kevindagame.snipe.Undo;
import com.github.kevindagame.util.Messages;
import com.github.kevindagame.util.VoxelMessage;
import com.github.kevindagame.voxelsniper.block.BlockFace;
import com.github.kevindagame.voxelsniper.block.IBlock;
import com.github.kevindagame.voxelsniper.blockstate.IBlockState;
import com.github.kevindagame.voxelsniper.material.VoxelMaterial;
import com.github.kevindagame.voxelsniper.treeType.VoxelTreeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.List;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Tree_Brush
 *
 * @author Mick
 */
public class TreeSnipeBrush extends AbstractBrush {

    private VoxelTreeType treeType = VoxelTreeType.TREE;

    /**
     *
     */
    public TreeSnipeBrush() {
        this.setName("Tree Snipe");
    }

    private void single(final SnipeData v, IBlock targetBlock) {
        this.positions.add(targetBlock.getLocation());
    }

    private int getYOffset() {
        // getMaxHeight() is the same as getTargetBlock().getWorld().getMaxHeight()
        for (int i = 1; i < (getMaxHeight() - 1 - getTargetBlock().getY()); i++) {
            if (getTargetBlock().getRelative(0, i + 1, 0).getMaterial().isAir()) {
                return i;
            }
        }
        return 0;
    }

    private void printTreeType(final VoxelMessage vm) {
        TextComponent.Builder printout = Component.text();

        boolean delimiterHelper = true;
        for (final VoxelTreeType treeType : VoxelTreeType.values()) {
            if (treeType.isSupported()) {
                if (delimiterHelper) {
                    delimiterHelper = false;
                } else {
                    printout.append(Component.text(", ").color(NamedTextColor.WHITE));
                }
                printout.append(Component.text(treeType.name().toLowerCase()).color(treeType.equals(this.treeType) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY));
            }
        }

        vm.custom(printout);
    }

    @Override
    protected boolean actPerform(SnipeData v) {
        Undo undo = new Undo();
        for (var position : positions) {
            IBlock blockBelow = position.getBlock().getRelative(BlockFace.DOWN);
            IBlockState currentState = blockBelow.getState();
            undo.put(currentState);
            blockBelow.setMaterial(VoxelMaterial.GRASS_BLOCK, false);

            undo = this.getWorld().generateTree(position, this.treeType, undo);

            blockBelow.setBlockData(currentState.getBlockData(), false);
        }
        v.owner().storeUndo(undo);
        return true;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        IBlock targetBlock = getTargetBlock().getRelative(0, getYOffset(), 0);
        this.single(v, targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.single(v, getTargetBlock());
    }

    @Override
    public final void info(final VoxelMessage vm) {
        vm.brushName(this.getName());
        this.printTreeType(vm);
    }

    @Override
    public final void parseParameters(final String triggerHandle, final String[] params, final SnipeData v) {
        if (params[0].equalsIgnoreCase("info")) {
            v.sendMessage(Messages.TREESNIPE_USAGE.replace("%triggerHandle%", triggerHandle));
            return;
        }

        try {
            this.treeType = VoxelTreeType.valueOf(params[0].toUpperCase());
            if (treeType.isSupported())
                this.printTreeType(v.getVoxelMessage());
            else
                throw new IllegalArgumentException("Not supported");
        } catch (Exception e) {
            v.getVoxelMessage().brushMessage(Messages.TREESNIPE_BRUSH_MESSAGE.replace("%triggerHandle%", triggerHandle));
        }
    }

    @Override
    public List<String> registerArguments() {

        return Arrays.stream(VoxelTreeType.values()).filter(VoxelTreeType::isSupported).map(Enum::name).toList();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.treesnipe";
    }
}
