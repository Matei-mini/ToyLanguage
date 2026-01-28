package model.expressions;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.IType;
import model.types.RefType;
import model.values.IValue;
import model.values.RefValue;

public class HeapReadingExpression implements IExp{
    private IExp exp;

    public HeapReadingExpression(IExp e){
        this.exp = e;
    }

    @Override
    public IValue eval(MyIDictionary<String, IValue> table, MyIHeap heap) throws MyException {
        IValue val = exp.eval(table, heap);
        if(!(val instanceof RefValue))
            throw new MyException("Expression must be RefValue");
        RefValue refValue = (RefValue) val;
        int address = refValue.getAddr();
        if(!heap.isDefined(address))
            throw new MyException("Address is not defined.");
        return heap.get(address);
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typ = exp.typecheck(typeEnv);
        if(typ instanceof RefType){
            RefType ref = (RefType) typ;
            return ref.getInner();
        }
        else
            throw new MyException("the rH argument is not a RefType");
    }

    public String toString(){
        return "rH("+exp.toString();
    }
}
