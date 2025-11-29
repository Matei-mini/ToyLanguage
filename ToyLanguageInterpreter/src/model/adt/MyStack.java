package model.adt;

import java.util.Stack;

public class MyStack<T> implements MyIStack<T> {
    private final Stack<T> stack;

    public MyStack() {
        stack = new Stack<>();
    }

    @Override
    public T pop() {
        return stack.pop();
    }

    @Override
    public void push(T value) {
        stack.push(value);
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }


    @Override
    public String toString() {
        Stack<T> nw = new  Stack<>();
        Stack<T> nw2 = new Stack<>();
        nw.addAll(stack);
        while (!nw.isEmpty()){
            nw2.push(nw.pop());
        }
        return nw2.toString();
    }
}
