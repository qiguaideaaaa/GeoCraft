package top.qiguaiaaaa.geocraft.mixin.atmosphere.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.MapColor;
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

import static net.minecraft.block.BlockDirt.SNOWY;
import static net.minecraft.block.BlockDirt.VARIANT;
import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockDirt.class)
public class BlockDirtMixin extends Block implements IBlockDirt {
    public BlockDirtMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        this.setTickRandomly(true);
        this.setDefaultState((this.blockState.getBaseState().
                withProperty(VARIANT, BlockDirt.DirtType.DIRT)
                .withProperty(SNOWY, Boolean.FALSE)
                .withProperty(HUMIDITY, 0)));
    }
    @Inject(method = "getStateFromMeta",at = @At(value = "HEAD"),cancellable = true)
    private void getStateFromMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        cir.cancel();
        cir.setReturnValue(this.getDefaultState()
                .withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta%3))
                .withProperty(HUMIDITY,Math.min(meta/3,4)));
    }
    @Inject(method = "getMetaFromState",at = @At(value = "HEAD"),cancellable = true)
    public void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(VARIANT).getMetadata()+state.getValue(HUMIDITY)*3);
    }

    @Inject(method = "createBlockState",at = @At(value = "HEAD"),cancellable = true)
    private void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, VARIANT, SNOWY, HUMIDITY));
    }

    /**
     * {@link Block#randomTick(World, BlockPos, IBlockState, Random)}
     */
    public void func_180645_a(World worldIn, BlockPos pos, IBlockState state, Random random) {
        this.updateTick(worldIn, pos, state, random);
        this.onRandomTick(worldIn, pos, state, random);
    }

    /**
     * {@link Block#onPlayerDestroy(World, BlockPos, IBlockState)}
     */
    public void func_176206_d(World worldIn, BlockPos pos, IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    /**
     * {@link Block#breakBlock(World, BlockPos, IBlockState)}
     */
    public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
        if (this.hasTileEntity(state) && !(Blocks.DIRT instanceof BlockContainer)) {
            worldIn.removeTileEntity(pos);
        }
        dropWaterWhenBroken(worldIn, pos, state);
    }
}
