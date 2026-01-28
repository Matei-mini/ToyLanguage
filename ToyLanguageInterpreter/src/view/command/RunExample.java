package view.command;

import controller.Controller;
import exception.MyException;
import exception.RepoException;

import java.io.FileNotFoundException;

public class RunExample extends Command {
    private Controller ctr;
    public RunExample(String key, String desc,Controller ctr){
        super(key, desc);
        this.ctr=ctr;
    }
    @Override
    public void execute() {
            ctr.allSteps();
    }
}
