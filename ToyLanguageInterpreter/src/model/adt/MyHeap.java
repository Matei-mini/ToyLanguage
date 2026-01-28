package model.adt;

import exception.MyException;
import model.values.IValue;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MyHeap implements MyIHeap{
    private Map<Integer, IValue> heap = new HashMap<>();
    private int freeAddress;

    public MyHeap(){
        this.heap = new HashMap<>();
        this.freeAddress = 1;
    }

    @Override
    public int allocate(IValue value) {
        int address = freeAddress;
        while(heap.containsKey(address))
            address++;
        heap.put(address, value);
        freeAddress = address + 1;
        return address;
    }

    @Override
    public boolean isDefined(int address) throws MyException {
        return heap.containsKey(address);
    }

    @Override
    public void update(int address, IValue value) throws MyException {
        if(address < 1)
            throw new MyException("Address should be grater than 0.");
        if(heap.containsKey(address))
            heap.put(address, value);
        else
            throw new MyException("Address not occupied.");
    }

    @Override
    public IValue get(int address) throws MyException {
        if(address < 1)
            throw new MyException("Address should be grater than 0.");
        if(heap.containsKey(address))
            return heap.get(address);
        throw new MyException("Address is empty.");
    }

    @Override
    public String toString() {
        return heap.toString();
    }

    @Override
    public Map<Integer, IValue> getContent() {
        return heap;
    }

    @Override
    public void setContent(Map<Integer, IValue> newContent) {
        this.heap = newContent;
    }

}
