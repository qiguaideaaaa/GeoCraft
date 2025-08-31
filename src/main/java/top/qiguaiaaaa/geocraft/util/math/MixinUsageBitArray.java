package top.qiguaiaaaa.geocraft.util.math;

import org.apache.commons.lang3.Validate;

/**
 * 该类Copy自 {@link net.minecraft.util.BitArray}
 * 用于发往客户端时修改Chunk数据
 */
public class MixinUsageBitArray {
    protected final long[] longArray;
    protected final int bitsPerEntry;
    protected final long maxEntryValue;
    protected final int arraySize;

    public MixinUsageBitArray(int bitsPerEntryIn, int arraySizeIn,long[] longArray) {
        Validate.inclusiveBetween(1L, 32L, bitsPerEntryIn);
        this.arraySize = arraySizeIn;
        this.bitsPerEntry = bitsPerEntryIn;
        this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
        this.longArray = longArray;
    }

    public void setAt(int index, int value) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        Validate.inclusiveBetween(0L, this.maxEntryValue, value);
        int i = index * this.bitsPerEntry;
        int j = i / 64;
        int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l = i % 64;
        this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;

        if (j != k) {
            int i1 = 64 - l;
            int j1 = this.bitsPerEntry - i1;
            this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
        }
    }

    public int getAt(int index) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        int i = index * this.bitsPerEntry;
        int j = i / 64;
        int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l = i % 64;

        if (j == k) {
            return (int)(this.longArray[j] >>> l & this.maxEntryValue);
        } else {
            int i1 = 64 - l;
            return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
        }
    }

    public long[] getBackingLongArray() {
        return this.longArray;
    }

    public int size()
    {
        return this.arraySize;
    }
}
