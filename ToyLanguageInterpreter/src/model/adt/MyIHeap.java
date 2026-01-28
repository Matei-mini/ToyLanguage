package model.adt;

import exception.MyException;
import model.values.IValue;

import java.util.Map;

public interface MyIHeap {
    int allocate(IValue value);
    void update(int address, IValue value)throws MyException;
    boolean isDefined(int address)throws MyException;
    IValue get(int address)throws MyException;

    Map<Integer, IValue> getContent();
    void setContent(Map<Integer, IValue> newContent);
}
