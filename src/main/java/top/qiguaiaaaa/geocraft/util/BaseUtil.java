/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package top.qiguaiaaaa.geocraft.util;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;

import java.io.File;
import java.util.Random;

/**
 * 该类会在Early Mixin时访问
 * @author QiguaiAAAA
 */
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

    public static float checkAndReturn(float num,float min,float max) throws IllegalArgumentException{
        if(num<min || num>max){
            throw new IllegalArgumentException("Value {} must be in range ["+min+","+max+"]");
        }
        return num;
    }

    public static double checkAndReturn(double num,double min,double max) throws IllegalArgumentException{
        if(num<min || num>max){
            throw new IllegalArgumentException("Value {} must be in range ["+min+","+max+"]");
        }
        return num;
    }

    public static int checkAndReturn(int num,int min,int max) throws IllegalArgumentException{
        if(num<min || num>max){
            throw new IllegalArgumentException("Value {} must be in range ["+min+","+max+"]");
        }
        return num;
    }

    public static long checkAndReturn(long num,long min,long max) throws IllegalArgumentException{
        if(num<min || num>max){
            throw new IllegalArgumentException("Value {} must be in range ["+min+","+max+"]");
        }
        return num;
    }

    public static int getRandomPressureSearchRange() {
        return FluidPhysicsConfig.WEIGHT_DISTRIBUTION_FOR_PRESSURE_SEARCH_RANGE.getRandomResult(random);
    }


}
