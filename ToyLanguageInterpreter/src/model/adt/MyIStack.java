package model.adt;

public interface MyIStack<T> {
    T pop();
    void push(T value);
    boolean isEmpty();

}
