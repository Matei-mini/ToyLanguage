package model.statements;

import exception.MyException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.adt.MyIStack;
import model.expressions.IExp;
import model.expressions.RelationalExpression;
import model.expressions.ValueExpression;
import model.expressions.VariableExpression;
import model.types.BoolType;
import model.types.IType;
import model.types.IntType;
import model.types.RefType;
import model.values.BoolValue;
import model.values.IValue;
import model.values.IntValue;

public class WhileStmt implements IStmt{
    private IExp exp;
    private IStmt stmt;

    public WhileStmt(IExp e, IStmt s){
        this.exp = e;
        this.stmt = s;
    }

    public String toString(){return "while("+this.exp+")"+this.stmt;}

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, IValue> symTbl = state.getSymTable();
        MyIHeap heap = state.getHeap();
        MyIStack<IStmt> stk = state.getExeStack();
        IValue eval = exp.eval(symTbl, heap);

        if(!(eval.getType() instanceof BoolType))
            throw new MyException("The expression is not boolean");
        if(eval.equals(new BoolValue(true)))
        {
            stk.push(this);//this = current statement object
            stk.push(stmt);
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typExp = exp.typecheck(typeEnv);
        if(typExp.equals(new BoolType())){
            return stmt.typecheck(typeEnv);
        }
        else
            throw new MyException("The expression is not boolean");
    }

    @Override
    public IStmt deepCopy() {
        return new WhileStmt(exp, stmt);
    }
}

