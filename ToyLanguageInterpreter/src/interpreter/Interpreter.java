package interpreter;

import controller.Controller;
import exception.MyException;
import model.PrgState;
import model.adt.*;
import model.expressions.*;
import model.expressions.HeapReadingExpression;
import model.statements.*;
import model.types.*;
import model.values.BoolValue;
import model.values.IValue;
import model.values.IntValue;
import model.values.StringValue;
import repository.IRepository;
import repository.Repository;
import view.command.ExitCommand;
import view.command.RunExample;
import view.TextMenu;

public class Interpreter {
    public static void main(String[] args) {

        /* ------------ Example 1 ------------
           int v; v=2; print(v)
        */
        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new BoolType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExpression(new IntValue(2))),
                        new PrintStmt(new VariableExpression("v"))
                )
        );

        /* ------------ Example 2 ------------
           int a; int b; a = 2 + 3*5; b = a + 1; print(b)
        */
        IExp two = new ValueExpression(new IntValue(2));
        IExp three = new ValueExpression(new IntValue(3));
        IExp five = new ValueExpression(new IntValue(5));
        IExp one = new ValueExpression(new IntValue(1));

        IExp mul = new ArithmeticExpression(three, five, 3);     // 3*5
        IExp sum = new ArithmeticExpression(two, mul, 1);        // 2 + (3*5)
        IExp aPlusOne = new ArithmeticExpression(new VariableExpression("a"), one, 1); // a+1

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

        /* ------------ Example 3 ------------
           bool a; int v; a=true;
           if a then v=2 else v=3; print(v)
        */
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

        /* ------------ Example 4 (2.9) ------------
           string varf;
           varf = "test.in";
           openRFile(varf);
           int varc;
           readFile(varf, varc); print(varc);
           readFile(varf, varc); print(varc);
           closeRFile(varf);
        */
        IStmt declVarf = new VarDeclStmt("varf", new StringType());
        IStmt assignVarf = new AssignStmt(
                "varf",
                new ValueExpression(new StringValue("test.in"))
        );
        IStmt open = new openRFileStmt(new VariableExpression("varf"));
        IStmt declVarc = new VarDeclStmt("varc", new IntType());
        IStmt read1 = new ReadFileStmt(new VariableExpression("varf"), "varc");
        IStmt print1 = new PrintStmt(new VariableExpression("varc"));
        IStmt read2 = new ReadFileStmt(new VariableExpression("varf"), "varc");
        IStmt print2 = new PrintStmt(new VariableExpression("varc"));
        IStmt close = new closeRFileStmt(new VariableExpression("varf"));

        IStmt ex4 = new CompStmt(
                declVarf,
                new CompStmt(
                        assignVarf,
                        new CompStmt(
                                open,
                                new CompStmt(
                                        declVarc,
                                        new CompStmt(
                                                new CompStmt(read1, print1),
                                                new CompStmt(read2, new CompStmt(print2, close))
                                        )
                                )
                        )
                )
        );

        /* ------------ Example 5 (heap test) ------------
           Ref int v;
           new(v,20);
           Ref Ref int a;
           new(a,v);
           new(v,30);
           print(rH(rH(a)));
        */

        IStmt ex5 =
                new CompStmt(
                        new VarDeclStmt("v", new RefType(new IntType())),
                        new CompStmt(
                                new HeapAllocStmt("v", new ValueExpression(new IntValue(20))),
                                new CompStmt(
                                        new VarDeclStmt("a",
                                                new RefType(new RefType(new IntType()))),
                                        new CompStmt(
                                                new HeapAllocStmt("a", new VariableExpression("v")),
                                                new CompStmt(
                                                        new HeapAllocStmt("v",
                                                                new ValueExpression(new IntValue(30))),
                                                        new PrintStmt(
                                                                new HeapReadingExpression(
                                                                        new HeapReadingExpression(
                                                                                new VariableExpression("a")
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );

        /* ------------ Example 6 (Lab 8 – fork) ------------
   int v; Ref int a;
   v = 10; new(a,22);
   fork(wH(a,30); v=32; print(v); print(rH(a)));
   print(v); print(rH(a))
*/

        IStmt ex6 =
                new CompStmt(
                        new VarDeclStmt("v", new IntType()),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new IntType())),
                                new CompStmt(
                                        new AssignStmt("v",
                                                new ValueExpression(new IntValue(10))),
                                        new CompStmt(
                                                new HeapAllocStmt("a",
                                                        new ValueExpression(new IntValue(22))),
                                                new CompStmt(
                                                        new ForkStmt(
                                                                new CompStmt(
                                                                        new HeapWritingStmt("a",
                                                                                new ValueExpression(new IntValue(30))),
                                                                        new CompStmt(
                                                                                new AssignStmt("v",
                                                                                        new ValueExpression(new IntValue(32))),
                                                                                new CompStmt(
                                                                                        new PrintStmt(
                                                                                                new VariableExpression("v")),
                                                                                        new PrintStmt(
                                                                                                new HeapReadingExpression(
                                                                                                        new VariableExpression("a")))
                                                                                )
                                                                        )
                                                                )
                                                        ),
                                                        new CompStmt(
                                                                new PrintStmt(
                                                                        new VariableExpression("v")),
                                                                new CompStmt(new NopStmt(), new PrintStmt(
                                                                        new HeapReadingExpression(
                                                                                new VariableExpression("a"))))
                                                        )
                                                )
                                        )
                                )
                        )
                );


        // ---------- Build Program States, Repositories, Controllers ----------

        // ex1
        Controller ctr1 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex1.typecheck(typeEnv);
            MyIStack<IStmt> stk1 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl1 = new MyDictionary<>();
            MyIList<IValue> out1 = new MyList<>();
            MyIFileTable fileTable1 = new MyFileTable();
            MyIHeap heap1 = new MyHeap();
            stk1.push(ex1);
            PrgState prg1 = new PrgState(stk1, symTbl1, out1, fileTable1, heap1, ex1);
            IRepository repo1 = new Repository("log1.txt");
            repo1.addPrgState(prg1);
            ctr1 = new Controller(repo1);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex1: " + e.getMessage());
        }

        // ex2
        Controller ctr2 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex2.typecheck(typeEnv);
            MyIStack<IStmt> stk2 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl2 = new MyDictionary<>();
            MyIList<IValue> out2 = new MyList<>();
            MyIFileTable fileTable2 = new MyFileTable();
            MyIHeap heap2 = new MyHeap();
            //stk2.push(ex2);
            PrgState prg2 = new PrgState(stk2, symTbl2, out2, fileTable2, heap2, ex2);
            IRepository repo2 = new Repository("log2.txt");
            repo2.addPrgState(prg2);
            ctr2 = new Controller(repo2);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex2: " + e.getMessage());
        }

        // ex3
        Controller ctr3 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex3.typecheck(typeEnv);
            MyIStack<IStmt> stk3 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl3 = new MyDictionary<>();
            MyIList<IValue> out3 = new MyList<>();
            MyIFileTable fileTable3 = new MyFileTable();
            MyIHeap heap3 = new MyHeap();
            //stk3.push(ex3);
            PrgState prg3 = new PrgState(stk3, symTbl3, out3, fileTable3, heap3, ex3);
            IRepository repo3 = new Repository("log3.txt");
            repo3.addPrgState(prg3);
            ctr3 = new Controller(repo3);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex3: " + e.getMessage());
        }


        // ex4 (file I/O)
        Controller ctr4 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex4.typecheck(typeEnv);
            MyIStack<IStmt> stk4 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl4 = new MyDictionary<>();
            MyIList<IValue> out4 = new MyList<>();
            MyIFileTable fileTable4 = new MyFileTable();
            MyIHeap heap4 = new MyHeap();
            //stk4.push(ex4);
            PrgState prg4 = new PrgState(stk4, symTbl4, out4, fileTable4, heap4, ex4);
            IRepository repo4 = new Repository("log4.txt");
            repo4.addPrgState(prg4);
            ctr4 = new Controller(repo4);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex4: " + e.getMessage());
        }

        // ex5 (heap + GC test)
        Controller ctr5 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex5.typecheck(typeEnv);
            MyIStack<IStmt> stk5 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl5 = new MyDictionary<>();
            MyIList<IValue> out5 = new MyList<>();
            MyIFileTable fileTable5 = new MyFileTable();
            MyIHeap heap5 = new MyHeap();
            //stk5.push(ex5);
            PrgState prg5 = new PrgState(stk5, symTbl5, out5, fileTable5, heap5, ex5);
            IRepository repo5 = new Repository("log5.txt");
            repo5.addPrgState(prg5);
            ctr5 = new Controller(repo5);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex5: " + e.getMessage());
        }

        // ex6 (Lab 8 – fork / concurrency)
        Controller ctr6 = null;
        try {
            MyIDictionary<String, IType> typeEnv = new MyDictionary<>();
            ex6.typecheck(typeEnv);

            MyIStack<IStmt> stk6 = new MyStack<>();
            MyIDictionary<String, IValue> symTbl6 = new MyDictionary<>();
            MyIList<IValue> out6 = new MyList<>();
            MyIFileTable fileTable6 = new MyFileTable();
            MyIHeap heap6 = new MyHeap();
            PrgState prg6 = new PrgState(
                    stk6,
                    symTbl6,
                    out6,
                    fileTable6,
                    heap6,
                    ex6
            );

            IRepository repo6 = new Repository("log6.txt");
            repo6.addPrgState(prg6);

            ctr6 = new Controller(repo6);
        } catch (MyException e) {
            System.out.println("Typecheck error in ex6: " + e.getMessage());
        }


        // ---------- Build Text Menu ----------

        TextMenu menu = new TextMenu();
        menu.addCommand(new ExitCommand("0", "exit"));
        if (ctr1 != null) {
            menu.addCommand(new RunExample("1", ex1.toString(), ctr1));
        }
        if (ctr2 != null) {
            menu.addCommand(new RunExample("2", ex2.toString(), ctr2));
        }
        if (ctr3 != null) {
            menu.addCommand(new RunExample("3", ex3.toString(), ctr3));
        }
        if (ctr4 != null) {
            menu.addCommand(new RunExample("4", ex4.toString(), ctr4));
        }
        if (ctr5 != null) {
            menu.addCommand(new RunExample("5", ex5.toString(), ctr5));
        }
        if (ctr6 != null) {
            menu.addCommand(new RunExample("6", ex6.toString(), ctr6));
        }

        menu.show();
    }
}
