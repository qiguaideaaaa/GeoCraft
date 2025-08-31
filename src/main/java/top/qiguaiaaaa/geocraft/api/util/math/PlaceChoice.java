package top.qiguaiaaaa.geocraft.api.util.math;

import net.minecraft.util.math.BlockPos;

public final class PlaceChoice {
    private int quanta;
    public final BlockPos pos;
    public PlaceChoice(int rawQuanta,BlockPos pos){
        this.quanta = rawQuanta;
        this.pos = pos;
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
