package top.qiguaiaaaa.geocraft.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.AverageAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.atmosphere.layer.surface.Underlying;

import javax.annotation.Nonnull;

public class SurfaceAtmosphereAccessor extends AverageAtmosphereAccessor {
    public SurfaceAtmosphereAccessor(@Nonnull IAtmosphereSystem system, @Nonnull AtmosphereData data, @Nonnull BlockPos pos, boolean notAir) {
        super(system, data, pos, notAir);
    }

    @Override
    public double getTemperature() {
        if(notAir && skyLight>0 && skyLight<=15){
            return getAtmosphereValue((dir, atmosphere) -> {
                UnderlyingLayer underlying = atmosphere.getUnderlying();
                Altitude altitude = underlying.getAltitude();
                if(pos.getY()>= altitude.get()) return (double) atmosphere.getTemperature(mutableBlockPos.setPos(pos.getX() + dir[0], pos.getY(), pos.getZ() + dir[1]), notAir);
                double 高差 = Altitude.to物理高度(altitude.get()-pos.getY());//>0
                double surfaceTemp = underlying.getTemperature().get()+高差* AtmosphereUtil.FinalFactors.对流层温度直减率;
                TemperatureState deepTempState = underlying.getTemperature(GeoCraftProperties.DEEP_TEMPERATURE);
                if(deepTempState == null) throw new RuntimeException("Atmosphere Underlying Type not suited! Try another accessor!");
                double deepTemp = deepTempState.get()+Altitude.to物理高度(Math.max(altitude.get()- Underlying.过渡距离 -pos.getY(),0))* AtmosphereUtil.FinalFactors.地下温度直增率;
                return (skyLight/15d)*(surfaceTemp-deepTemp)+deepTemp;
            });
        }else{
            return super.getTemperature();
        }

    }

    @Override
    public void putHeatToUnderlying(double amount) {
        if(amount <0) return;
        if(skyLight<0 || skyLight>15){
            super.putHeatToUnderlying(amount);
            return;
        }
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average = amount/datas.size();
        forAtmospheresDo((dir, atmosphere) -> {
            UnderlyingLayer underlying = atmosphere.getUnderlying();
            if(pos.getY()>underlying.getAltitude().get()){
                atmosphere.getUnderlying().putHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]));
                return null;
            }
            double modifiedY = skyLight==0?pos.getY():skyLight/15d*Underlying.过渡距离+underlying.getAltitude().get()-Underlying.过渡距离;
            atmosphere.getUnderlying().putHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],modifiedY,pos.getZ()+dir[1]));
            return null;
        });
    }

    @Override
    public double drawHeatFromUnderlying(double amount) {
        if(amount <0) return 0;
        if(skyLight<0 || skyLight>15){
            return super.drawHeatFromUnderlying(amount);
        }
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average = amount/datas.size();
        return drawAtmosphereProperty((dir,atmosphere)->{
            UnderlyingLayer underlying = atmosphere.getUnderlying();
            if(pos.getY()>underlying.getAltitude().get()){
                return atmosphere.getUnderlying().drawHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]));
            }
            double modifiedY = skyLight==0?pos.getY():skyLight/15d*Underlying.过渡距离+underlying.getAltitude().get()-Underlying.过渡距离;
            return atmosphere.getUnderlying().drawHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],modifiedY,pos.getZ()+dir[1]));
        });
    }
}
