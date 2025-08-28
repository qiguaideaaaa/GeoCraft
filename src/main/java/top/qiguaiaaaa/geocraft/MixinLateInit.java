package top.qiguaiaaaa.geocraft;

import top.qiguaiaaaa.geocraft.util.MixinUtil;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@SuppressWarnings("unused")
public class MixinLateInit implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return MixinUtil.getModMixins();
    }
}
