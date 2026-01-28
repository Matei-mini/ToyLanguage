package controller;

import exception.MyException;
import exception.RepoException;
import model.PrgState;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.adt.MyIStack;
import model.statements.IStmt;
import model.values.IValue;
import model.values.RefValue;
import repository.IRepository;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller implements IController {
    private IRepository repository;
    private boolean displayFlag;
    private ExecutorService executor;

    public Controller(IRepository repository){
        this.repository = repository;
        this.executor = Executors.newFixedThreadPool(2);
    }


    private List<Integer> getAddrFromSymTable(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());
    }

    private Map<Integer, IValue> safeGarbageCollector(List<Integer> symTableAddr,
                                                      Map<Integer, IValue> heap) {

        Set<Integer> reachable = new HashSet<>(symTableAddr);
        boolean changed = true;

        while (changed) {
            changed = false;

            List<Integer> newAddrs = heap.entrySet().stream()
                    .filter(e -> reachable.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .filter(v -> v instanceof RefValue)
                    .map(v -> ((RefValue) v).getAddr())
                    .filter(addr -> !reachable.contains(addr))
                    .collect(Collectors.toList());

            if (!newAddrs.isEmpty()) {
                reachable.addAll(newAddrs);
                changed = true;
            }
        }


        return heap.entrySet().stream()
                .filter(e -> reachable.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<PrgState> removeCompletedPrg(List<PrgState> programList){
        return programList.stream()
                .filter(PrgState::isNotCompleted)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void oneStepForAllPrg(List<PrgState> programList) {

        // log BEFORE execution
       /* programList.forEach(p -> {
            try {
                repository.logPrgState(p);
            } catch (FileNotFoundException | RepoException e) {
                throw new RuntimeException(e);
            }
        });*/

        // prepare callables (one step per PrgState)
        List<Callable<PrgState>> callables =
                programList.stream()
                        .map(p -> (Callable<PrgState>) p::executeOneStep)
                        .collect(Collectors.toList());

        try {
            // execute all steps concurrently
            List<PrgState> newStates =
                    executor.invokeAll(callables)
                            .stream()
                            .map(future -> {
                                try {
                                    return future.get();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .filter(Objects::nonNull) // keep only fork-created states
                            .collect(Collectors.toList());

            // IMPORTANT: do NOT modify programList directly
            List<PrgState> updatedList = new ArrayList<>(programList);
            updatedList.addAll(newStates);

            // log AFTER execution
            updatedList.forEach(p -> {
                try {
                    repository.logPrgState(p);
                } catch (FileNotFoundException | RepoException e) {
                    throw new RuntimeException(e);
                }
            });

            // replace repository list ONCE
            repository.setPrgList(updatedList);

        } catch (InterruptedException e) {
            throw new RuntimeException("Problem with the executor", e);
        }
    }


    @Override
    public void allSteps() {
        executor = Executors.newFixedThreadPool(2);

        List<PrgState> programList = removeCompletedPrg(repository.getPrgList());

        programList.forEach(p -> {
            try {
                repository.logPrgState(p);
            } catch (FileNotFoundException | RepoException e) {
                throw new RuntimeException(e);
            }
        });

        while (!programList.isEmpty()) {

            oneStepForAllPrg(programList);

            programList.get(0).getHeap().setContent(
                    safeGarbageCollector(
                            programList.stream()
                                    .flatMap(p -> p.getSymTable().getContent().values().stream())
                                    .filter(v -> v instanceof RefValue)
                                    .map(v -> ((RefValue) v).getAddr())
                                    .toList(),
                            programList.get(0).getHeap().getContent()
                    )
            );

            programList = removeCompletedPrg(repository.getPrgList());
        }

        executor.shutdownNow();
        repository.setPrgList(programList);
    }


    @Override
    public void setDisplay(boolean display) {
        this.displayFlag = display;
    }

    @Override
    public boolean getDisplay() {
        return displayFlag;
    }

    @Override
    public void addPrgState(PrgState prgState) {
        repository.addPrgState(prgState);
    }

    @Override
    public void clearPrgState() {
        repository.clearPrgState();
    }

    public IRepository getRepository(){
        return repository;
    }
}
