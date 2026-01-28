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

import java.io.BufferedReader;
import java.io.IOException;

public class closeRFileStmt implements IStmt {
    private IExp exp;

    public closeRFileStmt(IExp exp) {
        this.exp = exp;
    }

    public String toString(){return "closeRFile("+this.exp.toString()+")";}

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIFileTable fileTable = state.getFileTable();
        MyIHeap heap = state.getHeap();
        IValue file = exp.eval(symTbl, heap);

        if (!(file instanceof StringValue fileStr))
            throw new MyException("Invalid expression");

        if(!fileTable.isDefined(fileStr))
            throw new MyException("File not defined");

        BufferedReader br = fileTable.get(fileStr);
        try {
            br.close();
            fileTable.delete(fileStr);
        } catch (IOException e) {
            throw new MyException("Error while closing file");
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
            throw new MyException("Expression is a StringType.");
    }

    @Override
    public IStmt deepCopy() {
        return new closeRFileStmt(exp);
    }
}
