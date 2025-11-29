package model.values;

import model.types.IType;
import model.types.IntType;

public class IntValue implements IValue{
    private int val;
    public IntValue(int v){val=v;}
    public int getVal() {return val;}
    public String toString()
    {
        return String.valueOf(val);
    }
    public IType getType() { return new IntType();}

    public boolean equals(Object another) {
        if (another instanceof IntValue){
            IntValue a = (IntValue) another;
            return a.getVal() == val;
        }
        return false;
    }
}
