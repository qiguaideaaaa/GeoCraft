package top.qiguaiaaaa.geocraft.util;

import top.qiguaiaaaa.geocraft.GeoCraft;

import java.io.File;
import java.util.Random;

public final class BaseUtil {
    private static final Random random = new Random();
    public static File getSuggestedConfigurationFile(){
        File configurationDir = new File("config");
        if(!configurationDir.exists()) return null;
        return new File(configurationDir+File.separator+ GeoCraft.MODID+".cfg");
    }
    public static boolean getRandomResult(Random rand,double possibility){
        final int accuracy = 100000000;
        return rand.nextInt(accuracy) <= possibility*accuracy;
    }
    public static int[] toIntArray(String[] array){
        int[] ints = new int[array.length];
        for(int i=0;i<array.length;i++){
            ints[i] = Integer.parseInt(array[i]);
        }
        return ints;
    }
    public static long[] toLongArray(String[] array){
        long[] longs = new long[array.length];
        for(int i=0;i<array.length;i++){
            longs[i] = Long.parseLong(array[i]);
        }
        return longs;
    }
    public static boolean[] toBooleanArray(String[] array){
        boolean[] booleans = new boolean[array.length];
        for(int i=0;i<array.length;i++){
            booleans[i] = Boolean.parseBoolean(array[i]);
        }
        return booleans;
    }
    public static double[] toDoubleArray(String[] array){
        double[] doubles = new double[array.length];
        for(int i=0;i<array.length;i++){
            doubles[i] = Double.parseDouble(array[i]);
        }
        return doubles;
    }

    public static double getMemoryAvailablePercent(){
        Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory()/(double)runtime.totalMemory();
    }

    public static int getRandomPressureSearchRange() {
        int rand = random.nextInt(100); // 0-99

        if (rand < 80) return 2;        // 0-79 (80%)
        if (rand < 90) return 3;        // 80-89 (10%)
        if (rand < 95) return 4;        // 90-94 (5%)
        if (rand < 99) return 5;        // 95-98 (4%)
        return 6;                       // 99 (1%)
    }
}
