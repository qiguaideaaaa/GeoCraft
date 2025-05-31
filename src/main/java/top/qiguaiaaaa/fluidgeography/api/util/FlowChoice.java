package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.util.EnumFacing;

public final class FlowChoice {
    private int quanta;
    public final EnumFacing direction;
    public FlowChoice(int rawQuanta,EnumFacing direction){
        this.quanta = rawQuanta;
        this.direction = direction;
    }

    public void setQuanta(int quanta) {
        this.quanta = quanta;
    }

    public int getQuanta() {
        return quanta;
    }
    public void addQuanta(int i){
        quanta +=i;
    }
}
