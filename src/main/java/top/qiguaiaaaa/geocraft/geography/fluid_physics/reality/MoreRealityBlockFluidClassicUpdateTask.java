package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateBaseTask;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.IMoreRealityBlockFluidBase;

import javax.annotation.Nonnull;
import java.util.*;

import static net.minecraftforge.fluids.BlockFluidBase.LEVEL;

/**
 * @author QiguaiAAAA
 */
public class MoreRealityBlockFluidClassicUpdateTask extends FluidUpdateBaseTask implements IMoreRealityBlockFluidBase<BlockFluidClassic> {
    protected final BlockFluidClassic block;
    protected final int quantaPerBlock,tickRate,densityDir;
    protected IBlockState state;
    public MoreRealityBlockFluidClassicUpdateTask(@Nonnull Fluid fluid, @Nonnull BlockPos pos,@Nonnull BlockFluidClassic block,int quantaPerBlock,int tickRate,int densityDir) {
        super(fluid, pos);
        this.block = block;
        this.quantaPerBlock = quantaPerBlock;
        this.tickRate = tickRate;
        this.densityDir = densityDir;
    }

    @Override
    public void onUpdate(@Nonnull World world, @Nonnull IBlockState curState, @Nonnull Random rand) {
        this.state = curState;
        int meta = state.getValue(LEVEL);
        int quanta = quantaPerBlock-meta;
        BlockPos downPos = pos.up(densityDir);

        IBlockState stateBelow = world.getBlockState(downPos);
        boolean canMoveDown = this.canMoveDownTo(world, downPos);
        if(canMoveDown){
            if(isSameLiquid(stateBelow)){
                flowDown(world,pos,downPos,state,stateBelow,quanta,quantaPerBlock);
            }else if(FluidUtil.isFluid(stateBelow)){//下面密度小，交换上下液体
                FluidOperationUtil.swapFluid(world,pos,pos.down());
            }else{
                FluidOperationUtil.moveFluid(world,pos,downPos);
            }
            return;
        }
        //坡度流动模式
        if(quanta == 1){
            Set<EnumFacing> directions = this.getPossibleFlowDirections(world, pos,densityDir,quantaPerBlock);
            if(directions.isEmpty()){
                return;
            }
            EnumFacing randomFacing = (EnumFacing) directions.toArray()[rand.nextInt(directions.size())];
            world.setBlockToAir(pos);
            this.flowIntoBlockDirectly(world, pos.offset(randomFacing),state, meta);
            return;
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        Set<EnumFacing> slopeModeFlowDirections = EnumSet.noneOf(EnumFacing.class);//非Q=1坡度模式可用方向
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = world.getBlockState(pos.offset(facing));
            if(!this.canDisplaceEvenIsFluid(world,pos.offset(facing))) continue;
            int facingQuanta = FluidUtil.getFluidQuanta(world,pos.offset(facing),facingState);
            if(facingQuanta<quanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
            if(facingQuanta<quanta) slopeModeFlowDirections.add(facing);
        }
        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getQuanta));
            averageModeFlowDirections.add(new FlowChoice(quantaPerBlock,null));
            int newQuanta = quanta;
            while(averageModeFlowDirections.get(0).getQuanta()<newQuanta-1){ //向四周分配流量
                newQuanta--;
                averageModeFlowDirections.get(0).addQuanta(1);
                if(averageModeFlowDirections.get(0).getQuanta() > averageModeFlowDirections.get(1).getQuanta()){
                    Collections.swap(averageModeFlowDirections,0,1);
                }
            }
            quanta = newQuanta;
            meta = quantaPerBlock - quanta;
            if (quanta<=0) world.setBlockToAir(pos); //先更新自身状态
            else {
                state = state.withProperty(LEVEL,meta);
                world.setBlockState(pos, state, Constants.BlockFlags.SEND_TO_CLIENTS);
                FluidUpdateManager.scheduleUpdate(world,pos, block, this.tickRate);
                world.notifyNeighborsOfStateChange(pos,block, false);
            }
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                flowIntoBlockDirectly(world,facingPos,state,quantaPerBlock-choice.getQuanta());
            }
        }else if(!slopeModeFlowDirections.isEmpty()){ //非Q=1坡度模式
            slopeModeFlowDirections = getPossibleFlowDirections(world,pos,slopeModeFlowDirections,densityDir,quantaPerBlock,quanta);
            if(slopeModeFlowDirections.isEmpty()){
                return;
            }
            EnumFacing randomFacing = (EnumFacing) slopeModeFlowDirections.toArray()[rand.nextInt(slopeModeFlowDirections.size())];
            int newLiquidQuanta = quanta-1;
            int newLiquidMeta = quantaPerBlock-newLiquidQuanta;
            //更新自己
            state = state.withProperty(LEVEL,newLiquidMeta);
            world.setBlockState(pos,state,Constants.BlockFlags.SEND_TO_CLIENTS);
            FluidUpdateManager.scheduleUpdate(world,pos,block,this.tickRate);
            world.notifyNeighborsOfStateChange(pos,block,false);
            //移动至新位置
            world.setBlockState(pos.offset(randomFacing), state.withProperty(LEVEL, meta), Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }

    @Nonnull
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockFluidClassic getThis() {
        return block;
    }

    @Override
    public int getTickRate() {
        return tickRate;
    }
}
