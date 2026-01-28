package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expressions.IExp;
import model.types.BoolType;
import model.types.IType;
import model.values.BoolValue;
import model.values.IValue;

public class IfStmt implements IStmt {
    private IExp exp;
    private IStmt thenS;
    private IStmt elseS;

    public IfStmt(IExp exp, IStmt thenS, IStmt elseS) {
        this.exp = exp;
        this.thenS = thenS;
        this.elseS = elseS;
    }

    public String toString() {
        return "if(" + exp + "){" + thenS + "}else{" + elseS + "}";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIHeap heap = state.getHeap();
        IValue val = exp.eval(state.getSymTable(), heap);
        if (val.getType().equals(new BoolType())) {
            BoolValue b = (BoolValue) val;
            if (b.getVal() == true) {
                state.getExeStack().push(thenS);
            } else {
                state.getExeStack().push(elseS);
            }
        } else {
            throw new MyException("The condition is not boolean type.");
        }

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typExp = exp.typecheck(typeEnv);
        MyIDictionary<String, IType> typEnv1, typEnv2;
        if(typExp.equals(new BoolType())){
            typEnv1 = thenS.typecheck(typeEnv);
            typEnv2 = elseS.typecheck(typEnv1);
            return typEnv2;
        }
        else
            throw new MyException("The condition of IF has not the type bool");
    }

    @Override
    public IStmt deepCopy() {
        return new IfStmt(exp, thenS.deepCopy(), elseS.deepCopy());
    }
}
