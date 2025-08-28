package top.qiguaiaaaa.geocraft.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Block.class)
public interface BlockAccessor {
    @Accessor("material")
    Material getMaterial();

    @Accessor("blockState")
    BlockStateContainer getContainer();

    @Invoker("setDefaultState")
    void setDefaultState(IBlockState state);

    @Invoker("getDefaultState")
    IBlockState getDefaultState();
}
