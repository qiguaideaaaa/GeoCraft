package top.qiguaiaaaa.geocraft.geography.atmosphere;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.BaseAtmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.IAtmosphereTracker;
import top.qiguaiaaaa.geocraft.api.state.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class QiguaiAtmosphere extends BaseAtmosphere {
    protected boolean debug = false;
    protected int x,z;

    public abstract void updateTick(@Nullable Chunk chunk);

    protected void updateListeners(){
        for(IAtmosphereTracker listener:listeners) listener.notify(this);
    }

    public void setLocation(int x,int z){
        this.x = x;
        this.z = z;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public void onUnload() {}

    @Override
    public boolean addSteam(int addAmount, @Nonnull BlockPos pos){
        return getAtmosphereLayer(pos).addSteam(pos,addAmount);
    }

    @Override
    public boolean addWater(int amount, @Nonnull BlockPos pos) {
        return getAtmosphereLayer(pos).addWater(pos,amount);
    }

    @Override
    public int drainWater(int amount, @Nonnull BlockPos pos, boolean test) {
        if(amount<0) return 0;
        int realAmount = 0;
        for(Layer layer = getAtmosphereLayer(pos); layer != null; layer=layer.getUpperLayer()){
            if(!(layer instanceof AtmosphereLayer)) continue;
            FluidState state = layer.getWater();
            if(state == null) continue;
            int realAmountLayer = Math.min(amount,state.getAmount());
            amount -= realAmountLayer;
            realAmount += realAmountLayer;
            if(!test) state.addAmount(-realAmountLayer);
            if(amount <=0) break;
        }
        return realAmount;
    }

    @Override
    public void putHeat(double Q, BlockPos pos){
        getAtmosphereLayer(pos).putHeat(Q,pos);
    }

    @Override
    public Layer getLayer(@Nonnull BlockPos pos) {
        Layer res = null;
        for (Layer layer : layers) {
            if (pos.getY() < layer.getBeginY()) break;
            res = layer;
        }
        return res;
    }

    public AtmosphereLayer getAtmosphereLayer(BlockPos pos){
        Layer res = getLayer(pos);
        while (!(res instanceof AtmosphereLayer)){
            if(res == null) return getBottomAtmosphereLayer();
            res = res.getUpperLayer();
        }
        return (AtmosphereLayer) res;
    }

    //************
    // Override
    //************

    @Override
    public Vec3d getWind(@Nonnull BlockPos pos){
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return Vec3d.ZERO;
        return layer.getWind(pos);
    }

    @Override
    public double getPressure(@Nonnull BlockPos pos) {
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return 0;
        return layer.getPressure(pos);
    }

    @Override
    public double getWaterPressure(@Nonnull BlockPos pos) {
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return 0;
        return layer.getWaterPressure(pos);
    }


    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(Layer layer:layers){
            if(!layer.isSerializable()) continue;
            compound.setTag(layer.getTagName(),layer.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(Layer layer:layers){
            if(!layer.isSerializable()) continue;
            NBTBase base = nbt.getTag(layer.getTagName());
            if(!(base instanceof NBTTagCompound)) throw new IllegalArgumentException("NBT of Atmosphere Layer "+layer.getTagName()+" isn't a valid compound tag!");
            layer.deserializeNBT((NBTTagCompound) base);
        }
    }
}
