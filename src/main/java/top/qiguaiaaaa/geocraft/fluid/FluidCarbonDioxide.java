package top.qiguaiaaaa.geocraft.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.GeoCraft;

public class FluidCarbonDioxide extends Fluid {
    public static final ResourceLocation still = new ResourceLocation(GeoCraft.MODID,"fluids/carbon_dioxide_still");
    public static final ResourceLocation flowing = new ResourceLocation(GeoCraft.MODID, "fluids/carbon_dioxide_flow");
    public static final String fluidName = "carbon_dioxide";

    public FluidCarbonDioxide() {
        super(fluidName, still, flowing);
        setUnlocalizedName("geocraft.fluid.carbon_dioxide");
        setDensity(44);
    }

}
