package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateBaseTask;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure.RealityPressureTaskBuilder;
import top.qiguaiaaaa.geocraft.handler.BlockUpdater;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
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
    protected final byte quantaPerBlock,tickRate,densityDir;
    protected IBlockState state;
    public MoreRealityBlockFluidClassicUpdateTask(@Nonnull Fluid fluid, @Nonnull BlockPos pos,@Nonnull BlockFluidClassic block,int quantaPerBlock,int tickRate,int densityDir) {
        super(fluid, pos);
        this.block = block;
        this.quantaPerBlock = (byte) quantaPerBlock;
        this.tickRate = (byte) tickRate;
        this.densityDir = (byte) densityDir;
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
                FluidOperationUtil.swapFluid(world,pos,downPos);
            }else{
                FluidOperationUtil.moveFluid(world,pos,downPos);
            }
            return;
        }
        //坡度流动模式
        if(quanta == 1){
            if(isSameLiquid(stateBelow)){
                if(!managePressureTask(world,rand)) updateUp(world,rand);
                return;
            }
            Set<EnumFacing> directions = this.getPossibleFlowDirections(world, pos,densityDir,quantaPerBlock);
            if(directions.isEmpty()){
                if(!managePressureTask(world,rand)) updateUp(world,rand);
                return;
            }
            EnumFacing randomFacing = (EnumFacing) directions.toArray()[rand.nextInt(directions.size())];
            world.setBlockToAir(pos);
            this.flowIntoBlockDirectly(world, pos.offset(randomFacing),state, meta);
            return;
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = world.getBlockState(pos.offset(facing));
            if(!this.canDisplaceEvenIsFluid(world,pos.offset(facing))) continue;
            int facingQuanta = FluidUtil.getFluidQuanta(world,pos.offset(facing),facingState);
            if(facingQuanta<quanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
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
                BlockUpdater.scheduleUpdate(world,pos, block, this.tickRate);
                world.notifyNeighborsOfStateChange(pos,block, false);
            }
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                flowIntoBlockDirectly(world,facingPos,state,quantaPerBlock-choice.getQuanta());
            }
        }else{
            if(!managePressureTask(world,rand)) updateUp(world,rand);
        }
    }

    protected boolean managePressureTask(World world, Random rand){
        if(FluidPressureSearchManager.isTaskRunning(world,pos)){
            BlockUpdater.scheduleUpdate(world,pos,block,block.tickRate(world));
            return false;
        }
        Collection<BlockPos> res =FluidPressureSearchManager.getTaskResult(world,pos);
        if(res == null || res.isEmpty()){
            sendPressureQuery(world,rand,BaseUtil.getRandomPressureSearchRange(),false);
            return false;
        }
        IBlockState nowState =state;
        for(BlockPos toPos:res){
            if(FluidUtil.getFluid(nowState) != fluid) break;
            if(tryMoveInto(world,toPos,pos,nowState)) break;
            nowState = world.getBlockState(pos);
        }
        if(nowState != state && FluidUtil.getFluid(nowState) == fluid){
            sendPressureQuery(world,rand,BaseUtil.getRandomPressureSearchRange(),true);
        }else if(nowState == state){
            sendPressureQuery(world,rand,BaseUtil.getRandomPressureSearchRange(),false);
            return false;
        }
        return true;
    }

    protected void updateUp(World world,Random random){
        if(random.nextInt(3)<2) return;
        IBlockState up =world.getBlockState(pos.down(densityDir));
        if(up.getBlock() == block){
            BlockUpdater.scheduleUpdate(world,pos.down(densityDir),block,block.tickRate(world));
        }
    }

    protected void sendPressureQuery(World world,Random rand,int range,boolean directly){
        IBlockState up = world.getBlockState(pos.down(densityDir));
        if(FluidUtil.getFluid(up)!=fluid && (directly || rand.nextInt(3) <2)) {
            FluidPressureSearchManager.addTask(world, RealityPressureTaskBuilder.createModClassicTask(fluid,state,pos, range,quantaPerBlock));
        }
    }

    protected boolean tryMoveInto(World world,BlockPos pos,BlockPos srcPos,IBlockState myState){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR){
            int quanta = quantaPerBlock -myState.getValue(BlockFluidClassic.LEVEL);
            int movQuanta = srcPos.getY()==pos.getY()?quanta/2:quanta;
            if(movQuanta <= 0)return false;
            quanta -=movQuanta;
            if(quanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,block.getDefaultState().withProperty(BlockFluidClassic.LEVEL,quantaPerBlock-quanta));
            world.setBlockState(pos,block.getDefaultState().withProperty(BlockFluidClassic.LEVEL,quantaPerBlock-movQuanta));
            return quanta == 0;
        }else if(FluidUtil.getFluid(state) == fluid){
            int quanta = quantaPerBlock-state.getValue(BlockFluidClassic.LEVEL);
            int myQuanta = quantaPerBlock -myState.getValue(BlockFluidClassic.LEVEL);
            if(pos.getY() == srcPos.getY() && quanta>=myQuanta-1) return false;
            int movQuanta = srcPos.getY()==pos.getY()?(myQuanta-quanta)/2:Math.min(quantaPerBlock-quanta,myQuanta);
            myQuanta -=movQuanta;
            if(myQuanta <= 0){
                world.setBlockToAir(srcPos);
            }else world.setBlockState(srcPos,block.getDefaultState().withProperty(BlockFluidClassic.LEVEL,quantaPerBlock-myQuanta));
            quanta += movQuanta;
            world.setBlockState(pos,block.getDefaultState().withProperty(BlockFluidClassic.LEVEL,quantaPerBlock-quanta));
            return myQuanta==0;
        }
        return false;
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
