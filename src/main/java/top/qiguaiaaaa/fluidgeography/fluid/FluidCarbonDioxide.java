package top.qiguaiaaaa.fluidgeography.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.fluidgeography.FluidGeography;

public class FluidCarbonDioxide extends Fluid {
    public static final ResourceLocation still = new ResourceLocation(FluidGeography.MODID,"fluids/carbon_dioxide_still");
    public static final ResourceLocation flowing = new ResourceLocation(FluidGeography.MODID, "fluids/carbon_dioxide_flow");
    public static final String fluidName = "carbon_dioxide";

    public FluidCarbonDioxide() {
        super(fluidName, still, flowing);
        setUnlocalizedName("fluidphysics.fluid.carbon_dioxide");
        setDensity(44);
    }


}
