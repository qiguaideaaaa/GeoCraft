package top.qiguaiaaaa.geocraft.util.wrappers;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.api.util.math.PlaceChoice;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;

import java.util.*;
import java.util.function.Predicate;

import static top.qiguaiaaaa.geocraft.util.FluidSearchUtil.DIRS4;

public class PhysicsBlockLiquidWrapper extends BlockLiquidWrapper {
    protected final Fluid fluid;
    public PhysicsBlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        super(blockLiquid, world, blockPos);
        fluid = FluidUtil.getFluid(blockLiquid);
    }
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource.amount < Fluid.BUCKET_VOLUME) {
            return 0;
        }

        final Set<PlaceChoice> choices = FluidSearchUtil.findPlaceableLocations(world,blockPos,fluid,8);
        if(choices.isEmpty()) return 0;
        int quantaLeft = 8;
        for(PlaceChoice choice:choices){
             quantaLeft = placeLiquid(choice.pos,quantaLeft,doFill);
             if(quantaLeft<=0) break;
        }
        if(quantaLeft <=0) return Fluid.BUCKET_VOLUME;
        return 0;

    }

    /**
     * 在某处放置指定量的液体
     * @param placePos 位置
     * @param placeQuanta 放置量
     * @return 剩余未放置的量
     */
    protected int placeLiquid(BlockPos placePos,int placeQuanta,boolean doFill){
        if(placeQuanta <=0) return 0;
        IBlockState state = world.getBlockState(placePos);
        int quanta = FluidUtil.getFluidQuanta(world,placePos,state);
        int newQuanta = quanta+placeQuanta;
        if(newQuanta<=8){
            if(doFill) directlyPlaceLiquid(placePos,8-newQuanta);
            return 0;
        }
        if(doFill) directlyPlaceLiquid(placePos,0);
        return newQuanta-8;
    }
    protected void directlyPlaceLiquid(BlockPos placePos,int level){
        Material material = blockLiquid.getDefaultState().getMaterial();
        BlockLiquid block = BlockLiquid.getFlowingBlock(material);
        world.setBlockState(placePos, block.getDefaultState().withProperty(BlockLiquid.LEVEL, level), 11);
    }
}
