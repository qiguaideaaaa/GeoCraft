package top.qiguaiaaaa.geocraft.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(value = Block.class)
public interface BlockAccessor {
    @Accessor("material")
    Material getMaterial();

    @Invoker("getDefaultState")
    IBlockState getDefaultState();

    @Invoker("updateTick")
    void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random);
}
