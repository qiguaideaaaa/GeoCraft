package top.qiguaiaaaa.geocraft.api.util.math;

import net.minecraft.util.EnumFacing;
import top.qiguaiaaaa.geocraft.block.IPermeable;

public final class FlowChoice {
    private int quanta;
    public final EnumFacing direction;
    public final int heightPerQuanta;
    public final IPermeable block;
    public FlowChoice(int rawQuanta,EnumFacing direction){
        this(rawQuanta,direction,1,null);
    }
    public FlowChoice(int rawQuanta, EnumFacing direction, int heightPerQuanta, IPermeable block){
        this.quanta = rawQuanta;
        this.direction = direction;
        this.heightPerQuanta = heightPerQuanta;
        this.block = block;
    }

    public void setQuanta(int quanta) {
        this.quanta = quanta;
    }

    public int getQuanta() {
        return quanta;
    }
    public int getHeight(){
        return quanta*heightPerQuanta;
    }
    public void addQuanta(int i){
        quanta +=i;
    }
}
