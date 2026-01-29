package model.adt;

import java.util.List;

public interface MyIList<T> {
    void add(T value);
    List<T> getContent();
}
