package model.statements;

import exception.MyException;
import model.PrgState;
import model.expressions.IExp;
import model.types.BoolType;
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
        IValue val = exp.eval(state.getSymTable());
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

        return state;
    }

    @Override
    public IStmt deepCopy() {
        return new IfStmt(exp, thenS.deepCopy(), elseS.deepCopy());
    }
}
