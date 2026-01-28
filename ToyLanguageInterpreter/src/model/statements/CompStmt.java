package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIStack;
import model.types.IType;

public class CompStmt implements IStmt{
    private IStmt first;
    private IStmt snd;

    public CompStmt(IStmt first, IStmt snd) {
        this.first = first;
        this.snd = snd;
    }
    //:)
    public String toString() {
        return "(" + first.toString() + ";" + snd.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stk = state.getExeStack();
        stk.push(snd);
        stk.push(first);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        MyIDictionary<String, IType> typEnv1 = first.typecheck(typeEnv);
        MyIDictionary<String, IType> typEnv2 = snd.typecheck(typEnv1);
        return typEnv2;
    }

    @Override
    public IStmt deepCopy(){
        return new CompStmt(first.deepCopy(),snd.deepCopy());
    }
}