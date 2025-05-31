package top.qiguaiaaaa.fluidgeography.api.configs.value.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigNameWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigBlock extends ConfigNameWrapper {
    private static final Pattern BLOCK_STATE_PATTERN = Pattern.compile("^([^\\[]+)(?:\\[([^]]*)])?$");
    public final String blockRegistryName;
    public final int meta;
    public ConfigBlock(String registryName,int meta) {
        super(getConfigBlockString(registryName,meta));
        this.blockRegistryName = registryName;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Block){
            if(this.meta >-1) return false;
            ResourceLocation location = ((Block) obj).getRegistryName();
            if(location == null) return false;
            return location.toString().equals(blockRegistryName);
        }else if(obj instanceof IBlockState){
            IBlockState state = (IBlockState) obj;
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            ResourceLocation location = block.getRegistryName();
            if(location == null) return false;
            return meta == this.meta && location.toString().equals(blockRegistryName);
        }else if(obj instanceof ConfigBlock){
            ConfigBlock block = (ConfigBlock) obj;
            return block.meta == meta && block.blockRegistryName.equals(blockRegistryName);
        }
        return super.equals(obj);
    }

    @Override
    public ConfigBlock getInstanceByString(String content) {
        if(content == null || content.trim().isEmpty()) return null;
        try {
            Matcher matcher = BLOCK_STATE_PATTERN.matcher(content);
            if (matcher.matches()) {
                String blockId = matcher.group(1);
                String metaStr = matcher.group(2) != null ? matcher.group(2) : "*";
                if("*".equalsIgnoreCase(metaStr.trim())) return new ConfigBlock(blockId,-1);
                int meta = Integer.parseInt(metaStr);
                if(blockId.trim().isEmpty()) return null;
                if(meta < 0) return null;
                return new ConfigBlock(blockId,meta);
            }
        }catch (Throwable t){
            return null;
        }
        return null;
    }

    public static String getConfigBlockString(String blockRegistryName, int meta){
        if(meta == -1) return blockRegistryName+"[*]";
        return blockRegistryName+"["+meta+"]";
    }
}
