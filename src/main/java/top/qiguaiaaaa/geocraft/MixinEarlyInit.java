package top.qiguaiaaaa.geocraft;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.api.simulation.SimulationMode;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(GeoCraft.MODID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class MixinEarlyInit implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final boolean isClient = FMLLaunchHandler.side() == Side.CLIENT;

    @Override
    public List<String> getMixinConfigs() {
        CommonProxy.earlyInit();
        List<String> mixinList =  Lists.newArrayList("mixins.geocraft.json");
        SimulationMode mode = SimulationConfig.SIMULATION_MODE.getValue();
        switch (mode){
            case MORE_REALITY:{
                mixinList.add("mixins.geocraft_reality.json");
                break;
            }
            case VANILLA:{
                break;
            }
            case VANILLA_LIKE:
            default:{
                mixinList.add("mixins.geocraft_vanilla_like.json");
                break;
            }
        }
        if(mode != SimulationMode.VANILLA) mixinList.add("mixins.geocraft_atmosphere.json");
        System.out.println("GeoCraft's Fluid Physics is using mode "+mode);

        return mixinList;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}