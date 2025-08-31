package top.qiguaiaaaa.geocraft.mixin.atmosphere.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.block.IBlockDirt;

import java.util.Random;

import static net.minecraft.block.BlockGrass.SNOWY;
import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockGrass.class)
public class BlockGrassMixin extends Block implements IBlockDirt{
    public BlockGrassMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>",at =@At("RETURN"))
    protected void BlockGrass(CallbackInfo ci) {
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(SNOWY, Boolean.FALSE)
                .withProperty(HUMIDITY, 0));
    }

    /**
     * {@link Block#getStateFromMeta(int)}
     */
    public IBlockState func_176203_a(int meta) {
        if(meta>4) return this.getDefaultState();
        return this.getDefaultState().withProperty(HUMIDITY,meta);
    }

    @Inject(method = "getMetaFromState",at = @At("HEAD"), cancellable = true)
    protected void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(HUMIDITY));
    }

    @Inject(method = "createBlockState",at = @At("HEAD"), cancellable = true)
    protected void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, SNOWY, HUMIDITY));
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        super.randomTick(worldIn, pos, state, random);
        this.onRandomTick(worldIn, pos, state, random);
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Inject(method = "updateTick",at =@At("HEAD"))
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if (worldIn.isRemote) return;
        if (!worldIn.isAreaLoaded(pos, 3)) return;
        if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(HUMIDITY,state.getValue(HUMIDITY)));
        } else {
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
                for (int i = 0; i < 4; i++) {
                    BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

                    if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos))
                        return;

                    IBlockState upState = worldIn.getBlockState(blockpos.up());
                    IBlockState currentState = worldIn.getBlockState(blockpos);

                    if (currentState.getBlock() == Blocks.DIRT
                            && currentState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT
                            && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && upState.getLightOpacity(worldIn, pos.up()) <= 2) {
                        worldIn.setBlockState(blockpos, Blocks.GRASS.getDefaultState().withProperty(HUMIDITY,currentState.getValue(HUMIDITY)));
                    }
                }
            }
        }
    }
}
