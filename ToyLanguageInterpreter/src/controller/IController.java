package controller;

import exception.MyException;
import exception.RepoException;
import model.PrgState;

import java.io.FileNotFoundException;

public interface IController {
    void allSteps( );
    void setDisplay(boolean display);
    boolean getDisplay();
    void addPrgState(PrgState prgState);
    void clearPrgState();
}
