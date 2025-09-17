package top.qiguaiaaaa.geocraft.util.misc;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;

/**
 * @author QiguaiAAAA
 */
public class ExtendedNextTickListEntry extends NextTickListEntry {
    public ExtendedNextTickListEntry(World world, BlockPos positionIn, Block blockIn, int delay,int priority) {
        super(positionIn, blockIn);
        this.setPriority(priority);
        this.setScheduledTime(world.getTotalWorldTime()+delay);
    }
}
