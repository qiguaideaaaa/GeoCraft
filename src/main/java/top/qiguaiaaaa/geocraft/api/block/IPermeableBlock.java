package top.qiguaiaaaa.geocraft.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import javax.annotation.Nonnull;

/**
 * 可透水(或其他流体)方块
 * 注意这和含水方块有本质区别，含水方块是透水方块的子集。例如，泥土不是含水方块，但应该为透水方块。
 * @author QiguaiAAAA
 */
public interface IPermeableBlock {
    int MAX_HEIGHT = 16;
    @Nonnull
    Fluid getFluid(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);
    int getQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);

    /**
     * 返回水位高度
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @return 水位高度，分成16份，数字越大越高
     */
    int getHeight(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);

    int getHeightPerQuanta();

    /**
     * 添加指定片水
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @param quanta 片数，一片是{@link FluidUtil#ONE_IN_EIGHT_OF_BUCKET_VOLUME} mB
     */
    void addQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,int quanta);

    void setQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,int newQuanta);

    /**
     * 为区块生成时提供的设置量的方法
     */
    @Nonnull
    IBlockState getQuantaState(@Nonnull IBlockState state, int newQuanta);
    default boolean isFull(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state){
        return getHeight(world,pos,state) == MAX_HEIGHT;
    }
}
