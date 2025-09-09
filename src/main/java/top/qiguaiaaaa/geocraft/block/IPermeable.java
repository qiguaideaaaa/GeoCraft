package top.qiguaiaaaa.geocraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

/**
 * 可透水(或其他流体)方块
 */
public interface IPermeable {
    Fluid getFluid(World world,BlockPos pos,IBlockState state);
    int getQuanta(World world, BlockPos pos, IBlockState state);

    /**
     * 返回水位高度
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @return 水位高度，分成16份，数字越大越高
     */
    int getHeight(World world,BlockPos pos,IBlockState state);

    int getHeightPerQuanta();

    /**
     * 添加指定片水
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @param quanta 片数，一片是{@link FluidUtil#ONE_IN_EIGHT_OF_BUCKET_VOLUME} mB
     */
    void addQuanta(World world,BlockPos pos,IBlockState state,int quanta);

    void setQuanta(World world,BlockPos pos,IBlockState state,int newQuanta);

    /**
     * 为区块生成时提供的设置量的方法
     */
    IBlockState getQuantaState(IBlockState state, int newQuanta);
    default boolean isFull(World world,BlockPos pos,IBlockState state){
        return getHeight(world,pos,state) == 16;
    }
}
