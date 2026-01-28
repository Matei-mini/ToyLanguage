package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expressions.IExp;
import model.types.IType;
import model.types.RefType;
import model.values.IValue;
import model.values.RefValue;

public class HeapAllocStmt implements IStmt{
    private String varName;
    private IExp exp;

    public HeapAllocStmt(String vN, IExp e){
        this.varName = vN;
        this.exp = e;
    }

    public String toString(){return "new("+varName+","+exp.toString();}

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIHeap heap = state.getHeap();
        IValue eval = exp.eval(symTbl, heap);

        if(!(symTbl.isDefined(varName)))
            throw new MyException("The variable is not defined.");

        if(!(symTbl.get(varName).getType() instanceof RefType))
            throw new MyException("VarName is not RefType.");

        IType varType = symTbl.get(varName).getType();
        RefType refType = (RefType) varType;

        if(!(eval.getType().equals(refType.getInner())))
            throw new MyException("Eval value type is different than value type associated to var_name");
        int newAddress = heap.allocate(eval);
        symTbl.put(varName,new RefValue(newAddress, refType.getInner()));
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typVar = typeEnv.get(varName);
        IType typExp = exp.typecheck(typeEnv);
        if(typVar.equals(new RefType(typExp))){
            return typeEnv;
        }
        else
            throw new MyException("right hand side and left hand side have different types");
    }

    @Override
    public IStmt deepCopy() {
        return new HeapAllocStmt(varName, exp);
    }
}
