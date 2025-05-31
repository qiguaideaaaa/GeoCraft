package top.qiguaiaaaa.fluidgeography.util;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import top.qiguaiaaaa.fluidgeography.FluidGeography;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.mixin.common.BlockFluidBaseAccessor;

import java.util.*;

public final class FluidSearchUtil {
    private static final Set<BlockPos> EMPTY_BLOCKPOS_SET = new HashSet<>();

    /**
     * 广度优先搜索搜寻原版流体源
     * @param world 世界
     * @param startPos 起始位置
     * @param material 流体类型
     * @param ignoreSameY 是否不考虑同层液体
     * @param ignoreLevel 是否忽略流体等级进行搜索
     * @param maxIterations 最大迭代次数
     * @param sameLevelIterationLimit 最大同等级搜索次数
     * @return 一个可能的流体源
     */
    public static Optional<BlockPos> findSource(World world, BlockPos startPos, Material material, boolean ignoreSameY, boolean ignoreLevel, int maxIterations, int sameLevelIterationLimit){
        return findSourceIterate(world,startPos,material,ignoreSameY,ignoreLevel,maxIterations,sameLevelIterationLimit);
    }

    /**
     * 广度优先搜索流体源（支持MOD）
     * 流体方块需要是
     * @param world 世界
     * @param startPos 起始位置
     * @param fluid 流体类型
     * @param ignoreSameY 是否不考虑同层流体
     * @param ignoreLevel 是否忽略流体等级进行搜索
     * @param maxIterations 最大迭代次数
     * @param sameQuantaIterationLimit 最大同量搜索次数
     * @return 一个可能的流体源
     */
    public static Optional<BlockPos> findSource(World world, BlockPos startPos, Fluid fluid, boolean ignoreSameY, boolean ignoreLevel, int maxIterations, int sameQuantaIterationLimit){
        if(fluid == FluidRegistry.WATER){
            findSource(world,startPos,Material.WATER,ignoreSameY,ignoreLevel,maxIterations,sameQuantaIterationLimit);
        }else if(fluid == FluidRegistry.LAVA){
            findSource(world,startPos,Material.LAVA,ignoreSameY,ignoreLevel,maxIterations,sameQuantaIterationLimit);
        }
        try{
            return findSourceIterate(world,startPos,fluid,ignoreSameY,ignoreLevel,
                    (fluid.getDensity()>0)?-1:1,
                    ((BlockFluidBaseAccessor)(fluid.getBlock())).getQuantaPerBlock(),
                    maxIterations,sameQuantaIterationLimit);
        }catch (Throwable e){
            FluidGeography.getLogger().error(e);
        }
        return Optional.empty();
    }

