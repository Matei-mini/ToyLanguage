package model.adt;

import model.values.StringValue;

import java.io.BufferedReader;
import java.util.Map;

public interface MyIFileTable {
    boolean isDefined(StringValue key);
    void put(StringValue key, BufferedReader value);
    BufferedReader get(StringValue key);
    BufferedReader delete(StringValue key);
    Map<StringValue, BufferedReader> getContent();
}
