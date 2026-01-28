package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.adt.MyIStack;
import model.expressions.IExp;
import model.types.IType;
import model.values.IValue;



public class AssignStmt implements IStmt {
    private String id;
    private IExp exp;

    public AssignStmt(String id, IExp exp){
        this.id = id;
        this.exp = exp;
    }

    public String toString(){return id+"="+exp.toString();}

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stk = state.getExeStack();
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIHeap heap = state.getHeap();

        if(symTbl.isDefined(id)){
            IValue val = exp.eval(symTbl, heap);
            IType typeId = (symTbl.get(id)).getType();
            if(val.getType().equals(typeId))
                symTbl.put(id, val);
            else
                throw new MyException("declared  type of variable"+id+"and type of the assigned expression do not mach.");
        }
        else
            throw new MyException("the used variable"+id+"was not declared before");
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeVar = typeEnv.get(id);
        IType typExp = exp.typecheck(typeEnv);
        if(typeVar.equals(typExp))
            return typeEnv;
        else
            throw new MyException("Assignment: right hand side and left hand side have different types ");
    }

    @Override
    public IStmt deepCopy() {
        return new AssignStmt(id, exp);
    }
}
