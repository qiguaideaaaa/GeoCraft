package top.qiguaiaaaa.geocraft.api.atmosphere.weather;

public class Weather {
    public static final Weather SUNNY = new Weather("Sunny", "");
    public static final Weather LIGHT_RAIN = new Weather("LightRain","");

    protected final String name, translationKey;

    public Weather(String name, String translationKey) {
        this.name = name;
        this.translationKey = translationKey;
    }

    @Override
    public String toString() {
        return name;
    }
}
