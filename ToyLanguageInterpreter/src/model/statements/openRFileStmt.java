package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIFileTable;
import model.adt.MyIHeap;
import model.expressions.IExp;
import model.types.IType;
import model.types.StringType;
import model.values.IValue;
import model.values.StringValue;

import java.io.*;

public class openRFileStmt implements IStmt {
    private IExp exp;

    public openRFileStmt(IExp ex){
        this.exp = ex;
    }

    public String toString() {
        return "openRFile("+this.exp.toString()+")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIFileTable fileTable = state.getFileTable();
        MyIHeap heap = state.getHeap();
        IValue file = exp.eval(symTbl, heap);

        if (!(file instanceof StringValue fileStr))
            throw new MyException("Invalid expression");

        if(fileTable.isDefined(fileStr))
            throw new MyException("File already defined");

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileStr.getValue()));
            fileTable.put(fileStr,br);
        } catch (FileNotFoundException e) {
            throw new MyException("File not found");
        }

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typ = exp.typecheck(typeEnv);
        if(typ.equals(new StringType())){
            return typeEnv;
        }
        else
            throw new MyException("Expression is not a StringValue");
    }

    @Override
    public IStmt deepCopy() {
        return new openRFileStmt(exp);
    }
}
