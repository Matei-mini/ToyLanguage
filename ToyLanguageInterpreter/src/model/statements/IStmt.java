package model.statements;
import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.types.IType;

public interface IStmt {
    PrgState execute(PrgState state) throws MyException;
    IStmt deepCopy();
    MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType>typeEnv) throws MyException;
}
