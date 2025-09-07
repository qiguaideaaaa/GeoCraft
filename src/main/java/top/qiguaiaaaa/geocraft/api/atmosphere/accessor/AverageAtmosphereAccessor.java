package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class AverageAtmosphereAccessor extends AbstractAtmosphereAccessor{
    protected static final int[] CURRENT = {0,0};
    protected static final int[][] DIRS8 = {
            {2,0},{-2, 0},{0, 2},{ 0,-2},
            {2,2},{-2,-2},{2,-2},{-2, 2}};

    protected Map<int[],AtmosphereData> datas = new HashMap<>();

    protected BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

    public AverageAtmosphereAccessor(@Nonnull IAtmosphereSystem system,@Nonnull AtmosphereData data,@Nonnull BlockPos pos, boolean notAir) {
        super(system, data, pos, notAir);
        loadDatas();
    }

    @Override
    public boolean refresh() {
        datas.clear();
        loadDatas();
        return true;
    }

    @Override
    public double getTemperature() {
        if(skyLight<0 || notAir || skyLight >=15){
            return getAtmosphereValue((ints, atmosphere) ->
                    (double) atmosphere.getTemperature(mutableBlockPos.setPos(pos.getX() + ints[0], pos.getY(), pos.getZ() + ints[1]), notAir));
        }
        double airTemp= getAtmosphereValue((ints, atmosphere) ->
                (double) atmosphere.getTemperature(mutableBlockPos.setPos(pos.getX() + ints[0], pos.getY(), pos.getZ() + ints[1]), false)),
                blockTemp = getAtmosphereValue((ints, atmosphere) ->
                (double) atmosphere.getTemperature(mutableBlockPos.setPos(pos.getX() + ints[0], pos.getY(), pos.getZ() + ints[1]), true));
        return (skyLight/15.0)*(airTemp-blockTemp)+blockTemp;
    }

    @Override
    public double getPressure() {
        return getAtmosphereValue((ints, atmosphere) ->
                atmosphere.getPressure(mutableBlockPos.setPos(pos.getX() + ints[0], pos.getY(), pos.getZ() + ints[1])));
    }

    @Override
    public double getWaterPressure() {
        checkAtmosphereDataLoaded();
        return getAtmosphereValue((dir, atmosphere) ->
                atmosphere.getWaterPressure(mutableBlockPos.setPos(pos.getX() + dir[0], pos.getY(), pos.getZ() + dir[1])));
    }

    @Override
    public Vec3d getWind() {
        checkAtmosphereDataLoaded();
        Vec3d wind = data.getAtmosphere().getWind(pos);
        if(skyLight>=0 && skyLight<15){
            wind.scale(skyLight/15d);
        }
        return wind;
    }

    @Override
    public void putHeatToAtmosphere(double amount) {
        if(amount <0) return;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average;

        if(skyLight>=0 && skyLight<15){
            average = amount/datas.size()*(skyLight/15d);
        }else average = amount/datas.size();

        forAtmospheresDo((dir, atmosphere) -> {
            atmosphere.putHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]));
            return null;
        });
    }

    @Override
    public void putHeatToUnderlying(double amount) {
        if(amount <0) return;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average = amount/datas.size();
        forAtmospheresDo((dir, atmosphere) -> {
            atmosphere.getUnderlying().putHeat(average,mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]));
            return null;
        });
    }

    @Override
    public void putHeatToCurrentLayer(double amount) {
        if(amount <0) return;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average = amount/datas.size();
        forAtmospheresDo((dir, atmosphere) -> {
            Layer layer = atmosphere.getLayer(mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]));
            if(layer == null) return null;
            layer.putHeat(average,mutableBlockPos);
            return null;
        });
    }

    @Override
    public double drawHeatFromAtmosphere(double amount) {
        if(amount <0) return 0;
        if(skyLight == 0) return amount;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final Set<Pair<int[],Layer>> layerToDraw = new HashSet<>();
        forAtmospheresDo((dir,atmosphere) -> {
            mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]);
            Layer layer = atmosphere.getLayer(mutableBlockPos);
            if(layer == null) return null;
            if(layer instanceof AtmosphereLayer){
                layerToDraw.add(Pair.of(dir,layer));
            }else{
                if(pos.getY()>atmosphere.getTopLayer().getTopY()) return null;
                if(pos.getY()<0) return null;
                layer = atmosphere.getBottomAtmosphereLayer();
                if(layer == null) return null;
                layerToDraw.add(Pair.of(dir,layer));
            }
            return null;
        });
        if(layerToDraw.isEmpty()) return 0;
        double average = amount/layerToDraw.size(),res = 0;
        double factor = 1;
        if(skyLight>0 && skyLight<15) factor = skyLight/15d;
        for(Pair<int[],Layer> pair:layerToDraw){
            mutableBlockPos.setPos(pos.getX()+pair.getKey()[0],pos.getY(),pos.getZ()+pair.getKey()[1]);
            res += pair.getValue().drawHeat(average*factor,mutableBlockPos)/factor;
        }
        return res;
    }

    @Override
    public double drawHeatFromUnderlying(double amount) {
        if(amount <0) return 0;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final double average = amount/datas.size();
        return drawAtmosphereProperty((dir,atmosphere)->{
            mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]);
            return atmosphere.getUnderlying().drawHeat(average,mutableBlockPos);
        });
    }

    @Override
    public double drawHeatFromCurrentLayer(double amount) {
        if(amount <0) return 0;
        checkAtmosphereDataLoaded();
        clearInvalidData();
        final Set<Pair<int[],Layer>> layerToDraw = new HashSet<>();
        forAtmospheresDo((dir,atmosphere) -> {
            mutableBlockPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]);
            Layer layer = atmosphere.getLayer(mutableBlockPos);
            if(layer == null) return null;
            layerToDraw.add(Pair.of(dir,layer));
            return null;
        });
        if(layerToDraw.isEmpty()) return 0;
        double average = amount/layerToDraw.size(),res = 0;
        for(Pair<int[],Layer> pair:layerToDraw){
            mutableBlockPos.setPos(pos.getX()+pair.getKey()[0],pos.getY(),pos.getZ()+pair.getKey()[1]);
            res += pair.getValue().drawHeat(average,mutableBlockPos);
        }
        return res;
    }

    @Override
    public void sendHeat(HeatPack pack, EnumFacing direction) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        if(!(layer instanceof AtmosphereLayer)) layer = data.getAtmosphere().getBottomAtmosphereLayer();
        if(layer == null) return;
        layer.sendHeat(pack,direction);
    }

    @Override
    public void sendHeat(HeatPack pack, Vec3d directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        if(!(layer instanceof AtmosphereLayer)) layer = data.getAtmosphere().getBottomAtmosphereLayer();
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }

    @Override
    public void sendHeat(HeatPack pack, Vec3i directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        if(!(layer instanceof AtmosphereLayer)) layer = data.getAtmosphere().getBottomAtmosphereLayer();
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }

    protected double getAtmosphereValue(BiFunction<int[],Atmosphere,Double> method){
        checkAtmosphereDataLoaded();
        double res = 0;
        int cot = 0;
        for(Map.Entry<int[],AtmosphereData> entry:datas.entrySet()){
            if(isInvalidData(entry.getValue())) continue;
            res += method.apply(entry.getKey(),entry.getValue().getAtmosphere());
            cot ++;
        }
        if(cot == 0) return method.apply(CURRENT,data.getAtmosphere());
        return res/cot;
    }

    protected double drawAtmosphereProperty(BiFunction<int[],Atmosphere,Double> method){
        checkAtmosphereDataLoaded();
        double res = 0;
        int cot = 0;
        for(Map.Entry<int[],AtmosphereData> entry:datas.entrySet()){
            if(isInvalidData(entry.getValue())) continue;
            res += method.apply(entry.getKey(),entry.getValue().getAtmosphere());
            cot ++;
        }
        if(cot == 0) return 0;
        return res;
    }

    /**
     * 用之前记得{@link #clearInvalidData()}
     */
    protected void forAtmospheresDo(BiFunction<int[],Atmosphere,Void> method){
        checkAtmosphereDataLoaded();
        for(Map.Entry<int[],AtmosphereData> entry:datas.entrySet()){
            method.apply(entry.getKey(),entry.getValue().getAtmosphere());
        }
    }

    protected void loadDatas(){
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos(pos);
        datas.put(CURRENT,data);
        for(int[] dir:DIRS8){
            currentPos.setPos(pos.getX()+dir[0],pos.getY(),pos.getZ()+dir[1]);
            AtmosphereData curDat = system.getDataProvider().getLoadedAtmosphereData(currentPos.getX()>>4,currentPos.getZ()>>4);
            if(curDat == null) continue;
            if(curDat.isUnloaded()) continue;
            if(curDat.getAtmosphere() == null) continue;
            datas.put(dir,curDat);
        }
    }

    protected boolean isInvalidData(AtmosphereData data){
        return data == null||data.isUnloaded()||data.getAtmosphere() == null;
    }

    protected void clearInvalidData(){
        checkAtmosphereDataLoaded();
        Set<int[]> toClear = new HashSet<>();
        for(Map.Entry<int[],AtmosphereData> entry:datas.entrySet()){
            if(isInvalidData(entry.getValue())) toClear.add(entry.getKey());
        }
        for(int[] key:toClear){
            datas.remove(key);
        }
        if(datas.get(CURRENT) == null) refresh();
    }
}
