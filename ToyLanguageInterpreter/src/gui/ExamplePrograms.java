package gui;

import exception.MyException;
import model.adt.MyDictionary;
import model.adt.MyIDictionary;
import model.expressions.*;
import model.statements.*;
import model.types.*;
import model.values.BoolValue;
import model.values.IntValue;
import model.values.StringValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Central place where we build the example programs shown in the JavaFX program selector.
 */
public final class ExamplePrograms {
    private ExamplePrograms() {}

    public static List<IStmt> buildAll() {
        List<IStmt> examples = new ArrayList<>();

        // Example 1: int v; v=2; print(v)
        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExpression(new IntValue(2))),
                        new PrintStmt(new VariableExpression("v"))
                )
        );

        // Example 2: int a; int b; a=2+3*5; b=a+1; print(b)
        IExp two = new ValueExpression(new IntValue(2));
        IExp three = new ValueExpression(new IntValue(3));
        IExp five = new ValueExpression(new IntValue(5));
        IExp one = new ValueExpression(new IntValue(1));
        IExp mul = new ArithmeticExpression(three, five, 3);
        IExp sum = new ArithmeticExpression(two, mul, 1);
        IExp aPlusOne = new ArithmeticExpression(new VariableExpression("a"), one, 1);
        IStmt ex2 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", sum),
                                new CompStmt(
                                        new AssignStmt("b", aPlusOne),
                                        new PrintStmt(new VariableExpression("b"))
                                )
                        )
                )
        );

        // Example 3: bool a; int v; a=true; if a then v=2 else v=3; print(v)
        IStmt ex3 = new CompStmt(
                new VarDeclStmt("a", new BoolType()),
                new CompStmt(
                        new VarDeclStmt("v", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ValueExpression(new BoolValue(true))),
                                new CompStmt(
                                        new IfStmt(
                                                new VariableExpression("a"),
                                                new AssignStmt("v", new ValueExpression(new IntValue(2))),
                                                new AssignStmt("v", new ValueExpression(new IntValue(3)))
                                        ),
                                        new PrintStmt(new VariableExpression("v"))
                                )
                        )
                )
        );

        // Example 4 (file I/O): see Lab 5
        IStmt ex4 = new CompStmt(
                new VarDeclStmt("varf", new StringType()),
                new CompStmt(
                        new AssignStmt("varf", new ValueExpression(new StringValue("test.in"))),
                        new CompStmt(
                                new openRFileStmt(new VariableExpression("varf")),
                                new CompStmt(
                                        new VarDeclStmt("varc", new IntType()),
                                        new CompStmt(
                                                new CompStmt(
                                                        new ReadFileStmt(new VariableExpression("varf"), "varc"),
                                                        new PrintStmt(new VariableExpression("varc"))
                                                ),
                                                new CompStmt(
                                                        new CompStmt(
                                                                new ReadFileStmt(new VariableExpression("varf"), "varc"),
                                                                new PrintStmt(new VariableExpression("varc"))
                                                        ),
                                                        new closeRFileStmt(new VariableExpression("varf"))
                                                )
                                        )
                                )
                        )
                )
        );

        // Example 5 (heap): Ref int v; new(v,20); Ref Ref int a; new(a,v); new(v,30); print(rH(rH(a)))
        IStmt ex5 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new HeapAllocStmt("v", new ValueExpression(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new HeapAllocStmt("a", new VariableExpression("v")),
                                        new CompStmt(
                                                new HeapAllocStmt("v", new ValueExpression(new IntValue(30))),
                                                new PrintStmt(
                                                        new HeapReadingExpression(
                                                                new HeapReadingExpression(new VariableExpression("a"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // Example 6 (fork / concurrency) from Lab 8
        IStmt ex6 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new VarDeclStmt("a", new RefType(new IntType())),
                        new CompStmt(
                                new AssignStmt("v", new ValueExpression(new IntValue(10))),
                                new CompStmt(
                                        new HeapAllocStmt("a", new ValueExpression(new IntValue(22))),
                                        new CompStmt(
                                                new ForkStmt(
                                                        new CompStmt(
                                                                new HeapWritingStmt("a", new ValueExpression(new IntValue(30))),
                                                                new CompStmt(
                                                                        new AssignStmt("v", new ValueExpression(new IntValue(32))),
                                                                        new CompStmt(
                                                                                new PrintStmt(new VariableExpression("v")),
                                                                                new PrintStmt(new HeapReadingExpression(new VariableExpression("a")))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompStmt(
                                                        new PrintStmt(new VariableExpression("v")),
                                                        new PrintStmt(new HeapReadingExpression(new VariableExpression("a")))
                                                )
                                        )
                                )
                        )
                )
        );

        IStmt ex7 = new CompStmt(
                new VarDeclStmt("a", new RefType(new IntType())),
                new CompStmt(new HeapAllocStmt("a", new ValueExpression(new IntValue(20))),
                        new CompStmt(new CompStmt(new VarDeclStmt("v", new IntType()),
                                new ForStmt(
                                        new ValueExpression(new IntValue(0)),
                                        new ValueExpression(new IntValue(3)),
                                        new ArithmeticExpression(new VariableExpression("v"), new ValueExpression(new IntValue(1)),1),
                                        new ForkStmt(
                                                new CompStmt(
                                                        new PrintStmt(new VariableExpression("v")),
                                                        new AssignStmt("v",
                                                                new ArithmeticExpression(
                                                                        new VariableExpression("v"),
                                                                        new HeapReadingExpression((new VariableExpression("a"))),
                                                                        3
                                                                )
                                                        )
                                                )
                                        ),
                                        "v"
                                )),
                                new PrintStmt(new HeapReadingExpression(new VariableExpression("a")))
                        )

                )
        );

        // Keep only programs that pass typecheck (so GUI doesn't start broken programs).
        for (IStmt prg : List.of(ex1, ex2, ex3, ex4, ex5, ex6, ex7)) {
            try {
                MyIDictionary<String, IType> env = new MyDictionary<>();
                prg.typecheck(env);
                examples.add(prg);
            } catch (MyException ignored) {
                // skip invalid program
            }
        }

        return examples;
    }
}
