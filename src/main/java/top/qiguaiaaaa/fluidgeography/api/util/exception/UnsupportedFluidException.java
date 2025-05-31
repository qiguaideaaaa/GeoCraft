package top.qiguaiaaaa.fluidgeography.api.util.exception;

import net.minecraft.block.Block;

public class UnsupportedFluidException extends IllegalArgumentException{
    public UnsupportedFluidException(Block block) {
        super("Unsupported Liquid! "+block.getRegistryName());
    }
}
