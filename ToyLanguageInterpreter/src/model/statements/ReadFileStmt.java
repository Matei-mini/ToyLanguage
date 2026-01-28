package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIFileTable;
import model.adt.MyIHeap;
import model.expressions.IExp;
import model.types.IType;
import model.types.IntType;
import model.types.StringType;
import model.values.IValue;
import model.values.IntValue;
import model.values.StringValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class ReadFileStmt implements IStmt{
    private IExp exp;
    private String var_name;

    public ReadFileStmt(IExp ex, String v_n){
        this.exp = ex;
        this.var_name = v_n;
    }

    public String toString(){return "readFile("+this.exp.toString()+","+this.var_name+")";}

   @Override
   public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String , IValue> symTbl = state.getSymTable();
        MyIFileTable fileTable = state.getFileTable();
        MyIHeap heap = state.getHeap();
        IValue fileName = exp.eval(symTbl, heap);

        if(symTbl.isDefined(var_name))
            if(!(symTbl.get(var_name).getType() instanceof IntType))
                throw new MyException("Variable is not int.");
            else
                if(!(fileName instanceof StringValue fileNameStr))
                    throw new MyException("Expression error.");
                else {
                    if (!fileTable.isDefined(fileNameStr))
                        throw new MyException("File name not found.");
                    else{
                        BufferedReader br = fileTable.get(fileNameStr);
                        try {
                            String line = br.readLine();
                            if(line == null){
                                line ="0";
                            }
                            int lineNum = Integer.parseInt(line);
                            symTbl.put(var_name, new IntValue(lineNum));
                        }
                        catch (IOException e) {
                            throw new MyException("Error reading file.");
                        }
                        catch (NumberFormatException e) {
                            throw new MyException("Error, line can only be number.");
                        }
                    }
                }
        else
            throw new MyException("Variable is not found.");
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typVar = typeEnv.get(var_name);
        IType typExp = exp.typecheck(typeEnv);
        if(typVar.equals(new IntType())){
            if(typExp.equals(new StringType())){
                return typeEnv;
            }
            else
                throw new MyException("the expression is not a StringType");
        }
        else
            throw new MyException("the variable is not an int type");
    }

    @Override
    public IStmt deepCopy() {
        return new ReadFileStmt(exp, var_name);
    }
}
