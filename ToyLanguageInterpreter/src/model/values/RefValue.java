package model.values;

import model.types.IType;
import model.types.RefType;

public class RefValue implements IValue {
        int address;
        IType locationType;

        public RefValue(int ad, IType lt) {
            this.address = ad;
            this.locationType = lt;
        }

        public int getAddr() {return address;}

        @Override
        public IType getType() { return new RefType(locationType);}

        public IType getLocationType(){
            return locationType;
        }

    @Override
    public String toString() {
        return "Ref("+address+","+locationType+")";
    }
}

