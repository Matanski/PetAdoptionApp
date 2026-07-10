package com.hit.client.view;

import com.hit.client.controller.AppController;
import com.hit.client.model.AdoptionRequest;
import com.hit.client.model.Pet;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private static final String HOST = "localhost";
    private static final int PET_PORT = 34567;
    private static final int ADOPTION_PORT = 34568;

    private AppController controller;

    private final ObservableList<Pet> pets = FXCollections.observableArrayList();
    private final ObservableList<AdoptionRequest> adoptions = FXCollections.observableArrayList();

    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        controller = new AppController(HOST, PET_PORT, ADOPTION_PORT);

        primaryStage.setTitle("Pet Adoption");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(buildPetsTab(), buildAdoptionsTab());
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        VBox root = new VBox(buildHeader(), tabPane, buildStatusBar());
        root.setStyle("-fx-background-color: " + Styles.BG + ";");

        Scene scene = new Scene(root, 860, 640);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshPets();
        refreshAdoptions();
    }

    // ─── Header ────────────────────────────────────────────────────────────────

    private Node buildHeader() {
        Label title = new Label("Pet Adoption");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Find a home for every pet");
        subtitle.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 12px;");

        VBox box = new VBox(2, title, subtitle);
        box.setPadding(new Insets(16, 20, 16, 20));
        box.setStyle("-fx-background-color: " + Styles.PRIMARY + ";");
        return box;
    }

    private Node buildStatusBar() {
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: " + Styles.MUTED + "; -fx-font-size: 12px;");
        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(8, 20, 8, 20));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + Styles.BORDER + " 0 0 0 0;");
        return bar;
    }

    // ─── Pets tab ──────────────────────────────────────────────────────────────

    private Tab buildPetsTab() {
        Tab tab = new Tab("Pets");

        TableView<Pet> table = new TableView<>(pets);
        table.setPlaceholder(new Label("No pets yet - add one below"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle(Styles.card());
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getColumns().addAll(
                col("ID", "id", 50),
                col("Name", "name", 110),
                col("Species", "species", 90),
                col("Breed", "breed", 110),
                col("Age", "age", 55),
                statusCol("Status", 100),
                col("Description", "description", 220)
        );

        TextField idField = field("ID");
        TextField nameField = field("Name");
        TextField speciesField = field("Species");
        TextField breedField = field("Breed");
        TextField ageField = field("Age");
        TextField descField = field("Description");

        // clicking a row loads it into the form - no need to type the ID by hand
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idField.setText(String.valueOf(sel.getId()));
                nameField.setText(sel.getName());
                speciesField.setText(sel.getSpecies());
                breedField.setText(sel.getBreed());
                ageField.setText(String.valueOf(sel.getAge()));
                descField.setText(sel.getDescription());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, label("ID"), idField, label("Name"), nameField);
        form.addRow(1, label("Species"), speciesField, label("Breed"), breedField);
        form.addRow(2, label("Age"), ageField, label("Description"), descField);
        form.setPadding(new Insets(14));
        form.setStyle(Styles.card());

        Button addBtn = Styles.button("Add Pet", Styles.PRIMARY);
        Button updateBtn = Styles.button("Update", Styles.NEUTRAL);
        Button deleteBtn = Styles.button("Delete", Styles.DANGER);
        Button refreshBtn = Styles.button("Refresh", Styles.NEUTRAL);
        Button clearBtn = Styles.button("Clear", Styles.NEUTRAL);

        addBtn.setOnAction(e -> {
            Pet pet = readPet(idField, nameField, speciesField, breedField, ageField, descField);
            if (pet == null) return;
            ok(controller.addPet(pet));
            refreshPets();
        });

        updateBtn.setOnAction(e -> {
            Pet pet = readPet(idField, nameField, speciesField, breedField, ageField, descField);
            if (pet == null) return;
            ok(controller.updatePet(pet));
            refreshPets();
        });

        deleteBtn.setOnAction(e -> {
            Integer id = readInt(idField, "ID");
            if (id == null) return;
            ok(controller.deletePet(id));
            refreshPets();
        });

        refreshBtn.setOnAction(e -> {
            refreshPets();
            ok("Pet list refreshed");
        });

        clearBtn.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            idField.clear(); nameField.clear(); speciesField.clear();
            breedField.clear(); ageField.clear(); descField.clear();
        });

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(14, table, form, buttons);
        content.setPadding(new Insets(16));
        tab.setContent(content);
        return tab;
    }

    // ─── Adoptions tab ─────────────────────────────────────────────────────────

    private Tab buildAdoptionsTab() {
        Tab tab = new Tab("Adoptions");

        TableView<AdoptionRequest> table = new TableView<>(adoptions);
        table.setPlaceholder(new Label("No adoption requests yet"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle(Styles.card());
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getColumns().addAll(
                col("ID", "id", 60),
                col("Pet ID", "petId", 70),
                col("User ID", "userId", 70),
                statusCol("Status", 100),
                col("Message", "message", 300)
        );

        TextField idField = field("Request ID");
        TextField userIdField = field("User ID");
        TextField petIdField = field("Pet ID");
        TextField messageField = field("Message");

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idField.setText(String.valueOf(sel.getId()));
                userIdField.setText(String.valueOf(sel.getUserId()));
                petIdField.setText(String.valueOf(sel.getPetId()));
                messageField.setText(sel.getMessage());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, label("Request ID"), idField, label("User ID"), userIdField);
        form.addRow(1, label("Pet ID"), petIdField, label("Message"), messageField);
        form.setPadding(new Insets(14));
        form.setStyle(Styles.card());

        Button submitBtn = Styles.button("Submit Request", Styles.PRIMARY);
        Button approveBtn = Styles.button("Approve", Styles.SUCCESS);
        Button rejectBtn = Styles.button("Reject", Styles.DANGER);
        Button refreshBtn = Styles.button("Refresh", Styles.NEUTRAL);

        submitBtn.setOnAction(e -> {
            Integer id = readInt(idField, "Request ID");
            Integer userId = readInt(userIdField, "User ID");
            Integer petId = readInt(petIdField, "Pet ID");
            if (id == null || userId == null || petId == null) return;
            ok(controller.submitAdoption(new AdoptionRequest(id, userId, petId, messageField.getText())));
            refreshAdoptions();
        });

        approveBtn.setOnAction(e -> {
            Integer id = readInt(idField, "Request ID");
            if (id == null) return;
            ok(controller.approveAdoption(id));
            refreshAdoptions();
            refreshPets();   // the approved pet is now marked "adopted"
        });

        rejectBtn.setOnAction(e -> {
            Integer id = readInt(idField, "Request ID");
            if (id == null) return;
            ok(controller.rejectAdoption(id));
            refreshAdoptions();
        });

        refreshBtn.setOnAction(e -> {
            refreshAdoptions();
            ok("Adoption list refreshed");
        });

        HBox buttons = new HBox(10, submitBtn, approveBtn, rejectBtn, refreshBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(14, table, form, buttons);
        content.setPadding(new Insets(16));
        tab.setContent(content);
        return tab;
    }

    // ─── Table helpers ─────────────────────────────────────────────────────────

    private <T> TableColumn<T, Object> col(String title, String property, int width) {
        TableColumn<T, Object> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    // status column that paints the value green / red / amber
    private <T> TableColumn<T, String> statusCol(String title, int width) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>("status"));
        c.setPrefWidth(width);
        c.setCellFactory(column -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(value);
                    setStyle("-fx-text-fill: " + Styles.statusColour(value) + "; -fx-font-weight: bold;");
                }
            }
        });
        return c;
    }

    // ─── Form helpers ──────────────────────────────────────────────────────────

    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(Styles.textField());
        return f;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + Styles.TEXT + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        return l;
    }

    // returns null and shows an error if the field is not a valid whole number
    private Integer readInt(TextField f, String name) {
        try {
            return Integer.parseInt(f.getText().trim());
        } catch (NumberFormatException e) {
            error(name + " must be a whole number");
            return null;
        }
    }

    private Pet readPet(TextField id, TextField name, TextField species,
                        TextField breed, TextField age, TextField desc) {
        Integer petId = readInt(id, "ID");
        Integer petAge = readInt(age, "Age");
        if (petId == null || petAge == null) return null;
        if (name.getText().trim().isEmpty()) {
            error("Name cannot be empty");
            return null;
        }
        return new Pet(petId, name.getText(), species.getText(),
                breed.getText(), petAge, desc.getText());
    }

    // ─── Data refresh ──────────────────────────────────────────────────────────

    private void refreshPets() {
        List<Pet> list = controller.getAllPets();
        pets.setAll(list);
    }

    private void refreshAdoptions() {
        List<AdoptionRequest> list = controller.getAllAdoptions();
        adoptions.setAll(list);
    }

    // ─── Status bar ────────────────────────────────────────────────────────────

    private void ok(String msg) {
        boolean failed = msg != null && msg.toLowerCase().startsWith("error");
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: "
                + (failed ? Styles.DANGER : Styles.SUCCESS) + ";");
    }

    private void error(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Styles.DANGER + ";");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
