package top.qiguaiaaaa.geocraft.api.configs.value.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.api.util.exception.ConfigParseError;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurableBlockState {
    private static final Pattern BLOCK_STATE_PATTERN = Pattern.compile("^([^\\[]+)\\[(.*)]$");
    public final String location;
    public final int meta;
    public final Map<String,ConfigurableBlockProperty> properties;
    public ConfigurableBlockState(ResourceLocation location, int meta) {
        this(location.toString(),meta);
    }
    public ConfigurableBlockState(String registryName, int meta) {
        if(meta <-1) throw new IllegalArgumentException("Meta couldn't be lower than -1! Block: "+registryName);
        this.location = registryName.toLowerCase().trim();
        this.meta = meta;
        this.properties = Collections.emptyMap();
    }
    public ConfigurableBlockState(ResourceLocation location,ConfigurableBlockProperty... properties){
        this(location.toString(),properties);
    }
    public ConfigurableBlockState(String registryName,ConfigurableBlockProperty... properties){
        this.location = registryName.toLowerCase().trim();
        if (properties == null || properties.length == 0) {
            this.properties = Collections.emptyMap();
            this.meta = -1;
            return;
        }
        Map<String, ConfigurableBlockProperty> map = new TreeMap<>();

        for(ConfigurableBlockProperty property:properties){
            if(property == null) continue;
            map.put(property.name, property);
        }
        if(map.isEmpty()){
            this.meta = -1;
            this.properties = Collections.emptyMap();
        }else{
            this.meta = -2;
            this.properties = Collections.unmodifiableMap(map);
        }
    }
    public ConfigurableBlockState(IBlockState state){
        Block block = state.getBlock();
        ResourceLocation resourceLocation = block.getRegistryName();
        Map<String, ConfigurableBlockProperty> map = new TreeMap<>();
        if(resourceLocation == null) throw new NullPointerException();
        location = resourceLocation.toString();
        BlockStateContainer container = block.getBlockState();
        for(IProperty<?> property:container.getProperties()){
            Comparable<?> value = state.getValue(property);
            map.put(property.getName(),new ConfigurableBlockProperty(property,value));
        }
        if(map.isEmpty()){
            this.meta = -1;
            this.properties = Collections.emptyMap();
        }else{
            this.meta = -2;
            this.properties = Collections.unmodifiableMap(map);
        }
    }

    public boolean match(@Nonnull IBlockState state) {
        Block block = state.getBlock();
        ResourceLocation stateLoc = block.getRegistryName();
        if(stateLoc == null) return false;
        if(!stateLoc.toString().equalsIgnoreCase(location)) return false;
        if(meta == -1) return true;
        if(meta >-1) return block.getMetaFromState(state) == meta;

        BlockStateContainer container = block.getBlockState();
        for(IProperty<?> property:container.getProperties()){
            String name = property.getName();
            ConfigurableBlockProperty p = properties.get(name);
            if(p == null) return false;
            if(p.value.isEmpty()) return false;
            String val = state.getValue(property).toString();
            if(!p.value.equalsIgnoreCase(val)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurableBlockState)) return false;
        ConfigurableBlockState that = (ConfigurableBlockState) o;
        return Objects.equals(this.location, that.location)
                && this.meta == that.meta
                && Objects.equals(this.properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, meta, properties);
    }

    public static ConfigurableBlockState getInstanceByString(String content) {
        if (content == null || content.trim().isEmpty()) return null;
        try {
            content = content.trim();

            // 匹配 "命名空间:方块id[xxx]"
            Matcher matcher = BLOCK_STATE_PATTERN.matcher(content);

            if (!matcher.matches()) return null;

            String registryName = matcher.group(1); // 命名空间+ID
            String inside = matcher.group(2);       // 方括号里的内容（可能为 null）

            if (inside == null || inside.isEmpty()) {
                // 没有方括号，默认匹配全部
                return new ConfigurableBlockState(registryName, -1);
            }

            if (inside.equals("*")) {
                return new ConfigurableBlockState(registryName, -1);
            }
            // 数据值模式
            if (inside.matches("-?\\d+")) {
                return new ConfigurableBlockState(registryName, Integer.parseInt(inside));
            }

            // property 模式
            String[] props = inside.split(",");
            ConfigurableBlockProperty[] properties = new ConfigurableBlockProperty[props.length];
            for (int i = 0; i < props.length; i++) {
                String[] kv = props[i].split("=", 2);
                if (kv.length == 2) {
                    properties[i] = new ConfigurableBlockProperty(kv[0].trim(), kv[1].trim());
                } else {
                    throw new ConfigParseError("property "+props[i]+" of "+registryName+" is not valid");
                }
            }
            return new ConfigurableBlockState(registryName, properties);

        } catch (Throwable t) {
            throw new ConfigParseError("Parsing blockstate config error:",t);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(location);
        if(meta == -1){
            builder.append("[*]");
            return builder.toString();
        }
        builder.append('[');
        if(meta >-1){
            builder.append(meta).append(']');
            return builder.toString();
        }
        boolean notFirst = false;
        for(ConfigurableBlockProperty property:properties.values()){
            if(notFirst) builder.append(',');
            builder.append(property.toString());
            notFirst = true;
        }
        builder.append(']');
        return builder.toString();
    }
}
