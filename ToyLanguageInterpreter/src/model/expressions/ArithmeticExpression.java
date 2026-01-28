package model.expressions;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.IType;
import model.types.IntType;
import model.values.IValue;
import model.values.IntValue;

public class ArithmeticExpression implements IExp{
    private IExp e1;
    private IExp e2;
    private int op;

    public ArithmeticExpression(IExp e1, IExp e2, int op){
        this.e1 = e1;
        this.e2 = e2;
        this.op = op;
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typ1, typ2;
        typ1 = e1.typecheck(typeEnv);
        typ2 = e2.typecheck(typeEnv);
        if(typ1.equals(new IntType())){
            if(typ2.equals(new IntType()))
                return new IntType();
            else
                throw new MyException("second operand is not intType");
        }
        else
            throw new MyException("first operand is not intType");
    }

    @Override
    public IValue eval(MyIDictionary<String, IValue>tbl, MyIHeap heap) throws MyException{
        IValue v1,v2;
        v1 = e1.eval(tbl,heap);
        if(v1.getType().equals(new IntType())){
            v2 = e2.eval(tbl, heap);
            if(v2.getType().equals(new IntType())){
                IntValue i1 = (IntValue)v1;
                IntValue i2 = (IntValue)v2;
                int n1, n2;
                n1 = i1.getVal();
                n2 = i2.getVal();
                if(op == 1) return new IntValue(n1+n2);
                if(op == 2) return new IntValue(n1-n2);
                if(op == 3) return new IntValue(n1*n2);
                if(op == 4)
                    if(n2 == 0)
                        throw new MyException("division by 0");
                    else
                        return new IntValue(n1/n2);
            }
            else
                throw new MyException("second operand is not an integer");
        }
        else
            throw new MyException("first operand is not an integer");
        throw new MyException("op is not good");
    }

}
