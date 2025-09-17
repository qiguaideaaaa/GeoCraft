package top.qiguaiaaaa.geocraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;
import top.qiguaiaaaa.geocraft.configs.GeneralConfig;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import top.qiguaiaaaa.geocraft.api.util.exception.UnsupportedFluidException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static top.qiguaiaaaa.geocraft.api.util.FluidUtil.*;

public final class FluidOperationUtil {
    public static FluidStack tryDrainFluid(World world, BlockPos pos, int target, final int searchIterations, boolean doDrain){
        int currentDrained = 0;
        IBlockState state = world.getBlockState(pos);
        Fluid fluid = getFluid(state);
        if(fluid == null) return null;
        BlockPos lastDrained = null;
        Set<BlockPos> visited = new HashSet<>();
        while(currentDrained<target) {
            Optional<BlockPos> optional = FluidSearchUtil.findFluid(world, pos, fluid, visited, false, false, searchIterations);
            if (!optional.isPresent()) break;
            BlockPos posToDrain = optional.get();
            visited.add(posToDrain);
            FluidStack stack = drainFluidDirectly(world, posToDrain, doDrain);
            if (stack == null) break;
            lastDrained = posToDrain;
            currentDrained += stack.amount;
        }
        if(currentDrained == 0) return null;
        if(currentDrained>target && doDrain){
            int diff = currentDrained - target;
            int placed = placeFluidDirectly(world,lastDrained,fluid,diff, true);
            currentDrained -= placed;
        }
        return new FluidStack(fluid,currentDrained);
    }

    public static FluidStack drainFluidDirectly(World world, BlockPos pos, boolean doDrain){
        IBlockState state = world.getBlockState(pos);
        if(!isFluid(state)) return null;
        Block block = state.getBlock();
        if(block instanceof IFluidBlock){
            return ((IFluidBlock) block).drain(world, pos, doDrain);
        }else if(block instanceof BlockLiquid){
            Fluid fluid = getFluid(block);
            if(fluid == null) return null;
            int quanta = getFluidQuanta(world,pos,state);
            FluidStack stack = new FluidStack(fluid,quanta* ONE_IN_EIGHT_OF_BUCKET_VOLUME);
            if(doDrain){
                world.setBlockToAir(pos);
            }
            return stack;
        }
        return null;
    }

    public static int placeFluidDirectly(World world, BlockPos pos, Fluid fluid, int amount, boolean doPlace){
        if(!isFluidPlaceable(world,pos,fluid)) return 0;
       Block block = fluid.getBlock();
       if(block instanceof IFluidBlock){
           return ((IFluidBlock) block).place(world,pos,new FluidStack(fluid,amount),doPlace);
       }else if(block instanceof BlockLiquid){
           IBlockState state = block.getDefaultState();
           if(doPlace) setQuanta(world,pos,state,amount/ ONE_IN_EIGHT_OF_BUCKET_VOLUME);
           return amount;
       }
       return 0;
    }

