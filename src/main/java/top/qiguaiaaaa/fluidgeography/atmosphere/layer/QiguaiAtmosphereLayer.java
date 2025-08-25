package top.qiguaiaaaa.fluidgeography.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.BaseAtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;
import top.qiguaiaaaa.fluidgeography.atmosphere.AtmospherePropertyManager;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;
import top.qiguaiaaaa.fluidgeography.util.MathUtil;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public abstract class QiguaiAtmosphereLayer extends BaseAtmosphereLayer{
    protected final Map<EnumFacing, Vec3d> winds = new EnumMap<>(EnumFacing.class);
    protected double heatCapacity = Double.MAX_VALUE/2;
    protected final GasState water = FGAtmosphereProperties.WATER.getStateInstance();
    protected final GasState steam = FGAtmosphereProperties.STEAM.getStateInstance();
    protected final TemperatureState temperature = FGAtmosphereProperties.TEMPERATURE.getStateInstance();
    //缓存数值
    protected double 长波吸收率,长波发射率,平均密度,本层气压,本层体积;
    public QiguaiAtmosphereLayer(DefaultAtmosphere atmosphere) {
        super(atmosphere);
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            winds.put(facing,Vec3d.ZERO);
        }
        winds.put(EnumFacing.UP,Vec3d.ZERO);
        states.put(FGAtmosphereProperties.WATER,water);
        states.put(FGAtmosphereProperties.STEAM,steam);
        states.put(FGAtmosphereProperties.TEMPERATURE, temperature);
    }
    public void updateHeatCapacity(){
        heatCapacity = AtmosphereUtil.FinalFactors.大气单元底面积*
                Altitude.to物理高度(getDepth()) *
                getDensity()*
                AtmosphereUtil.FinalFactors.干空气比热容;
        heatCapacity += water.getAmount()* 4200;
        heatCapacity += steam.getAmount()* 1860;
    }
    /**
     * 计算本层大气的短波辐射透过率
     * @return 短波辐射参数，透过率和吸收率，介于0~1之间
     */
    public double[] 计算短波辐射参数(Degree 与水平面夹角) {
        double 液态水每平方质量 = water.getAmount()/ AtmosphereUtil.FinalFactors.大气单元底面积;
        double 气态水每平方质量 = steam.getAmount() / AtmosphereUtil.FinalFactors.大气单元底面积;
        double 空气密度 = getDensity();

        double sin = Math.sin(与水平面夹角.getRadian());
        sin = Math.max(sin, 0.0001);

        //计算各部分光学厚度
        double 云光学厚度 = AtmosphereUtil.FinalFactors.质量消光系数_云*液态水每平方质量;
        double 大气光学厚度 = AtmosphereUtil.FinalFactors.质量消光系数_气体*空气密度*Altitude.to物理高度(getDepth());
        double 水汽光学厚度 = AtmosphereUtil.FinalFactors.质量消光系数_水汽 * 气态水每平方质量;
        double 总光学厚度 = 云光学厚度+大气光学厚度+水汽光学厚度;

        //SSA（单散射比）
        final double SSA_cloud = 0.99;
        final double SSA_gas   = 0.01;
        final double SSA_steam = 0.01;
        double SSA_total = (云光学厚度 * SSA_cloud + 水汽光学厚度 * SSA_steam + 大气光学厚度 * SSA_gas) / 总光学厚度;

        // Beer–Lambert
        double 透过率 = MathHelper.clamp(Math.exp(-总光学厚度/sin), 0.0, 1.0);
        double 吸收率 = (1-透过率) * (1.0 - SSA_total);

        return new double[]{透过率,吸收率};
    }

    public double 计算垂直对流速度() {
        if (upperLayer == null) return 0.0;

        double 本层温度 = temperature.get();
        double 上层温度 = upperLayer.getTemperature().get();

        // 假设气块从本层绝热抬升到上层高度
        double 抬升高度 = Altitude.to物理高度(upperLayer.getBeginY() - getBeginY());
        double 气块温度 = 本层温度 - AtmosphereUtil.FinalFactors.干绝热温度直减率 * 抬升高度;

        double 温度差 = 气块温度 - 上层温度;
        double 浮力加速度 = AtmosphereUtil.FinalFactors.重力加速度 * 温度差 / 上层温度;

        return 浮力加速度*0.1;
    }

    /**
     * 对本层大气的长波辐射参数进行更新
     */
    public void 更新长波辐射参数(){
        double 水汽路径 = steam.getAmount() / AtmosphereUtil.FinalFactors.大气单元底面积;
        double 液态水路径 = water.getAmount() / AtmosphereUtil.FinalFactors.大气单元底面积;

        double 温室气体效应 = AtmosphereUtil.FinalFactors.大气单元底面积*
                Altitude.to物理高度(getDepth())*
                getDensity()*
                AtmosphereUtil.FinalFactors.温室气体浓度*
                AtmosphereUtil.FinalFactors.温室气体吸收系数;

        长波吸收率 = 1.0 - Math.exp(
                -AtmosphereUtil.FinalFactors.水汽长波吸收系数 * 水汽路径
                        - AtmosphereUtil.FinalFactors.液态水长波吸收系数 * 液态水路径
                        - 温室气体效应
        );

        //基尔霍夫热辐射定律
        长波发射率 = 长波吸收率;

        // 确保值在合理范围内
        长波吸收率 = MathHelper.clamp(长波吸收率, 0.0, 0.999);
        长波发射率 = MathHelper.clamp(长波发射率, 0.0, 0.999);
    }

    public void 更新缓存(){
        平均密度 = getDensity();
        本层气压 = getPressure();
        本层体积 = AtmosphereUtil.FinalFactors.大气单元底面积*Altitude.to物理高度(getDepth());
    }

    public double 散度(){
        return (winds.get(EnumFacing.EAST).x-winds.get(EnumFacing.WEST).x)/16+
                (winds.get(EnumFacing.SOUTH).z-winds.get(EnumFacing.NORTH).z)/16;
    }

    protected Vec3d 计算坡度修饰后风速分量(double 水平风,double 海拔差,EnumFacing dir){
        double 重力风 = Math.sqrt(2 * AtmosphereUtil.FinalFactors.重力加速度 * Math.abs(海拔差)) * 0.01;
        double tan坡角 = 海拔差/16;
        if(水平风*tan坡角<0){
            return new Vec3d(dir.getDirectionVec()).scale(水平风).add(0,Math.max(-重力风,水平风*tan坡角),0);
        }
        double 修正水平风;
        double 坡角 = Math.atan(tan坡角);

        double a = (1+tan坡角*tan坡角)*(水平风*水平风),
                b = 2*水平风*重力风*tan坡角,
                c = 重力风*重力风-水平风*水平风;
        double delta = b*b-4*a*c;
        if(delta <0){
            修正水平风 = -水平风*Math.cos(坡角);
        }else{
            修正水平风 = 水平风*MathHelper.clamp((-b+Math.sqrt(delta))/(2*a),-1,1);
        }
        return new Vec3d(dir.getDirectionVec()).scale(修正水平风).add(0,修正水平风*tan坡角,0);

    }

    /**
     * 计算朝向目标大气的风速分量(包含垂直分量)
     * @param to 目标大气
     * @param dir 目标大气相对于自身的方向
     * @return 风速分量
     */
    public Vec3d 计算水平风速分量(Atmosphere to, EnumFacing dir){
        Vec3d wind = Vec3d.ZERO;
        Altitude 对方平均海拔 = to.getUnderlying().getAltitude();
        if(getTopY()>对方平均海拔.get()){
            double 本层气压 = getPressure();
            double 对方大气同高度气压 = to.getPressure(new BlockPos(0,getCenterY(),0));
            double 水平风 = Math.sqrt(Math.abs(本层气压-对方大气同高度气压)/平均密度)*0.1*(本层气压>对方大气同高度气压?1:-1);

            double 海拔差 = 对方平均海拔.get()-getBeginY();
            if(海拔差<1e-2){
                wind = new Vec3d(dir.getDirectionVec()).scale(水平风);
            }else{
                wind = 计算坡度修饰后风速分量(水平风,海拔差,dir);
            }
        }

        for(AtmosphereProperty property: AtmospherePropertyManager.getWindEffectedProperties()){
            wind.add(property.getWind(this,to,dir));
        }
        return wind;
    }
    public abstract Vec3d 计算上风速();
    public abstract Vec3d 计算下风速();
    protected abstract void 对流();

    /**
     * 计算向目标大气传送的热量
     * @param to B大气
     * @param windSpeed 大气间风速，正方向为A指向B
     * @return 互相传输的热量，正为A向B传输，负为B向A传输
     */
    protected double 计算热量平流量(Atmosphere to, double windSpeed){
        double referenceWind = 32.0; // 归一化
        return 平均密度 * 本层体积 * AtmosphereUtil.FinalFactors.干空气比热容 *
                (temperature.get()-to.getTemperature(new BlockPos(0,getCenterY(),0))) *
                Math.min(windSpeed / referenceWind, 1.6);
    }

    protected void 热量平流(Atmosphere to,EnumFacing dir) {
        if (to.getUnderlying().getAltitude().get() > getTopY()) return;
        double windSpeedSize = MathUtil.获得带水平正负方向的速度(winds.get(dir),dir);
        if (windSpeedSize < 0) return;
        double heatTransferQuantity = 计算热量平流量(to, windSpeedSize);
        to.putHeat(heatTransferQuantity, new BlockPos(0, getCenterY(), 0));
        temperature.add热量(-heatTransferQuantity, heatCapacity);
    }

    protected void 大气平流(Chunk chunk, Triple<Atmosphere,Chunk,EnumFacing> neighbor){
        //能量平流
        热量平流(neighbor.getLeft(),neighbor.getRight());
        //物质和其他属性平流
        for(AtmosphereProperty property: AtmospherePropertyManager.getFlowableProperties()){
            property.onFlow(this,chunk,neighbor.getLeft(),neighbor.getMiddle(),neighbor.getRight(),winds.get(neighbor.getRight()));
        }
    }

    @Override
    public void initialise(Chunk chunk) {
        updateHeatCapacity();
        更新缓存();
        更新长波辐射参数();
    }

    @Override
    public void tick(Chunk chunk,Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors) {
        更新长波辐射参数();
        if(!atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()){
            double 辐射能量 = 长波发射率 * AtmosphereUtil.FinalFactors.斯特藩_玻尔兹曼常数 *
                    Math.pow(temperature.get(), 4) *
                    AtmosphereUtil.FinalFactors.大气单元底面积;

            temperature.add热量(-辐射能量, heatCapacity);

            if (lowerLayer != null) {
                HeatPack 向下辐射包 = new HeatPack(HeatPack.HeatType.LONG_WAVE, 辐射能量 * 0.5);
                lowerLayer.sendHeat(向下辐射包,EnumFacing.DOWN);
            }
            if (upperLayer != null) {
                HeatPack 向上辐射包 = new HeatPack(HeatPack.HeatType.LONG_WAVE, 辐射能量 * 0.5);
                upperLayer.sendHeat(向上辐射包,EnumFacing.UP);
            }
        }
        更新缓存();
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            EnumFacing direction = neighbor.getRight();
            Vec3d newWindSpeed = 计算水平风速分量(neighbor.getLeft(),direction);
            winds.put(direction,newWindSpeed);
        }
        //大气平流
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            大气平流(chunk,neighbor);
        }
        //垂直计算
        winds.put(EnumFacing.UP, 计算上风速());
        winds.put(EnumFacing.DOWN,计算下风速());
        对流();
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable EnumFacing direction) {
        if(direction == null || pack.getType() == null){
            this.putHeat(pack.getHeat(),null);
            return;
        }
        double 透过率=0.98;
        double 吸收率=0.01;

        switch (pack.getType()){
            case SHORT_WAVE:
                Degree degree = Degree.RIGHT_ANGLE;
                double[] 参数 = 计算短波辐射参数(degree);
                透过率 = 参数[0];
                吸收率 = 参数[1];
                break;
            case LONG_WAVE:
                吸收率 = 长波吸收率;
                透过率 = 1.0 - 吸收率;
                break;
        }

        double 吸收量 = pack.getHeat()*吸收率,
                不透过量 = pack.getHeat()*(1-透过率);

        temperature.add热量(pack.drawHeat(吸收量),heatCapacity);
        pack.drawHeat(Math.max(不透过量-吸收量,0));

        if(pack.isEmpty()) return;

        if(direction == EnumFacing.DOWN && lowerLayer != null){
            lowerLayer.sendHeat(pack,direction);
        }else if(direction == EnumFacing.UP && upperLayer != null){
            upperLayer.sendHeat(pack,direction);
        }
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3d direction) {
        if(direction == null || pack.getType() == null){
            this.putHeat(pack.getHeat(),null);
            return;
        }
        double 透过率=0.98;
        double 吸收率=0.01;

        switch (pack.getType()){
            case SHORT_WAVE:
                Degree degree = MathUtil.计算与水平面夹角(direction);
                double[] 参数 = 计算短波辐射参数(degree);
                透过率 = 参数[0];
                吸收率 = 参数[1];
                break;
            case LONG_WAVE:
                吸收率 = 长波吸收率;
                透过率 = 1.0 - 吸收率;
                break;
        }

        double 吸收量 = pack.getHeat()*吸收率,
                不透过量 = pack.getHeat()*(1-透过率);

        temperature.add热量(pack.drawHeat(吸收量),heatCapacity);
        pack.drawHeat(Math.max(不透过量-吸收量,0));

        if(pack.isEmpty()) return;

        if(direction.y<0 && lowerLayer != null){
            lowerLayer.sendHeat(pack,direction);
        }else if(direction.y>0 && upperLayer != null){
            upperLayer.sendHeat(pack,direction);
        }
    }

    @Override
    public double drawHeat(double quanta) {
        temperature.add热量(-quanta, heatCapacity);
        return quanta;
    }

    @Override
    public boolean addSteam(BlockPos pos, int amount) {
        return steam.addAmount(amount);
    }

    @Override
    public boolean addWater(BlockPos pos, int amount) {
        return water.addAmount(amount);
    }

    @Override
    public double getPressure(BlockPos pos) {
        if(shouldSwitchToLowerLayer(pos)) return lowerLayer.getPressure(pos);
        if(shouldSwitchToUpperLayer(pos)) return upperLayer.getPressure(pos);
        return AtmosphereUtil.FinalFactors.海平面气压 *
                Math.exp(
                        -AtmosphereUtil.FinalFactors.干空气摩尔质量 *
                                AtmosphereUtil.FinalFactors.重力加速度 *
                                Altitude.get物理海拔(pos.getY()) /
                                (AtmosphereUtil.FinalFactors.气体常数 * getTemperature(pos,false))
                );
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        if(shouldSwitchToLowerLayer(pos)) return lowerLayer.getWaterPressure(pos);
        if(shouldSwitchToUpperLayer(pos)) return upperLayer.getWaterPressure(pos);
        return getWaterPressure();
    }

    /**
     * 获取大气平均水汽压
     * @return 大气平均水汽压,单位为Pa
     */
    @Override
    public double getWaterPressure() {
        GasState steam = getSteam();
        if(steam == null) return 0;
        return steam.getAmount()*
                AtmosphereUtil.FinalFactors.气体常数*
                getTemperature().get()
                / (
                AtmosphereUtil.FinalFactors.水摩尔质量 *
                        AtmosphereUtil.FinalFactors.大气单元底面积 *
                        (Altitude.to物理高度(getDepth()))
        );
    }

    /**
     * 获取大气平均大气压
     * @return 平均大气压,单位为Pa
     */
    public double getPressure() {
        return AtmosphereUtil.FinalFactors.海平面气压 *
                Math.exp(
                        -AtmosphereUtil.FinalFactors.干空气摩尔质量 *
                                AtmosphereUtil.FinalFactors.重力加速度 *
                                Altitude.get物理海拔(getCenterY()) /
                                (AtmosphereUtil.FinalFactors.气体常数 * temperature.get())
                );
    }

    /**
     * 获取大气平均密度
     * @return 平均密度,单位 kg/m^3
     */
    public double getDensity(){
        final double eps = 0.622;               // ε = Mv / Md ≈ 0.622
        double P = getPressure();
        double waterPressure = getWaterPressure();
        double T = getTemperature().get();

        waterPressure = MathHelper.clamp(waterPressure,0,0.9999 * P);

        //虚温法
        double r = eps * waterPressure / Math.max(1e-12, P);
        double modifiedT = T * (1.0 + 0.61 * r);
        return P / (AtmosphereUtil.FinalFactors.干空气比热容 * modifiedT);       // kg/m^3
    }

    @Override
    public Vec3d getWind(BlockPos pos) {
        if(shouldSwitchToLowerLayer(pos)) return lowerLayer.getWind(pos);
        if(shouldSwitchToUpperLayer(pos)) return upperLayer.getWind(pos);
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        double weightS = z/16.0,
                weightN = 1-weightS,
                weightE = x/16.0,
                weightW = 1-weightE;
        return winds.get(EnumFacing.SOUTH).scale(weightS)
                .add(winds.get(EnumFacing.NORTH).scale(weightN))
                .add(winds.get(EnumFacing.EAST).scale(weightE))
                .add(winds.get(EnumFacing.WEST).scale(weightW));
    }

    public Vec3d getWind(EnumFacing facing){
        return winds.get(facing);
    }

    public Map<EnumFacing, Vec3d> getWinds(){
        return winds;
    }

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    @Override
    public GasState getWater() {
        return water;
    }

    @Nullable
    @Override
    public GasState getSteam() {
        return steam;
    }

    public double getCenterY(){
        return getBeginY()+getDepth()/2;
    }
    public double getTopY(){
        return getBeginY()+getDepth();
    }

    protected boolean shouldSwitchToLowerLayer(BlockPos pos){
        return pos.getY()<getBeginY()&& lowerLayer !=null;
    }
    protected boolean shouldSwitchToUpperLayer(BlockPos pos){
        return pos.getY()>getBeginY()+getDepth() && upperLayer != null;
    }
}
