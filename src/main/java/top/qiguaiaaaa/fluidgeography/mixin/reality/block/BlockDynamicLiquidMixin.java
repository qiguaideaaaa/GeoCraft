package top.qiguaiaaaa.fluidgeography.mixin.reality.block;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig;
import top.qiguaiaaaa.fluidgeography.util.FluidOperationUtil;
import top.qiguaiaaaa.fluidgeography.util.mixinapi.IVanillaFlowChecker;
import top.qiguaiaaaa.fluidgeography.api.util.math.FlowChoice;
import top.qiguaiaaaa.fluidgeography.mixin.common.BlockLiquidAccessor;
import top.qiguaiaaaa.fluidgeography.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;

import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;

@Mixin(value = BlockDynamicLiquid.class)
public class BlockDynamicLiquidMixin implements FluidSettable, IVanillaFlowChecker {
    private Fluid thisFluid;
    private BlockDynamicLiquid thisBlock;
    private Material thisMaterial;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(Material materialIn, CallbackInfo ci) {
        thisBlock = (BlockDynamicLiquid) (Object)this;
        thisMaterial = materialIn;
    }

    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(fluidsNotToSimulate.containsEquivalent(thisFluid)) return;
        ci.cancel();
        if (!worldIn.isAreaLoaded(pos, Math.max(this.getSlopeFindDistance(worldIn),this.getSlopeFindDistance2(worldIn)))){
            return;
        }
        flow(worldIn,pos,state,rand);
    }

    /**
     * 流动
     * @param worldIn 所在世界
     * @param pos 流体位置
     * @param state 流体方块状态
     * @param rand 随机数发生器
     */
    public void flow(World worldIn, BlockPos pos, IBlockState state, Random rand){
        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            worldIn.setBlockToAir(pos);
            return;
        }
        int liquidQuanta = 8-liquidMeta;
        int updateRate = thisBlock.tickRate(worldIn);

        IBlockState stateBelow = worldIn.getBlockState(pos.down());
        boolean canMoveDown = this.canMoveDownTo(worldIn, pos.down(), stateBelow);

        if(canMoveDown){ //向下流动
            if(isSameLiquid(stateBelow) || (stateBelow.getBlock() == Blocks.SNOW_LAYER && thisFluid == FluidRegistry.WATER)){
                flowDown(worldIn,pos,stateBelow,liquidQuanta,updateRate);
            }else if(stateBelow.getMaterial() == Material.WATER){ // 流体融合的情况
                liquidQuanta--;
                liquidMeta = 8-liquidQuanta;
                if (liquidQuanta<=0) worldIn.setBlockToAir(pos); //先更新自身状态
                else {
                    state = state.withProperty(LEVEL,liquidMeta);
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos,thisBlock, updateRate);
                    worldIn.notifyNeighborsOfStateChange(pos,thisBlock, false);
                }
                worldIn.setBlockState(pos.down(), ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos.down(), pos, Blocks.STONE.getDefaultState()));
                FluidOperationUtil.triggerFluidMixEffects(worldIn,pos.down());
            }else{
                FluidOperationUtil.moveFluid(worldIn,pos,pos.down());
            }
            return;
        }

        //Q=1 坡度流动模式
        if(liquidMeta == 7){
            Set<EnumFacing> directions = this.getPossibleFlowDirections(worldIn, pos);
            if(directions.isEmpty()){
                placeStaticBlock(worldIn,pos,state);
                return;
            }

            EnumFacing randomFacing = (EnumFacing) directions.toArray()[rand.nextInt(directions.size())];
            worldIn.setBlockToAir(pos);
            this.tryFlowInto(worldIn, pos.offset(randomFacing), worldIn.getBlockState(pos.offset(randomFacing)), 7);
            return;
        }
        if ((thisMaterial == Material.LAVA) && rand.nextInt(4) != 0){ //岩浆速度处理
            updateRate *= 4;
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        Set<EnumFacing> slopeModeFlowDirections = EnumSet.noneOf(EnumFacing.class);//非Q=1坡度模式可用方向
        this.checkNeighborsToFindFlowChoices(worldIn,pos,liquidQuanta,averageModeFlowDirections,slopeModeFlowDirections);

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
            if (liquidQuanta<=0) worldIn.setBlockToAir(pos); //先更新自身状态
            else {
                state = state.withProperty(LEVEL,liquidMeta);
                worldIn.setBlockState(pos, state, 2);
                worldIn.scheduleUpdate(pos,thisBlock, updateRate);
                worldIn.notifyNeighborsOfStateChange(pos,thisBlock, false);
            }
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                directlyFlowInto(worldIn,facingPos,worldIn.getBlockState(facingPos),8-choice.getQuanta());
            }
        }else if(!slopeModeFlowDirections.isEmpty()){ //非Q=1坡度模式
            slopeModeFlowDirections = getPossibleFlowDirections(worldIn,pos,slopeModeFlowDirections,liquidQuanta);
            if(slopeModeFlowDirections.isEmpty()){
                this.placeStaticBlock(worldIn,pos,state);
                return;
            }
            EnumFacing randomFacing = (EnumFacing) slopeModeFlowDirections.toArray()[rand.nextInt(slopeModeFlowDirections.size())];
            int newLiquidQuanta = liquidQuanta-1;
            int newLiquidMeta = 8-newLiquidQuanta;
            //更新自己
            state = state.withProperty(LEVEL,newLiquidMeta);
            worldIn.setBlockState(pos,state,2);
            worldIn.scheduleUpdate(pos,thisBlock, updateRate);
            worldIn.notifyNeighborsOfStateChange(pos,thisBlock,false);
            //移动至新位置
            setLiquidToFlowingLevel(worldIn,pos.offset(randomFacing),liquidMeta);
        } else{
            this.placeStaticBlock(worldIn,pos,state);
        }
    }
    @Override
    public boolean canFlow(World worldIn,BlockPos pos,IBlockState state,Random rand){
        if (!worldIn.isAreaLoaded(pos, Math.max(this.getSlopeFindDistance(worldIn),this.getSlopeFindDistance2(worldIn)))){
            return false;
        }

        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            return false;
        }
        int liquidQuanta = 8-liquidMeta;

        IBlockState stateBelow = worldIn.getBlockState(pos.down());
        boolean canMoveDown = this.canMoveDownTo(worldIn, pos.down(), stateBelow);
        if(canMoveDown) return true;

        //坡度流动模式
        if(liquidMeta == 7){
            Set<EnumFacing> directions = this.getPossibleFlowDirections(worldIn, pos);
            return !directions.isEmpty();
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        Set<EnumFacing> slopeModeFlowDirections = EnumSet.noneOf(EnumFacing.class);//非Q=1坡度模式可用方向
        this.checkNeighborsToFindFlowChoices(worldIn,pos,liquidQuanta,averageModeFlowDirections,slopeModeFlowDirections);

        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            return true;
        }else if(!slopeModeFlowDirections.isEmpty()){ //Q>1坡度模式
            slopeModeFlowDirections = getPossibleFlowDirections(worldIn,pos,slopeModeFlowDirections,liquidQuanta);
            return !slopeModeFlowDirections.isEmpty();
        }
        return false;
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
            int facingMeta = ((BlockLiquidAccessor)this).getDepth(facingState);
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
     * @param worldIn 所在世界
     * @param currentPos 当前位置
     * @param downState 下方方块状态
     * @param liquidQuanta 当前流体量
     * @param tickRate 更新间隔
     */
    private void flowDown(World worldIn,BlockPos currentPos,IBlockState downState,int liquidQuanta,int tickRate){
        if(downState.getBlock() == Blocks.SNOW_LAYER && thisFluid == FluidRegistry.WATER){
            int belowLayer = downState.getValue(BlockSnow.LAYERS);
            int totalQuanta = belowLayer+liquidQuanta;
            int frozenQuanta = Math.min(8-belowLayer,liquidQuanta);
            if(totalQuanta<=8){
                worldIn.setBlockToAir(currentPos);
                worldIn.setBlockState(currentPos.down(),downState.withProperty(BlockSnow.LAYERS,totalQuanta));
            }else{
                int remain = totalQuanta-8;
                setLiquidToFlowingLevel(worldIn,currentPos,8-remain);
                worldIn.scheduleUpdate(currentPos,thisBlock,tickRate);
                worldIn.setBlockState(currentPos.down(),downState.withProperty(BlockSnow.LAYERS,8));
            }
            Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(worldIn,currentPos);
            if(atmosphere == null) return;
            atmosphere.add低层大气热量(frozenQuanta* AtmosphereUtil.FinalFactors.WATER_MELT_LATENT_HEAT_PER_QUANTA);
            return;
        }
        int belowQuanta = FluidUtil.getFluidQuanta(worldIn,currentPos.down(),downState);
        int totalQuanta = liquidQuanta+belowQuanta;
        if(totalQuanta<=8){
            worldIn.setBlockToAir(currentPos);
            setLiquidToFlowingLevel(worldIn, currentPos.down(),8-totalQuanta);
        }else{
            int remain = totalQuanta-8;
            setLiquidToFlowingLevel(worldIn,currentPos,8-remain);
            worldIn.scheduleUpdate(currentPos,thisBlock,tickRate);
            setLiquidToFlowingLevel(worldIn,currentPos.down(),0);
        }
    }

    /**
     * 将指定位置的方块设置为指定等级的流动型流体
     * @param worldIn 所在世界
     * @param pos 位置
     * @param newLevel 新等级
     */
    private void setLiquidToFlowingLevel(World worldIn,BlockPos pos,int newLevel){
        worldIn.setBlockState(pos, thisBlock.getDefaultState().withProperty(LEVEL,newLevel), Constants.BlockFlags.DEFAULT);
    }

    /**
     * 不检查是否能够流入，直接流入对应方块
     * @param worldIn 所在世界
     * @param pos 流入位置
     * @param state 当前方块状态
     * @param level 流入流体等级
     */
    private void directlyFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
        FluidOperationUtil.triggerDestroyBlockEffectByFluid(worldIn,pos,state,thisFluid);
        worldIn.setBlockState(pos, thisBlock.getDefaultState().withProperty(LEVEL, level), Constants.BlockFlags.DEFAULT);
    }

    /**
     * 是否是相同液体
     */
    private boolean isSameLiquid(IBlockState state){
        Block block = state.getBlock();
        if(block instanceof IFluidBlock) return false;
        return state.getMaterial() == thisMaterial;
    }

    /**
     * 液体是否可以往下流动
     * @param worldIn 所在世界
     * @param pos 当前位置
     * @param state 当前方块状态
     * @return 如果可以往下流动，则返回true
     */
    private boolean canMoveDownTo(World worldIn, BlockPos pos, IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material == Material.WATER && thisMaterial == Material.LAVA) return true;
            if(material != thisMaterial) return false;
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
        if(thisFluid != FluidRegistry.WATER) return true;
        if(state.getBlock() != Blocks.SNOW_LAYER) return true;
        int layer = state.getValue(BlockSnow.LAYERS);
        return layer < thisQuanta-2;
    }
    @Inject(method = "getPossibleFlowDirections",at = @At("HEAD"),cancellable = true)
    private void getPossibleFlowDirections(World worldIn, BlockPos pos, CallbackInfoReturnable<Set<EnumFacing>> cir) {
        if(fluidsNotToSimulate.containsEquivalent(thisFluid)) return;
        cir.cancel();
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
        cir.setReturnValue(possibleDirections);
    }
    @Inject(method = "getSlopeDistance",at = @At("HEAD"),cancellable = true)
    private void getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from, CallbackInfoReturnable<Integer> cir) {
        if(fluidsNotToSimulate.containsEquivalent(thisFluid)) return;
        cir.cancel();
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
                cir.setReturnValue(distance);
                return;
            }

            if (distance < this.getSlopeFindDistance(worldIn)) {
                int newDistance = this.getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite());
                if (newDistance < difficulty) difficulty = newDistance;
            }
        }

        cir.setReturnValue(difficulty);
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
        int quanta = 8-((BlockLiquidAccessor)this).getDepth(state);
        if(quanta<0) quanta = 0;
        return thisQuanta - quanta;
    }

    /**
     * Q>1 坡度流动模式的搜寻距离
     * @param worldIn 所在世界
     */
    private int getSlopeFindDistance2(World worldIn) {
        int ans = SimulationConfig.slopeFindDistanceForWaterWhenQuantaAbove1.getValue().value;
        if(thisMaterial == Material.LAVA && !worldIn.provider.doesWaterVaporize()){
            ans = SimulationConfig.slopeFindDistanceForLavaWhenQuantaAbove1.getValue().value;
        }
        return ans;
    }
    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        this.thisFluid = fluid;
    }
    @Shadow
    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {}
    @Shadow
    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState){}
    @Shadow
    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {return false;}
    /**
     * Q=1 坡度流动模式的可流动方向寻找算法
     */
    @Shadow
    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {return EnumSet.noneOf(EnumFacing.class);}
    /**
     * Q=1 坡度流动模式的可流动方向寻找内层递归算法
     */
    @Shadow
    private int getSlopeDistance(World worldIn,BlockPos pos,int distance,EnumFacing from){return 0;}
    @Shadow
    private int getSlopeFindDistance(World worldIn) {
        return 0;
    }
    @Shadow
    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state){return false;}
}
