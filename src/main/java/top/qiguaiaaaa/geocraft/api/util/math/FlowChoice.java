package top.qiguaiaaaa.geocraft.api.util.math;

import net.minecraft.util.EnumFacing;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;

public final class FlowChoice {
    private int quanta;
    public final EnumFacing direction;
    public final int heightPerQuanta;
    public final IPermeableBlock block;
    public FlowChoice(int rawQuanta,EnumFacing direction){
        this(rawQuanta,direction,1,null);
    }
    public FlowChoice(int rawQuanta, EnumFacing direction, int heightPerQuanta, IPermeableBlock block){
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
