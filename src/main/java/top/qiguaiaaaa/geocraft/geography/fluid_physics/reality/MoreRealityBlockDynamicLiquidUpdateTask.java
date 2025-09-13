package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Shadow;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateBaseTask;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.mixin.common.BlockDynamicLiquidAccessor;
import top.qiguaiaaaa.geocraft.mixin.common.BlockLiquidAccessor;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;

import javax.annotation.Nonnull;
import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;

/**
 * @author QiguaiAAAA
 */
public class MoreRealityBlockDynamicLiquidUpdateTask extends FluidUpdateBaseTask {
    protected static final Random random = new Random();
    protected final BlockDynamicLiquid block;
    protected IBlockState state;
    protected Material material;
    public MoreRealityBlockDynamicLiquidUpdateTask(@Nonnull Fluid fluid, @Nonnull BlockPos pos, @Nonnull BlockDynamicLiquid block) {
        super(fluid, pos);
        this.block = block;
    }

    @Override
    public void onUpdate(@Nonnull World world, @Nonnull IBlockState curState, @Nonnull Random rand) {
        if (!world.isAreaLoaded(pos,1)){
            return;
        }
        state = curState;
        material = state.getMaterial();
        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            world.setBlockToAir(pos);
            return;
        }
        int liquidQuanta = 8-liquidMeta;
        int updateRate = block.tickRate(world);

        IBlockState stateBelow = world.getBlockState(pos.down());
        boolean canMoveDown = this.canMoveDownTo(world, pos.down(), stateBelow);