    /**
     * 将指定位置的流体源移动到新的位置，并将原流体源改为LEVEL = 1的流体方块，仅适用于{@link SimulationMode}偏原版流体的情况
     * @param worldIn 世界
     * @param srcPos 源位置
     * @param newPos 新位置
     */
    public static void moveFluidSource(World worldIn, BlockPos srcPos, BlockPos newPos){
        IBlockState srcState = worldIn.getBlockState(srcPos);
        Block srcBlock = srcState.getBlock();
        if(!isFluid(srcBlock)) return;
        IBlockState newPosState = worldIn.getBlockState(newPos);
        triggerDestroyBlockEffectByFluid(worldIn,newPos,newPosState, getFluid(srcState));

        if(srcBlock instanceof BlockLiquid){
            worldIn.setBlockState(srcPos,BlockLiquid.getStaticBlock(srcState.getMaterial()).getDefaultState().withProperty(LEVEL,1), Constants.BlockFlags.SEND_TO_CLIENTS);
            worldIn.setBlockState(newPos,BlockLiquid.getStaticBlock(srcState.getMaterial()).getDefaultState().withProperty(LEVEL,0),Constants.BlockFlags.SEND_TO_CLIENTS);
            worldIn.notifyNeighborsOfStateChange(srcPos, BlockLiquid.getStaticBlock(srcState.getMaterial()), false);
        }else if(srcBlock instanceof BlockFluidClassic){
            BlockFluidBase fluidBlock = (BlockFluidBase) srcBlock;
            worldIn.setBlockState(srcPos,fluidBlock.getDefaultState().withProperty(LEVEL,1),Constants.BlockFlags.SEND_TO_CLIENTS);
            worldIn.setBlockState(newPos,srcState,Constants.BlockFlags.SEND_TO_CLIENTS);
            worldIn.notifyNeighborsOfStateChange(srcPos,srcBlock,false);
        }else if(srcBlock instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) srcBlock;
            FluidStack stack = fluidBlock.drain(worldIn,srcPos,true);
            if(stack == null) return;
            fluidBlock.place(worldIn,newPos,stack,true);
        }
    }

    /**
     * 将指定位置的流体方块移动到新的位置，并将原位置替换为空气
     * @param worldIn 世界
     * @param srcPos 源位置
     * @param newPos 新位置
     */
    public static void moveFluid(World worldIn,BlockPos srcPos,BlockPos newPos){
        IBlockState srcState = worldIn.getBlockState(srcPos);
        IBlockState newPosState = worldIn.getBlockState(newPos);
        Block srcBlock = srcState.getBlock();
        triggerDestroyBlockEffectByFluid(worldIn,newPos,newPosState, getFluid(srcState));
        if(srcBlock instanceof BlockLiquid){
            int srcLevel = srcState.getValue(LEVEL);
            worldIn.setBlockToAir(srcPos);
            worldIn.setBlockState(newPos,BlockLiquid.getFlowingBlock(srcState.getMaterial()).getDefaultState().withProperty(LEVEL,srcLevel));
        }else if(srcBlock instanceof IFluidBlock){
            worldIn.setBlockToAir(srcPos);
            worldIn.setBlockState(newPos,srcState);
        }
    }

    /**
     * 将两个位置的流体交换
     * @param world 所在世界
     * @param aPos 第一个流体的位置
     * @param bPos 第二个流体的位置
     */
    public static void swapFluid(World world,BlockPos aPos,BlockPos bPos){
        IBlockState aState = world.getBlockState(aPos);
        IBlockState bState = world.getBlockState(bPos);
        world.setBlockToAir(aPos);
        world.setBlockState(bPos,aState);
        world.setBlockState(aPos,bState);
    }

    /**
     * 以指定液体的形式生成摧毁某位置的方块效果
     * @param worldIn 所在世界
     * @param pos 位置
     * @param state 对应方块状态
     * @param destroyer 液体
     * @return 如果方块为空气(根据Material)或为流体或方块不会掉落物品，则返回false，否则则返回true
     * <br/>其中，当方块温度高于等于{@link GeneralConfig}的leastTemperatureForFluidToCompletelyDestroyBlock配置项时，将不会掉落物品
     */
    public static boolean triggerDestroyBlockEffectByFluid(World worldIn, BlockPos pos, IBlockState state, Fluid destroyer){
        if(state.getMaterial() == Material.AIR) return false;
        if(isFluid(state)) return false;
        boolean canDropItem = (destroyer == null || destroyer.getTemperature(worldIn, pos) < GeneralConfig.leastTemperatureForFluidToCompletelyDestroyBlock.getValue());
        if(canDropItem && state.getBlock() != Blocks.SNOW_LAYER){
            state.getBlock().dropBlockAsItem(worldIn,pos,state,0);
        }else if(!canDropItem){
            triggerFluidMixEffects(worldIn,pos);
        }
        return canDropItem;
    }

    public static void setQuanta(World world, BlockPos pos, IBlockState state, int newQuanta){
        if(!isFluid(state)) return;
        if(newQuanta <= 0){
            world.setBlockToAir(pos);
            return;
        }
        Block block = state.getBlock();
        if(block instanceof BlockLiquid || block instanceof BlockFluidClassic){
            int quantaPerBlock = FluidMixinUtil.getQuantaPerBlock(block);
            int newLevel = quantaPerBlock-newQuanta;
            setLevel(world,pos,state,newLevel);
        }else if(block instanceof BlockFluidFinite){
            int newLevel = newQuanta-1;
            setLevel(world,pos,state,newLevel);
        }else if(block instanceof BlockFluidBase){
            int quantaPerBlock = FluidMixinUtil.getQuantaPerBlock(block);
            float amountPerQuanta = Fluid.BUCKET_VOLUME/(float)quantaPerBlock;
            BlockFluidBase blockFluid = (BlockFluidBase) block;
            blockFluid.drain(world,pos,true);
            blockFluid.place(world,pos,new FluidStack(blockFluid.getFluid(), MathHelper.floor(amountPerQuanta*newQuanta)),true);
        }else if(block instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) block;
            fluidBlock.place(world,pos,new FluidStack(fluidBlock.getFluid(), ONE_IN_EIGHT_OF_BUCKET_VOLUME*newQuanta),true);
        }else{
            throw new UnsupportedFluidException(state.getBlock());
        }
    }

    public static void addQuanta(World world,BlockPos pos,Block block,int quanta){
        if(!isFluid(block)) return;
        if(quanta <=0) return;
        IBlockState state = world.getBlockState(pos);
        if(!isFluid(state)){
            if(!block.canPlaceBlockAt(world,pos)) return;
            FluidOperationUtil.setQuanta(world,pos,block.getDefaultState(),quanta);
            return;
        }

        if(block instanceof BlockLiquid || block instanceof BlockFluidClassic){
            int level= state.getValue(LEVEL);
            int newLevel = level-1;
            FluidOperationUtil.setLevel(world,pos,state,newLevel);
        }else if(block instanceof BlockFluidFinite){
            int level= state.getValue(LEVEL);
            int newLevel = level+1;
            FluidOperationUtil.setLevel(world,pos,state,newLevel);
        }else if(block instanceof BlockFluidBase){
            int quantaPerBlock = FluidMixinUtil.getQuantaPerBlock(block);
            float amountPerQuanta = Fluid.BUCKET_VOLUME/(float)quantaPerBlock;
            BlockFluidBase blockFluid = (BlockFluidBase) block;
            FluidStack stack = blockFluid.drain(world,pos,true);
            int rawAmount = stack == null?0:stack.amount;
            blockFluid.place(world,pos,new FluidStack(blockFluid.getFluid(),rawAmount+MathHelper.floor(amountPerQuanta*quanta)),true);
        }else if(block instanceof IFluidBlock){
            IFluidBlock fluidBlock = (IFluidBlock) block;
            FluidStack stack = fluidBlock.drain(world,pos,true);
            int rawAmount = stack == null?0:stack.amount;
            fluidBlock.place(world,pos,new FluidStack(fluidBlock.getFluid(), rawAmount+ONE_IN_EIGHT_OF_BUCKET_VOLUME*quanta),true);
        }else{
            throw new UnsupportedFluidException(state.getBlock());
        }
    }

    /**
     * 将指定方块的液体等级设置为指定等级
     * @param world 所在世界
     * @param pos 位置
     * @param state 指定液体方块状态
     * @param newLevel 新等级
     */
    public static void setLevel(World world,BlockPos pos,IBlockState state,int newLevel){
        if(newLevel<0) return;
        if(!isFluid(state)) return;
        try {
            state.getValue(LEVEL);
            state = state.withProperty(LEVEL,newLevel);
            world.setBlockState(pos,state);
        }catch (Throwable e){
            throw new UnsupportedFluidException(state.getBlock());
        }
    }

    /**
     * 在指定位置生成高温液体流动声音
     * @param worldIn 所在世界
     * @param pos 位置
     */
    public static void triggerFluidMixEffects(World worldIn, BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        worldIn.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + 1.2D, z + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
}
