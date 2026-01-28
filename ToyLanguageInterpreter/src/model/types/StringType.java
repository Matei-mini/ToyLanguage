package model.types;

import model.values.IValue;
import model.values.StringValue;

public class StringType implements IType{
    public StringType(){}
    public String toString(){
        return "String";
    }
    public boolean equals(Object another){
        return another instanceof StringType;
    }

    @Override
    public IValue defaultValue() {
        return new StringValue("");
    }
}
