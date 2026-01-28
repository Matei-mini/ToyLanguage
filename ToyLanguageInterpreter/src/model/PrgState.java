package model;

import exception.MyException;
import exception.RepoException;
import model.adt.*;
import model.statements.IStmt;
import model.values.IValue;

import java.io.FileNotFoundException;
import java.util.*;

public class PrgState {
    private MyIStack<IStmt> exeStack;
    private MyIDictionary<String, IValue> symTable;
    private MyIList<IValue> out;
    private MyIFileTable table;
    private MyIHeap heap;
    private static int nextId = 0;
    private int id;
    private IStmt originalProgram; //optional field, but good to have

    public PrgState(MyIStack<IStmt> stk,
                    MyIDictionary<String, IValue> symtbl,
                    MyIList<IValue> ot,
                    MyIFileTable tbl,
                    MyIHeap heap,
                    IStmt prg) {
        this.exeStack = stk;
        this.symTable = symtbl;
        this.out = ot;
        this.table = tbl;
        this.heap = heap;
        this.originalProgram = prg;
        this.id = PrgState.getNextId();
        this.exeStack.push(this.originalProgram);
    }

    public int getId(){
        return this.id;
    }

    public static synchronized int getNextId(){
        return ++nextId;
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

    public MyIHeap getHeap(){
        return heap;
    }


    @Override
    public String toString() {
        return "Id:"+this.id+"\n"+"ExeStack:"+
                exeStack.toString() + "\n" +"SymTable:"+
                symTable.toString() + "\n" +"Out:"+
                out.toString() + "\n" +"Table:"+
                table.toString() + "\n" +"Heap:"+
                heap.toString()+"\n";
    }

    public boolean isNotCompleted(){
        return !exeStack.isEmpty();
    }

    public PrgState executeOneStep() throws MyException{
        if(exeStack.isEmpty())
            throw new MyException("prgstate is empty");
        IStmt stmt = exeStack.pop();
        return stmt.execute(this);
    }
}
