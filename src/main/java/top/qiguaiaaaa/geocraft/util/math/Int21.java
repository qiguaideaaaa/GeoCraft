package top.qiguaiaaaa.geocraft.util.math;

/**
 * @author QiguaiAAAA
 */
public final class Int21 {
    public static final int SIGN_MASK = 0x100000;
    public static final int CONTENT_MASK = 0xFFFFF;
    private final int val;

    public Int21(int v){
        val = (int) toInt21(v);
    }

    public Int21(short v){
        val = (int) toInt21(v);
    }

    public Int21(byte v){
        val = (int) toInt21(v);
    }

    public static long toInt21(byte v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }

    public static long toInt21(short v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }

    public static long toInt21(int v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }
}
