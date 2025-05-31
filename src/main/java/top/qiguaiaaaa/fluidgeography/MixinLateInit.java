package top.qiguaiaaaa.fluidgeography;

import top.qiguaiaaaa.fluidgeography.util.MixinUtil;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@SuppressWarnings("unused")
public class MixinLateInit implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return MixinUtil.getModMixins();
    }
}
