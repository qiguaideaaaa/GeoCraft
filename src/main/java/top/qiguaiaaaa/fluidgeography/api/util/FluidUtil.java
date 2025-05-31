package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import top.qiguaiaaaa.fluidgeography.api.util.exception.UnsupportedFluidException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.block.BlockLiquid.LEVEL;

public final class FluidUtil {
    public static final int ONE_IN_EIGHT_OF_BUCKET_VOLUME = Fluid.BUCKET_VOLUME/8;

    /**
     * 对应方块是否是一个液体
     * @param state 方块状态
     */
    public static boolean isFluid(@Nonnull IBlockState state) {
        return state.getMaterial().isLiquid() || state.getBlock() instanceof IFluidBlock;
    }
    /**
     * 对应方块是否是一个液体
     * @param block 方块
     */
    public static boolean isFluid(@Nonnull Block block) {
        return block instanceof BlockLiquid || block instanceof IFluidBlock;
    }

    /**
     * 检测是否是一个完整的流体方块
     * @param state 状态
     * @return 检测结果
     */
    public static boolean isFullFluid(World world,BlockPos pos,IBlockState state){
        if(!isFluid(state)) return false;
        Block block = state.getBlock();
        if(block instanceof BlockLiquid || block instanceof BlockFluidClassic){
            return state.getValue(LEVEL) == 0;
        }else if(block instanceof BlockFluidFinite){
            BlockFluidFinite fluidBlock = (BlockFluidFinite) block;
            return fluidBlock.getQuantaPercentage(world,pos) == 1f;
        }else if(block instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) block;
            FluidStack stack = fluidBlock.drain(world,pos,false);
            if(stack == null) return false;
            return stack.amount == Fluid.BUCKET_VOLUME;
        }
        try{
            return state.getValue(LEVEL) == 0;
        }catch (Throwable e){
            throw new UnsupportedFluidException(state.getBlock());
        }
    }

    public static boolean isFluidPlaceable(World world,BlockPos pos,Fluid fluid){
        IBlockState state = world.getBlockState(pos);
        if(!FluidUtil.isFluid(state)){
            boolean isNonSolid = !state.getMaterial().isSolid();
            boolean isReplaceable = state.getBlock().isReplaceable(world, pos);
            return  world.isAirBlock(pos) || isNonSolid || isReplaceable ;
        }
        if(FluidUtil.getFluid(state) != fluid) return false;
        return !FluidUtil.isFullFluid(world,pos,state);
    }

    /**
     * 获取指定方块状态的液体
     * @param state 方块状态
     * @return 指定方块状态的液体，若没有则返回null
     */
    @Nullable
    public static Fluid getFluid(IBlockState state) {
        Block block = state.getBlock();

        if (block instanceof IFluidBlock) {
            return ((IFluidBlock)block).getFluid();
        }else if (block instanceof BlockLiquid) {
            if (state.getMaterial() == Material.WATER) {
                return FluidRegistry.WATER;
            }else if (state.getMaterial() == Material.LAVA) {
                return FluidRegistry.LAVA;
            }
        }
        return null;
    }
    /**
     * 获取指定方块的液体
     * @param block 方块
     * @return 指定方块的液体，若没有则返回null
     */
    @Nullable
    public static Fluid getFluid(Block block) {
        if (block instanceof IFluidBlock) {
            return ((IFluidBlock)block).getFluid();
        }else if (block instanceof BlockLiquid) {
            Material material = block.getDefaultState().getMaterial();
            if (material == Material.WATER) {
                return FluidRegistry.WATER;
            }else if (material == Material.LAVA) {
                return FluidRegistry.LAVA;
            }
        }
        return null;
    }

    /**
     * 获得溶液方块剩余量,单位量
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param state 方块状态
     * @return 一个数值，表示剩余的量
     */
    public static int getFluidQuanta(World worldIn, BlockPos pos, IBlockState state){
        if(!isFluid(state)) return 0;
        if(state.getBlock() instanceof BlockFluidBase){
            BlockFluidBase fluidBase = (BlockFluidBase) state.getBlock();
            return Math.max(fluidBase.getQuantaValue(worldIn,pos),0);
        }
        if(state.getBlock() instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            FluidStack fluidStack = fluidBlock.drain(worldIn,pos,false);
            if(fluidStack == null) return 0;
            return fluidStack.amount/ONE_IN_EIGHT_OF_BUCKET_VOLUME;
        }
        int stateValue = state.getValue(BlockLiquid.LEVEL);
        if(stateValue>=8) return 1;
        else return 8-stateValue;
    }

    /**
     * 获得溶液方块剩余量,单位mB
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param state 方块状态
     * @return 方块剩余量
     */
    public static int getFluidAmount(World worldIn, BlockPos pos, IBlockState state){
        if(!isFluid(state)) return 0;
        if(state.getBlock() instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            FluidStack fluidStack = fluidBlock.drain(worldIn,pos,false);
            if(fluidStack == null)return 0;
            return fluidStack.amount;
        }
        int stateValue = state.getValue(BlockLiquid.LEVEL);
        if(stateValue>=8) return 0;
        else return (8-stateValue)*ONE_IN_EIGHT_OF_BUCKET_VOLUME;
    }

    /**
     * 计算液体流动难度（坡度的余切）
     * @param distance 距离
     * @param heightDiff 难易度
     * @return 坡度的余切
     */
    public static double getFlowDifficulty(int distance,int heightDiff){
        return distance/((double)heightDiff);
    }

}
