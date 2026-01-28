package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.types.IType;
import model.types.IntType;
import model.values.BoolValue;
import model.values.IValue;
import model.values.IntValue;

public class VarDeclStmt implements IStmt{
    private String name;
    private IType typ;

    public VarDeclStmt(String name, IType typ){
        this.name = name;
        this.typ = typ;
    }

    public String toString(){return typ.toString()+" "+name;}

    @Override
    public PrgState execute(PrgState state) throws MyException{
        MyIDictionary<String, IValue> symTbl = state.getSymTable();

        if(symTbl.isDefined(name))
            throw new MyException("variable"+name+"already declared");
        else
            symTbl.put(name, typ.defaultValue());

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        typeEnv.put(name, typ);
        return typeEnv;
    }

    @Override
    public IStmt deepCopy() {
        return new VarDeclStmt(name, typ);
    }
}
