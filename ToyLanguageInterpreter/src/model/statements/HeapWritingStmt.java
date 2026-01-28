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

public class HeapWritingStmt implements IStmt{
    private String varName;
    private IExp exp;

    public HeapWritingStmt(String vN, IExp e){
        this.varName = vN;
        this.exp = e;
    }

    public String toString() {
        return "wH("+varName+","+exp.toString()+")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIHeap heap = state.getHeap();

        if(!symTbl.isDefined(varName))
            throw new MyException("VarName not defined.");
        if(!(symTbl.get(varName).getType() instanceof RefType))
            throw new MyException("VarName type should be RefType.");

        IValue val = symTbl.get(varName);
        RefValue refValue = (RefValue) val;
        int address = refValue.getAddr();

        if(!heap.isDefined(address))
            throw new MyException("Address not defined in heap.");

        IType inner = refValue.getLocationType();
        IValue eval = exp.eval(symTbl, heap);

        if(!eval.getType().equals(inner))
            throw new MyException("LocationType does not mach varName type.");

        heap.update(address, eval);

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
            throw new MyException("right hand side and left hand side have different types ");
    }

    @Override
    public IStmt deepCopy() {
        return new HeapWritingStmt(varName, exp);
    }
}
