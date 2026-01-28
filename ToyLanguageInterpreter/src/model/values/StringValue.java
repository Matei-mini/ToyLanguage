package model.values;

import model.types.IType;
import model.types.StringType;

public class StringValue implements IValue{
    private String value;

    public StringValue(String value){
        this.value = value;
    }

    @Override
    public IType getType() {
        return new StringType();
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return value;
    }

    public boolean equals(Object another){
        if(another instanceof StringValue){
            StringValue a = (StringValue) another;
            return a.getValue().equals(value);
        }
        return false;
    }

}