        if(canMoveDown){ //向下流动
            if(isSameLiquid(stateBelow) || (stateBelow.getBlock() == Blocks.SNOW_LAYER && fluid == FluidRegistry.WATER)){
                flowDown(world,pos,stateBelow,liquidQuanta,updateRate);
            }else if(stateBelow.getMaterial() == Material.WATER){ // 流体融合的情况
                liquidQuanta--;
                liquidMeta = 8-liquidQuanta;
                if (liquidQuanta<=0) world.setBlockToAir(pos); //先更新自身状态
                else {
                    state = state.withProperty(LEVEL,liquidMeta);
                    world.setBlockState(pos, state, Constants.BlockFlags.SEND_TO_CLIENTS);
                    FluidUpdateManager.scheduleUpdate(world,pos,block, updateRate);
                    world.notifyNeighborsOfStateChange(pos,block, false);
                }
                world.setBlockState(pos.down(), ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos.down(), pos, Blocks.STONE.getDefaultState()));
                FluidOperationUtil.triggerFluidMixEffects(world,pos.down());
            }else{
                FluidOperationUtil.moveFluid(world,pos,pos.down());
            }
            return;
        }

        if (!world.isAreaLoaded(pos, Math.max(this.getSlopeFindDistance(world),this.getSlopeFindDistance2(world)))){
            return;
        }

        //Q=1 坡度流动模式
        if(liquidMeta == 7){
            if(FluidUtil.getFluid(stateBelow) == fluid){
                this.placeStaticBlock(world,pos,state);
                return;
            }
            Set<EnumFacing> directions = this.getPossibleFlowDirections(world, pos);
            if(directions.isEmpty()){
                this.placeStaticBlock(world,pos,state);
                return;
            }

            EnumFacing randomFacing = (EnumFacing) directions.toArray()[rand.nextInt(directions.size())];
            world.setBlockToAir(pos);
            this.tryFlowInto(world, pos.offset(randomFacing), world.getBlockState(pos.offset(randomFacing)), 7);
            return;
        }
        if ((state.getMaterial() == Material.LAVA) && rand.nextInt(4) != 0){ //岩浆速度处理
            updateRate *= 4;
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        Set<EnumFacing> slopeModeFlowDirections = EnumSet.noneOf(EnumFacing.class);//非Q=1坡度模式可用方向
        this.checkNeighborsToFindFlowChoices(world,pos,liquidQuanta,averageModeFlowDirections,slopeModeFlowDirections);

        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getQuanta));
            averageModeFlowDirections.add(new FlowChoice(8,null));
            int newLiquidQuanta = liquidQuanta;
            while(averageModeFlowDirections.get(0).getQuanta()<newLiquidQuanta-1){ //向四周分配流量
                averageModeFlowDirections.get(0).addQuanta(1);
                newLiquidQuanta--;
                if(averageModeFlowDirections.get(0).getQuanta() > averageModeFlowDirections.get(1).getQuanta()){
                    Collections.swap(averageModeFlowDirections,0,1);
                }
            }
            liquidQuanta = newLiquidQuanta;
            liquidMeta = 8 - liquidQuanta;
            if (liquidQuanta<=0) world.setBlockToAir(pos); //先更新自身状态
            else {
                state = state.withProperty(LEVEL,liquidMeta);
                world.setBlockState(pos, state, net.minecraftforge.common.util.Constants.BlockFlags.SEND_TO_CLIENTS);
                FluidUpdateManager.scheduleUpdate(world,pos,block, updateRate);
                world.notifyNeighborsOfStateChange(pos,block, false);
            }
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                directlyFlowInto(world,facingPos,world.getBlockState(facingPos),8-choice.getQuanta());
            }
        }else if(!slopeModeFlowDirections.isEmpty()){ //非Q=1坡度模式,现在改用压强
//            slopeModeFlowDirections = getPossibleFlowDirections(world,pos,slopeModeFlowDirections,liquidQuanta);
//            if(slopeModeFlowDirections.isEmpty()){
//                this.placeStaticBlock(world,pos,state);
//                return;
//            }
//            EnumFacing randomFacing = (EnumFacing) slopeModeFlowDirections.toArray()[rand.nextInt(slopeModeFlowDirections.size())];
//            int newLiquidQuanta = liquidQuanta-1;
//            int newLiquidMeta = 8-newLiquidQuanta;
//            //更新自己
//            state = state.withProperty(LEVEL,newLiquidMeta);
//            world.setBlockState(pos,state, net.minecraftforge.common.util.Constants.BlockFlags.SEND_TO_CLIENTS);
//            world.scheduleUpdate(pos,block, updateRate);
//            world.notifyNeighborsOfStateChange(pos,block,false);
//            //移动至新位置
//            setLiquidToFlowingLevel(world,pos.offset(randomFacing),liquidMeta);
            this.placeStaticBlock(world,pos,state);
        } else{
            this.placeStaticBlock(world,pos,state);
        }
    }

    /**
     * 检查方块四周可流动的选择
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param liquidQuanta 液体量
     * @param averageModeFlowDirections 平均流动模式下的选择列表
     * @param slopeModeFlowDirections Q>1 坡度流动模式下的选择集合
     */
    private void checkNeighborsToFindFlowChoices(World worldIn,BlockPos pos,int liquidQuanta,List<FlowChoice> averageModeFlowDirections,Set<EnumFacing> slopeModeFlowDirections){
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = worldIn.getBlockState(pos.offset(facing));
            if(!canFlowInto2(worldIn,pos.offset(facing),facingState)) continue;
            if(!canFlowIntoWhenItIsSnowLayer(facingState,liquidQuanta)) continue;
            int facingMeta = ((BlockLiquidAccessor)block).getDepth(facingState);
            if(facingMeta <0 || facingMeta>7) facingMeta = 8;
            int facingQuanta = 8-facingMeta;
            if(facingQuanta<liquidQuanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
            if(facingQuanta<liquidQuanta) slopeModeFlowDirections.add(facing);
        }
    }

    /**
     * 在下方有相同流体的情况下，流下去
     * @param world 所在世界
     * @param currentPos 当前位置
     * @param downState 下方方块状态
     * @param liquidQuanta 当前流体量
     * @param tickRate 更新间隔
     */
    private void flowDown(World world,BlockPos currentPos,IBlockState downState,int liquidQuanta,int tickRate){
        if(downState.getBlock() == Blocks.SNOW_LAYER && fluid == FluidRegistry.WATER){
            int belowLayer = downState.getValue(BlockSnow.LAYERS);
            int totalQuanta = belowLayer+liquidQuanta;
            int frozenQuanta = Math.min(8-belowLayer,liquidQuanta);
            if(totalQuanta<=8){
                world.setBlockToAir(currentPos);
                world.setBlockState(currentPos.down(),downState.withProperty(BlockSnow.LAYERS,totalQuanta));
            }else{
                int remain = totalQuanta-8;
                setLiquidToFlowingLevel(world,currentPos,8-remain);
                FluidUpdateManager.scheduleUpdate(world,currentPos,block,tickRate);
                world.setBlockState(currentPos.down(),downState.withProperty(BlockSnow.LAYERS,8));
            }
            IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,currentPos,true);
            if(accessor == null) return;
            accessor.putHeatToUnderlying(frozenQuanta* AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA);
            return;
        }
        int belowQuanta = FluidUtil.getFluidQuanta(world,currentPos.down(),downState);
        int totalQuanta = liquidQuanta+belowQuanta;
        if(totalQuanta<=8){
            world.setBlockToAir(currentPos);
            setLiquidToFlowingLevel(world, currentPos.down(),8-totalQuanta);
        }else{
            int remain = totalQuanta-8;
            setLiquidToFlowingLevel(world,currentPos,8-remain);
            FluidUpdateManager.scheduleUpdate(world,currentPos,block,tickRate);
            setLiquidToFlowingLevel(world,currentPos.down(),0);
        }
    }

    /**
     * 将指定位置的方块设置为指定等级的流动型流体
     * @param worldIn 所在世界
     * @param pos 位置
     * @param newLevel 新等级
     */
    private void setLiquidToFlowingLevel(World worldIn,BlockPos pos,int newLevel){
        worldIn.setBlockState(pos, block.getDefaultState().withProperty(LEVEL,newLevel),Constants.BlockFlags.DEFAULT);
    }

    /**
     * 不检查是否能够流入，直接流入对应方块
     * @param worldIn 所在世界
     * @param pos 流入位置
     * @param state 当前方块状态
     * @param level 流入流体等级
     */
    private void directlyFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
        FluidOperationUtil.triggerDestroyBlockEffectByFluid(worldIn,pos,state,fluid);
        worldIn.setBlockState(pos, block.getDefaultState().withProperty(LEVEL, level), Constants.BlockFlags.DEFAULT);
    }

    /**
     * 是否是相同液体
     */
    private boolean isSameLiquid(IBlockState state){
        Block block = state.getBlock();
        if(block instanceof IFluidBlock) return false;
        return state.getMaterial() == this.material;
    }

    /**
     * 液体是否可以往下流动
     * @param worldIn 所在世界
     * @param pos 下方位置
     * @param state 下方方块状态
     * @return 如果可以往下流动，则返回true
     */
    private boolean canMoveDownTo(World worldIn, BlockPos pos, IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material == Material.WATER && this.material == Material.LAVA) return true;
            if(material != this.material) return false;
            return state.getValue(LEVEL) != 0;
        }
        if(state.getBlock() == Blocks.SNOW_LAYER){
            return state.getValue(BlockSnow.LAYERS) < 8;
        }
        return !this.isBlocked(worldIn,pos,state);
    }

    /**
     * Q>1 坡度流动模式下检查是否能够流入指定方块
     * @param world 所在世界
     * @param pos 检测位置
     * @param state 检测位置方块状态
     * @return 如果可以，则返回true
     */
    private boolean canFlowInto2(World world,BlockPos pos,IBlockState state){
        if(canFlowInto(world,pos,state)) return true;
        return isSameLiquid(state);
    }
    private boolean canFlowIntoWhenItIsSnowLayer(IBlockState state,int thisQuanta){
        if(fluid != FluidRegistry.WATER) return true;
        if(state.getBlock() != Blocks.SNOW_LAYER) return true;
        int layer = state.getValue(BlockSnow.LAYERS);
        return layer < thisQuanta-2;
    }

    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
        int difficulty = 1000;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (this.isBlocked(worldIn, facingPos, state) || !canFlowIntoWhenItIsSnowLayer(state,1) || FluidUtil.isFluid(state)) {
                continue;
            }
            int slope;
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (!canMoveDownTo(worldIn,facingPos.down(),stateBelow)) {
                slope = this.getSlopeDistance(worldIn, facingPos, 1, enumfacing.getOpposite());
            } else{
                slope = 0;
            }

            if (slope < difficulty)
                possibleDirections.clear();
            if (slope <= difficulty) {
                possibleDirections.add(enumfacing);
                difficulty = slope;
            }
        }
        if(difficulty == 1000) possibleDirections.clear();
        return possibleDirections;
    }

    private int getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from) {
        int difficulty = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (this.isBlocked(worldIn, facingPos, state) || !canFlowIntoWhenItIsSnowLayer(state,1) || FluidUtil.isFluid(state)) {
                continue;
            }
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (canMoveDownTo(worldIn,facingPos.down(),stateBelow)) {
                return distance;
            }

            if (distance < this.getSlopeFindDistance(worldIn)) {
                int newDistance = this.getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite());
                if (newDistance < difficulty) difficulty = newDistance;
            }
        }

        return difficulty;
    }

    /**
     * Q>1 坡度流动模式的可流动方向寻找算法
     * @param worldIn 所在世界
     * @param pos 位置
     * @param accessibleDirections 可流动的方向
     * @param thisQuanta 搜寻者的液体量
     * @return 一个流动方向的集合，意味着最佳的流动方向
     */
    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos,Set<EnumFacing> accessibleDirections,int thisQuanta) {
        double difficulty = 10000d;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : accessibleDirections) {
            BlockPos facingPos = pos.offset(enumfacing);

            double slope = this.getSlopeDistance(worldIn, facingPos, 1,thisQuanta, enumfacing.getOpposite());

            if (slope < difficulty)
                possibleDirections.clear();
            if (slope <= difficulty) {
                possibleDirections.add(enumfacing);
                difficulty = slope;
            }
        }
        if(difficulty == 10000d) possibleDirections.clear();
        return possibleDirections;
    }

    /***
     * Q>1 坡度流动模式的可流动方向寻找内层递归算法
     * @param worldIn 所在世界
     * @param pos 位置
     * @param distance 当前距离原点的距离
     * @param thisQuanta 搜寻者的液体量
     * @param from 来源方向
     * @return 难易度，即坡度的余切值
     */
    private double getSlopeDistance(World worldIn, BlockPos pos, int distance,int thisQuanta ,EnumFacing from) {
        double difficulty = 10000d;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);
            int quantaDiffer = getQuantaDiffer(state,thisQuanta);
            boolean isFluid = FluidUtil.isFluid(state);
            boolean isAir = state.getMaterial() == Material.AIR;
            if (this.isBlocked(worldIn, facingPos, state) || !canFlowIntoWhenItIsSnowLayer(state,thisQuanta) || (isFluid && quantaDiffer <1)) {
                continue;
            }
            if(isAir){
                IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
                if (canMoveDownTo(worldIn,facingPos.down(),stateBelow)) {
                    return FluidUtil.getFlowDifficulty(distance*8,8+thisQuanta);
                }else{
                    return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
                }
            }else if(quantaDiffer >1){ //同样的流体
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta-quantaDiffer);
            }else if(!isFluid){ //例如火把
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
            }

            if (distance < this.getSlopeFindDistance2(worldIn)) {
                double slope = this.getSlopeDistance(worldIn, facingPos, distance + 1,thisQuanta, enumfacing.getOpposite());
                if (slope < difficulty) difficulty = slope;
            }
        }

        return difficulty;
    }

    /**
     * 获得对应方块状态的流体量与自身流体量的差值
     * @param state 对应方块状态
     * @param thisQuanta 自身流体量
     * @return 如果不是一个流体，则返回INT整形最大值。如果是一个流体，则返回自身流体量减去对方流体量的结果。
     */
    private int getQuantaDiffer(IBlockState state,int thisQuanta){
        if(!isSameLiquid(state)) return Integer.MIN_VALUE;
        int quanta = 8-((BlockLiquidAccessor)block).getDepth(state);
        if(quanta<0) quanta = 0;
        return thisQuanta - quanta;
    }

    /**
     * Q>1 坡度流动模式的搜寻距离
     * @param worldIn 所在世界
     */
    private int getSlopeFindDistance2(World worldIn) {
        int ans = SimulationConfig.slopeFindDistanceForWaterWhenQuantaAbove1.getValue();
        if(this.material == Material.LAVA && !worldIn.provider.doesWaterVaporize()){
            ans = SimulationConfig.slopeFindDistanceForLavaWhenQuantaAbove1.getValue();
        }
        return ans;
    }

    @Nonnull
    @Override
    public Block getBlock() {
        return block;
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
        ((BlockDynamicLiquidAccessor)block).tryFlowInto(worldIn,pos,state,level);
    }
    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState){
        ((BlockDynamicLiquidAccessor)block).placeStaticBlock(worldIn,pos,currentState);
        IBlockState newState = worldIn.getBlockState(pos);
        if(newState.getMaterial().isLiquid() && !FluidPressureSearchManager.isTaskRunning(worldIn,pos)){
            FluidPressureSearchManager.addTask(worldIn,new MoreRealityBlockLiquidPressureSearchTask(fluid,newState,pos));
        }
    }
    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
        return ((BlockDynamicLiquidAccessor)block).canFlowInto(worldIn, pos, state);
    }
    private int getSlopeFindDistance(World worldIn) {
        return ((BlockDynamicLiquidAccessor)block).getSlopeFindDistance(worldIn);
    }
    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state){
        return ((BlockDynamicLiquidAccessor)block).isBlocked(worldIn, pos, state);
    }
}
