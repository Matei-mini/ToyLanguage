package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.expressions.IExp;
import model.expressions.RelationalExpression;
import model.expressions.VariableExpression;
import model.types.IType;
import model.types.IntType;

public class ForStmt implements IStmt{
    public IExp exp1;
    public IExp exp2;
    public IExp exp3;
    public IStmt stmt;
    public String var;

    public ForStmt(IExp exp1, IExp exp2, IExp exp3, IStmt stmt, String var){
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.exp3 = exp3;
        this.stmt = stmt;
        this.var = var;
    }

    @Override
    public String toString() {
        return "for("+this.exp1+", "+this.exp2+","+this.exp3+")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IStmt forStatement =
                new CompStmt(
                        new AssignStmt(var, exp1),
                        new WhileStmt(new RelationalExpression(new VariableExpression(var), exp2, 1),
                                new CompStmt(
                                        stmt,
                                        new AssignStmt(var, exp3)
                                )
                        )
                );

        state.getExeStack().push(forStatement);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typExp1 = this.exp1.typecheck(typeEnv);
        IType typExp2 = this.exp2.typecheck(typeEnv);
        IType typExp3 = this.exp3.typecheck(typeEnv);
        if(!typExp1.equals(new IntType()))
            throw new MyException("First exp type should be IntType.");
        if(!typExp2.equals(new IntType()))
            throw new MyException("Second exp type should be IntType.");
        if(!typExp3.equals(new IntType()))
            throw new MyException("Third exp type should be IntType.");
        return this.stmt.typecheck(typeEnv);
    }

    @Override
    public IStmt deepCopy() {
        return new ForStmt(this.exp1, this.exp2, this.exp3, this.stmt, this.var);
    }
}
