package repository;


import exception.RepoException;
import model.PrgState;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository{
    private List<PrgState> prgStates;
    private String fileName;

    public Repository(String fileName){
        //List<PrgState> prgStates = new ArrayList<>();
        this.prgStates = new ArrayList<>();
        this.fileName = fileName;

    }

    @Override
    public void addPrgState(PrgState prgState) {
        prgStates.add(prgState);
    }

    @Override
    public void logPrgState(PrgState prgState) throws RepoException, FileNotFoundException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))) {
            bw.write(prgStates.toString());
            bw.write("\n");
        }catch (IOException e){
            throw new RepoException("Scrierea nu a fost scrisa cu succes.");
        }

    }

    @Override
    public void clearPrgState() {
        prgStates.clear();
    }

    @Override
    public List<PrgState> getPrgList() {
        return this.prgStates;
    }

    @Override
    public void setPrgList(List<PrgState> prgList) {
        this.prgStates = prgList;
    }
}
