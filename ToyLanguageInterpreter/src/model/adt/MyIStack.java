package model.adt;

import java.util.List;

public interface MyIStack<T> {
    T pop();
    void push(T value);
    boolean isEmpty();
    List<T> getReversed();

}
