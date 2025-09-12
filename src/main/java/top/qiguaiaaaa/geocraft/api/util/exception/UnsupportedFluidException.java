package top.qiguaiaaaa.geocraft.api.util.exception;

import net.minecraft.block.Block;

import javax.annotation.Nonnull;

/**
 * 当尝试对不受该模组支持的流体进行操作的时候抛出
 * @author QiguaiAAAA
 */
public class UnsupportedFluidException extends IllegalArgumentException{
    public UnsupportedFluidException(@Nonnull Block block) {
        super("Unsupported Liquid! "+block.getRegistryName());
    }
}
