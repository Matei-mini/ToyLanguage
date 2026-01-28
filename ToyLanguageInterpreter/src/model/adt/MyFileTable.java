package model.adt;

import model.values.StringValue;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MyFileTable implements MyIFileTable{
    private final Map<StringValue, BufferedReader> table;

    public MyFileTable(){
        this.table = new HashMap<>();
    }

    @Override
    public BufferedReader get(StringValue key) {
        return this.table.get(key);
    }

    @Override
    public void put(StringValue key, BufferedReader value) {
        this.table.put(key, value);
    }

    @Override
    public boolean isDefined(StringValue key) {
        return this.table.containsKey(key);
    }

    @Override
    public String toString() {
        return table.toString();
    }

    @Override
    public BufferedReader delete(StringValue key) {
        return this.table.remove(key);
    }
}
