package top.qiguaiaaaa.fluidgeography.util.mixinapi;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface IVanillaFlowChecker {
    /**
     * 检查当前位置的原版液体是否能够流动
     * @param worldIn 世界
     * @param pos 位置
     * @param state 方块状态
     * @param rand 随机数生成器
     * @return 如果能够流动，则返回true
     */
    boolean canFlow(World worldIn, BlockPos pos, IBlockState state, Random rand);
}
