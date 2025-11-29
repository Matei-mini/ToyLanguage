package model;

import model.adt.MyIDictionary;
import model.adt.MyIFileTable;
import model.adt.MyIList;
import model.adt.MyIStack;
import model.statements.IStmt;
import model.values.IValue;
import model.values.StringValue;

import java.io.BufferedWriter;

public class PrgState {
    private MyIStack<IStmt> exeStack;
    private MyIDictionary<String, IValue> symTable;
    private MyIList<IValue> out;
    private MyIFileTable table;
    private IStmt originalProgram; //optional field, but good to have

    public PrgState(MyIStack<IStmt> stk, MyIDictionary<String, IValue> symtbl, MyIList<IValue> ot,MyIFileTable tbl, IStmt prg){
        this.exeStack = stk;
        this.symTable = symtbl;
        this.out = ot;
        this.table = tbl;
        this.originalProgram = prg;
    }

    public MyIList<IValue> getOut() {
        return out;
    }

    public MyIDictionary<String, IValue> getSymTable() {
        return symTable;
    }

    public MyIStack<IStmt> getExeStack() {
        return exeStack;
    }

    public MyIFileTable getFileTable(){
        return table;
    }

    @Override
    public String toString() {
        return exeStack.toString()+" "+ symTable.toString()+ " " + out.toString()+" "+table.toString();
    }
}
