package top.qiguaiaaaa.geocraft.util.math;

/**
 * @author QiguaiAAAA
 */
public final class Int10 {
    public static final int SIGN_MASK = 0x200;
    public static final int CONTENT_MASK = 0x1FF;

    private final short val;

    public Int10(int v){
        val = (short) toInt10(v);
    }

    public Int10(short v){
        val = (short) toInt10(v);
    }

    public Int10(byte v){
        val = (short) toInt10(v);
    }

    public static int toInt10(byte v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }

    public static int toInt10(short v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }

    public static int toInt10(int v){
        if(v>=0) return v;
        return SIGN_MASK | ((-v) & CONTENT_MASK);
    }
}
