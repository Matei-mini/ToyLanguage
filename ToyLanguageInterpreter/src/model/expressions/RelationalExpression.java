package model.expressions;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.IType;
import model.types.IntType;
import model.values.BoolValue;
import model.values.IValue;
import model.values.IntValue;

public class RelationalExpression implements IExp{
    private IExp e1;
    private IExp e2;
    private int op;

    public RelationalExpression(IExp e1, IExp e2, int o) {
        this.e1 = e1;
        this.e2 = e2;
        this.op = o;
    }

    @Override
    public IValue eval(MyIDictionary<String, IValue> table, MyIHeap heap) throws MyException {
        IValue v1, v2;
        v1 =  e1.eval(table, heap);
        if (op < 1 || op > 6)
            throw new MyException("invalid operation");
        if (v1.getType().equals(new IntType())) {
            v2 =  e2.eval(table, heap);
            if(v2.getType().equals(new IntType())){
                IntValue i1 = (IntValue) v1;
                IntValue i2 = (IntValue) v2;
                if(op == 1 && i1.getVal() < i2.getVal()) return new BoolValue(true);
                if(op == 2 && i1.getVal() <= i2.getVal()) return new BoolValue(true);
                if(op == 3 && i1.getVal() == i2.getVal()) return new BoolValue(true);
                if(op == 4 && i1.getVal() != i2.getVal()) return new BoolValue(true);
                if(op == 5 && i1.getVal() > i2.getVal()) return new BoolValue(true);
                if(op == 6 && i1.getVal() >= i2.getVal()) return new BoolValue(true);
                return new BoolValue(false);
            }
            else
                throw new MyException("second operand is not an integer");
        }
        else
            throw new MyException("first operand is not an integer");
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typ1, typ2;
        typ1 = e1.typecheck(typeEnv);
        typ2 = e2.typecheck(typeEnv);
        if(typ1.equals(new IntType())){
            if(typ2.equals(new IntType())){
                if(!(op < 1 || op > 6)){
                    return new IntType();
                }
                else
                    throw new MyException("invalid operation");
            }
            else
                throw new MyException("second operand is not an integer");
        }
        else
            throw new MyException("first operand is not an integer");
    }
}
