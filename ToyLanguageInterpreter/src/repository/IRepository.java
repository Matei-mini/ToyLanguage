package repository;

import exception.MyException;
import exception.RepoException;
import model.PrgState;

import java.io.FileNotFoundException;
import java.util.List;

public interface IRepository {
    void addPrgState(PrgState prgState);
    void logPrgState(PrgState prgState) throws FileNotFoundException, RepoException;
    void clearPrgState();
    List<PrgState> getPrgList();
    void setPrgList(List<PrgState> prgList);
}
