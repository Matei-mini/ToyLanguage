package gui;

import controller.Controller;
import exception.MyException;
import model.PrgState;
import model.adt.*;
import model.statements.IStmt;
import model.values.IValue;
import model.values.StringValue;
import repository.IRepository;
import repository.Repository;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GuiApp extends Application {

    // ---- UI state
    private Controller controller;
    private PrgState selectedPrgState;

    // ---- widgets (main window)
    private TextField nrPrgStatesField;
    private TableView<HeapEntry> heapTable;
    private ListView<String> outList;
    private ListView<String> fileTableList;
    private ListView<Integer> prgStateIdsList;
    private TableView<SymEntry> symTable;
    private ListView<String> exeStackList;
    private Button runOneStepBtn;

    private boolean programFinished = false;

    @Override
    public void start(Stage primaryStage) {
        showProgramSelector(primaryStage);
    }

    // -------------------------- Program Selector Window --------------------------

    private void showProgramSelector(Stage stage) {
        List<IStmt> programs = ExamplePrograms.buildAll();

        ListView<IStmt> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(programs));
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(IStmt item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        Button startBtn = new Button("Start selected program");
        startBtn.setDefaultButton(true);
        startBtn.setOnAction(e -> {
            IStmt selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Please select a program first.");
                return;
            }
            try {
                this.controller = buildControllerForProgram(selected);
                showMainWindow(new Stage());
                stage.close();
            } catch (Exception ex) {
                showError("Could not start program: " + ex.getMessage());
            }
        });

        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                startBtn.fire();
            }
        });

        VBox root = new VBox(10,
                new Label("Select a program to execute:"),
                listView,
                startBtn
        );
        root.setPadding(new Insets(12));
        root.setPrefSize(900, 500);

        stage.setTitle("ToyLanguage - Program Selector");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private Controller buildControllerForProgram(IStmt program) throws MyException {
        // typecheck happens in ExamplePrograms, but we keep it safe here too.
        program.typecheck(new MyDictionary<>());

        MyIStack<IStmt> stk = new MyStack<>();
        MyIDictionary<String, IValue> symTbl = new MyDictionary<>();
        MyIList<IValue> out = new MyList<>();
        MyIFileTable fileTable = new MyFileTable();
        MyIHeap heap = new MyHeap();

        PrgState prgState = new PrgState(stk, symTbl, out, fileTable, heap, program);

        // Use a distinct log file per run.
        String logName = "gui-log.txt";
        IRepository repo = new Repository(logName);
        repo.addPrgState(prgState);
        return new Controller(repo);
    }

    // ------------------------------- Main Window -------------------------------

    private void showMainWindow(Stage stage) {
        // top: number of programs + button
        nrPrgStatesField = new TextField();
        nrPrgStatesField.setEditable(false);
        nrPrgStatesField.setPrefColumnCount(6);

        runOneStepBtn = new Button("Run one step");
        runOneStepBtn.setOnAction(e -> onRunOneStep());

        HBox topBar = new HBox(10,
                new Label("#PrgStates:"),
                nrPrgStatesField,
                runOneStepBtn
        );
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // left: prgstate IDs
        prgStateIdsList = new ListView<>();
        prgStateIdsList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return;
            selectedPrgState = getPrgStateById(newV);
            refreshSelectedProgramViews();
        });

        VBox left = new VBox(6, new Label("PrgState IDs"), prgStateIdsList);
        left.setPadding(new Insets(10));
        left.setPrefWidth(180);

        // center: heap + out + filetable
        heapTable = createHeapTable();
        outList = new ListView<>();
        fileTableList = new ListView<>();

        VBox heapBox = new VBox(6, new Label("Heap"), heapTable);
        VBox outBox = new VBox(6, new Label("Out"), outList);
        VBox fileBox = new VBox(6, new Label("FileTable"), fileTableList);
        heapBox.setPadding(new Insets(10));
        outBox.setPadding(new Insets(10));
        fileBox.setPadding(new Insets(10));

        VBox center = new VBox(10, heapBox, outBox, fileBox);

        // right: symtable + exestack
        symTable = createSymTable();
        exeStackList = new ListView<>();
        VBox symBox = new VBox(6, new Label("SymTable (selected PrgState)"), symTable);
        VBox stackBox = new VBox(6, new Label("ExeStack (top first)"), exeStackList);
        symBox.setPadding(new Insets(10));
        stackBox.setPadding(new Insets(10));
        VBox right = new VBox(10, symBox, stackBox);
        right.setPrefWidth(450);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(left);
        root.setCenter(center);
        root.setRight(right);

        stage.setTitle("ToyLanguage - Interpreter GUI");
        stage.setScene(new Scene(root, 1200, 720));
        stage.setOnCloseRequest(e -> {
            if (controller != null) controller.shutdown();
            Platform.exit();
        });
        stage.show();

        refreshAll();
    }

    private TableView<HeapEntry> createHeapTable() {
        TableView<HeapEntry> table = new TableView<>();

        TableColumn<HeapEntry, Integer> addrCol = new TableColumn<>("Address");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addrCol.setPrefWidth(100);

        TableColumn<HeapEntry, String> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valCol.setPrefWidth(260);

        table.getColumns().addAll(addrCol, valCol);
        table.setPrefHeight(220);
        return table;
    }

    private TableView<SymEntry> createSymTable() {
        TableView<SymEntry> table = new TableView<>();

        TableColumn<SymEntry, String> nameCol = new TableColumn<>("Var");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(140);

        TableColumn<SymEntry, String> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valCol.setPrefWidth(240);

        table.getColumns().addAll(nameCol, valCol);
        table.setPrefHeight(220);
        return table;
    }

    private void clearGUI() {
        heapTable.getItems().clear();
        outList.getItems().clear();
        fileTableList.getItems().clear();
        prgStateIdsList.getItems().clear();
        symTable.getItems().clear();
        exeStackList.getItems().clear();
        nrPrgStatesField.setText("0");
    }


    private void onRunOneStep() {
        try {
            // dacă am ajuns deja la final → al doilea click = reset GUI
            if (programFinished) {
                clearGUI();
                programFinished = false;
                return;
            }

            // verificăm dacă mai există cel puțin un program cu exeStack NE-gol
            boolean hasRunnable = controller.getRepository()
                    .getPrgList()
                    .stream()
                    .anyMatch(p -> !p.getExeStack().isEmpty());

            if (!hasRunnable) {
                // nu mai rulăm nimic, doar marcăm finalul
                programFinished = true;
                return;
            }

            // executăm UN pas
            controller.oneStepForAllPrograms();
            refreshAll();

            // după execuție, verificăm dacă s-a terminat chiar acum
            boolean finishedNow = controller.getRepository()
                    .getPrgList()
                    .stream()
                    .noneMatch(p -> !p.getExeStack().isEmpty());

            if (finishedNow) {
                programFinished = true;
            }

        } catch (Exception ex) {
            showError("Execution error: " + ex.getMessage());
        }
    }


    // ------------------------------- Refresh logic -------------------------------

    private void refreshAll() {
        List<PrgState> prgList = controller.getRepository().getPrgList();
        nrPrgStatesField.setText(String.valueOf(prgList.size()));

        runOneStepBtn.setDisable(prgList.isEmpty());

        // shared structures (heap/out/filetable) -> use first state if exists
        if (prgList.isEmpty()) {
            heapTable.setItems(FXCollections.observableArrayList());
            outList.setItems(FXCollections.observableArrayList());
            fileTableList.setItems(FXCollections.observableArrayList());
            prgStateIdsList.setItems(FXCollections.observableArrayList());
            symTable.setItems(FXCollections.observableArrayList());
            exeStackList.setItems(FXCollections.observableArrayList());
            selectedPrgState = null;
            return;
        }

        // Heap
        ObservableList<HeapEntry> heapItems = FXCollections.observableArrayList(
                prgList.get(0).getHeap().getContent().entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .map(e -> new HeapEntry(e.getKey(), Objects.toString(e.getValue())))
                        .collect(Collectors.toList())
        );
        heapTable.setItems(heapItems);

        // Out
        outList.setItems(FXCollections.observableArrayList(
                prgList.get(0).getOut().getContent().stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList())
        ));

        // FileTable
        fileTableList.setItems(FXCollections.observableArrayList(
                prgList.get(0).getFileTable().getContent().keySet().stream()
                        .map(StringValue::toString)
                        .collect(Collectors.toList())
        ));

        // PrgState IDs
        List<Integer> ids = prgList.stream().map(PrgState::getId).collect(Collectors.toList());
        prgStateIdsList.setItems(FXCollections.observableArrayList(ids));

        // keep selection if possible
        Integer currentSelected = prgStateIdsList.getSelectionModel().getSelectedItem();
        if (currentSelected == null || getPrgStateById(currentSelected) == null) {
            prgStateIdsList.getSelectionModel().selectFirst();
            currentSelected = prgStateIdsList.getSelectionModel().getSelectedItem();
        }
        selectedPrgState = getPrgStateById(currentSelected);
        refreshSelectedProgramViews();
    }

    private void refreshSelectedProgramViews() {
        if (selectedPrgState == null) {
            symTable.setItems(FXCollections.observableArrayList());
            exeStackList.setItems(FXCollections.observableArrayList());
            return;
        }

        // SymTable for selected prg
        ObservableList<SymEntry> symItems = FXCollections.observableArrayList(
                selectedPrgState.getSymTable().getContent().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> new SymEntry(e.getKey(), Objects.toString(e.getValue())))
                        .collect(Collectors.toList())
        );
        symTable.setItems(symItems);

        // ExeStack for selected prg (TOP first)
        exeStackList.setItems(FXCollections.observableArrayList(
                selectedPrgState.getExeStack().getReversed().stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList())
        ));
    }

    private PrgState getPrgStateById(Integer id) {
        if (id == null) return null;
        return controller.getRepository().getPrgList().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ------------------------------- Table models -------------------------------

    public static class HeapEntry {
        private final SimpleIntegerProperty address;
        private final SimpleStringProperty value;

        public HeapEntry(int address, String value) {
            this.address = new SimpleIntegerProperty(address);
            this.value = new SimpleStringProperty(value);
        }

        public int getAddress() {
            return address.get();
        }

        public String getValue() {
            return value.get();
        }
    }

    public static class SymEntry {
        private final SimpleStringProperty name;
        private final SimpleStringProperty value;

        public SymEntry(String name, String value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value);
        }

        public String getName() {
            return name.get();
        }

        public String getValue() {
            return value.get();
        }
    }
}