    /**
     * 广度优先搜索的实现 for 原版流体
     * @param world 世界
     * @param startPos 起始位置
     * @param material 流体类型
     * @param ignoreSameY 是否不考虑同层液体
     * @param ignoreLevel 是否忽略流体等级进行搜索
     * @param maxIterations 最大迭代次数
     * @param sameLevelIterationLimit 最大同等级搜索次数
     * @return 一个可能的流体源
     */
    private static Optional<BlockPos> findSourceIterate(World world,
                                                    BlockPos startPos,
                                                    Material material,
                                                    boolean ignoreSameY,
                                                    boolean ignoreLevel,
                                                    int maxIterations,
                                                    int sameLevelIterationLimit) {

        Queue<FluidSourceSearchNode> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(new FluidSourceSearchNode(startPos, EnumFacing.UP, 0, 0));
        visited.add(startPos);

        while (!queue.isEmpty()) {
            FluidSourceSearchNode current = queue.poll();

            if (current.iteration > maxIterations) continue;
            IBlockState state = world.getBlockState(current.pos);
            if (state.getMaterial() != material || state.getBlock() instanceof IFluidBlock)
                continue;
            // 检查是否为源方块
            int level = FluidUtil.getFluidQuanta(world, current.pos, state);
            if (!(ignoreSameY && ( current.pos.getY() == startPos.getY()) ) && level == 8) return Optional.of(current.pos);

            if(!(ignoreSameY  && current.pos.getY()>startPos.getY())){
                // 向上搜索
                BlockPos upPos = current.pos.up();
                if (!visited.contains(upPos)) {
                    visited.add(upPos);
                    if(!world.isBlockLoaded(upPos)) continue;
                    queue.add(new FluidSourceSearchNode(
                            upPos,
                            EnumFacing.UP,
                            current.iteration + 1,
                            0
                    ));
                }
            }


            // 水平方向搜索
            EnumFacing opposite = current.direction.getOpposite();
            boolean falling = state.getValue(BlockLiquid.LEVEL) >= 8;

            for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                if (dir == opposite) continue;

                BlockPos nextPos = current.pos.offset(dir);
                if (visited.contains(nextPos)) continue;

                IBlockState nextState = world.getBlockState(nextPos);
                if (nextState.getMaterial() != material || nextState.getBlock() instanceof IFluidBlock) continue;

                int nextLevel = FluidUtil.getFluidQuanta(world, nextPos, nextState);
                boolean nextFalling = nextState.getValue(BlockLiquid.LEVEL) >= 8;

                if (nextLevel >= level || (falling && !nextFalling) || ignoreLevel) {
                    int newSameLevelIter = (nextLevel == level) ?
                            current.sameLevelIteration + 1 : 0;
                    if(ignoreLevel) newSameLevelIter = 0;
                    if (newSameLevelIter > sameLevelIterationLimit) continue;

                    visited.add(nextPos);
                    if(!world.isBlockLoaded(nextPos)) continue;
                    queue.add(new FluidSourceSearchNode(
                            nextPos,
                            dir,
                            current.iteration + 1,
                            newSameLevelIter
                    ));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 广度优先搜索实现的 for Mod流体
     * @param world 世界
     * @param startPos 起始位置
     * @param ignoreSameY 是否不考虑同层流体
     * @param ignoreLevel 是否忽略流体等级进行搜索
     * @param maxIterations 最大迭代次数
     * @param sameQuantaIterationLimit 最大同量搜索次数
     * @param fluid 流体类型
     * @param densityDir 流体流动方向。向上为1，向下为-1。
     * @param quantaPerBlock 每个流体方块最大容量
     * @return 一个可能的流体源
     */
    private static Optional<BlockPos> findSourceIterate(World world,
                                                        BlockPos startPos,
                                                        Fluid fluid,
                                                        boolean ignoreSameY,
                                                        boolean ignoreLevel,
                                                        int densityDir,
                                                        int quantaPerBlock,
                                                        int maxIterations,
                                                        int sameQuantaIterationLimit) {

        Queue<FluidSourceSearchNode> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(new FluidSourceSearchNode(startPos, EnumFacing.UP, 0, 0));
        visited.add(startPos);

        while (!queue.isEmpty()) {
            FluidSourceSearchNode current = queue.poll();
            if (current.iteration > maxIterations) continue;
            IBlockState state = world.getBlockState(current.pos);
            Fluid currentFluid = FluidUtil.getFluid(state);
            if (currentFluid != fluid) continue;
            // 检查是否为源方块
            int quanta = FluidUtil.getFluidQuanta(world, current.pos, state);
            if (!(ignoreSameY && ( current.pos.getY() == startPos.getY()) ) && quanta == quantaPerBlock) return Optional.of(current.pos);

            if(!(ignoreSameY && current.pos.getY()>startPos.getY())){
                BlockPos upPos = current.pos.down(densityDir);
                if (!visited.contains(upPos)) {
                    visited.add(upPos);
                    queue.add(new FluidSourceSearchNode(
                            upPos,
                            EnumFacing.UP,
                            current.iteration + 1,
                            0
                    ));
                }
            }
            // 向上搜索


            // 水平方向搜索
            EnumFacing opposite = current.direction.getOpposite();
            boolean falling = (world.getBlockState(current.pos.down(densityDir)).getBlock() == state.getBlock());

            for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                if (dir == opposite) continue;

                BlockPos nextPos = current.pos.offset(dir);
                if (visited.contains(nextPos)) continue;

                IBlockState nextState = world.getBlockState(nextPos);
                if (FluidUtil.getFluid(nextState) != fluid) continue;

                int nextQuanta = FluidUtil.getFluidQuanta(world, nextPos, nextState);
                boolean nextFalling = (world.getBlockState(nextPos.down(densityDir)).getBlock() == nextState.getBlock());

                if (nextQuanta >= quanta || (falling && !nextFalling) || ignoreLevel) {
                    int newSameQuantaIter = (nextQuanta == quanta) ?
                            current.sameLevelIteration + 1 : 0;
                    if(ignoreLevel) newSameQuantaIter = 0;
                    if (newSameQuantaIter > sameQuantaIterationLimit) continue;

                    visited.add(nextPos);
                    queue.add(new FluidSourceSearchNode(
                            nextPos,
                            dir,
                            current.iteration + 1,
                            newSameQuantaIter
                    ));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 寻找流体方块（广度优先）
     * @param world 所在世界
     * @param startPos 起始位置位置
     * @param searchInFlat 仅在水平面上搜索
     * @param upwardPriority 优先往上搜索
     * @param maxIterations 最大迭代次数
     * @return 一个流体方块，不一定是流体源
     */
    public static Optional<BlockPos> findFluid(World world,BlockPos startPos,boolean searchInFlat,boolean upwardPriority,int maxIterations){
        return findFluidIterate(world,startPos,null,null,searchInFlat,upwardPriority,maxIterations);
    }

    /**
     * 寻找流体方块（广度优先）
     * @param world 所在世界
     * @param startPos 起始位置位置
     * @param fluid 指定流体，若为null则不指定
     * @param ignoreBlocks 忽略的方块列表，null即为不指定忽略的方块
     * @param searchInFlat 仅在水平面上搜索
     * @param upwardPriority 优先往上搜索
     * @param maxIterations 最大迭代次数
     * @return 一个流体方块，不一定是流体源
     */
    public static Optional<BlockPos> findFluid(World world,BlockPos startPos,Fluid fluid,Set<BlockPos> ignoreBlocks,boolean searchInFlat,boolean upwardPriority,int maxIterations){
        return findFluidIterate(world,startPos,fluid,ignoreBlocks,searchInFlat,upwardPriority,maxIterations);
    }

    private static Optional<BlockPos> findFluidIterate(World world, BlockPos startPos, Fluid fluid, Set<BlockPos> ignoreBlocks, boolean searchInFlat, boolean upwardPriority, int maxIterations){
        Queue<FluidSearchNode> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        if(ignoreBlocks == null) ignoreBlocks = EMPTY_BLOCKPOS_SET;

        queue.add(new FluidSearchNode(startPos, EnumFacing.UP, 0));
        visited.add(startPos);

        while (!queue.isEmpty()) {
            FluidSearchNode current = queue.poll();
            if (current.iteration > maxIterations) continue;
            IBlockState state = world.getBlockState(current.pos);
            if(FluidUtil.isFluid(state) && (fluid == null || FluidUtil.getFluid(state) == fluid) && !ignoreBlocks.contains(current.pos)){
                if(!upwardPriority){
                    return Optional.of(current.pos);
                }
                queue.clear(); //下面一定不会再搜到了
                visited.clear();
                BlockPos upPos = current.pos.up();
                IBlockState upState = world.getBlockState(upPos);
                if(!FluidUtil.isFluid(upState)) return Optional.of(current.pos);
                visited.add(upPos); //不可能已经搜索过
                queue.add(new FluidSearchNode(
                        upPos,
                        EnumFacing.UP,
                        current.iteration // 不论怎样都已经找到了，不需要再加迭代次数
                ));
                continue;
            }

            // 水平方向搜索
            EnumFacing opposite = current.direction.getOpposite();

            for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                if (dir == opposite) continue;
                BlockPos nextPos = current.pos.offset(dir);
                if (visited.contains(nextPos)) continue;
                visited.add(nextPos);
                IBlockState nextState = world.getBlockState(nextPos);
                if(!ignoreBlocks.contains(nextPos) && FluidUtil.isFluid(nextState) && (fluid == null || FluidUtil.getFluid(nextState) == fluid)) return Optional.of(nextPos);
                else if(nextState.getBlock().isPassable(world,nextPos) || nextState.getBlock().isReplaceable(world,nextPos) || FluidUtil.isFluid(nextState)) { //需要方块不能够阻挡
                    queue.add(new FluidSearchNode(
                            nextPos,
                            dir,
                            current.iteration + 1
                    ));
                }
            }
            //向上搜索
            if(!searchInFlat && !upwardPriority){
                BlockPos upPos = current.pos.up();
                visited.add(upPos);
                queue.add(new FluidSearchNode(
                        upPos,
                        EnumFacing.UP,
                        current.iteration + 1
                ));
            }
        }

        return Optional.empty();
    }

    private static class FluidSearchNode{
        final BlockPos pos;
        final EnumFacing direction;
        final int iteration;
        public FluidSearchNode(BlockPos pos, EnumFacing direction, int iteration) {
            this.pos = pos;
            this.direction = direction;
            this.iteration = iteration;
        }
    }

    private static class FluidSourceSearchNode extends FluidSearchNode{
        final int sameLevelIteration;

        public FluidSourceSearchNode(BlockPos pos, EnumFacing direction, int iteration, int sameLevelIteration) {
            super(pos,direction,iteration);
            this.sameLevelIteration = sameLevelIteration;
        }
    }
}
