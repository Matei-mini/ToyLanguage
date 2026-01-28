package model.expressions;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.IType;
import model.values.IValue;

public class ValueExpression implements IExp {
    private IValue val;
    public ValueExpression(IValue val) {
        this.val = val;
    }

    public IValue eval(MyIDictionary<String,IValue> tbl, MyIHeap heap) throws MyException {return val;}

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return val.getType();
    }

    @Override
    public String toString() {
        return "ValueExpression{" +
                "val=" + val +
                '}';
    }
}
