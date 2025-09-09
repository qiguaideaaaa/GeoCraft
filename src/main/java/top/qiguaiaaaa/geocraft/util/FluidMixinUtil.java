package top.qiguaiaaaa.geocraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.mixin.common.BlockFluidBaseAccessor;

public final class FluidMixinUtil {
    /**
     * 获取指定液体的单方块容量
     * @param block 流体方块
     */
    public static int getQuantaPerBlock(Block block){
        if(!FluidUtil.isFluid(block)) return -1;
        if(block instanceof BlockLiquid) return 8;
        if(block instanceof BlockFluidBase) return ((BlockFluidBaseAccessor)block).getQuantaPerBlock();
        throw new IllegalArgumentException("Unsupported Liquid");
    }

    public static int getAmountForBlockFluidBase(IBlockState state){
        int quantaPerBlock = getQuantaPerBlock(state.getBlock());
        int quanta = quantaPerBlock-state.getValue(BlockFluidBase.LEVEL);
        return quanta* Fluid.BUCKET_VOLUME/quantaPerBlock;
    }
}
