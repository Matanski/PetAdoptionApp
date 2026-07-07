package com.hit.client.view;

import com.hit.client.controller.AppController;
import com.hit.client.model.AdoptionRequest;
import com.hit.client.model.Pet;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private static final String HOST = "localhost";
    private static final int PET_PORT = 34567;
    private static final int ADOPTION_PORT = 34568;

    private AppController controller;
    private ObservableList<String> petListItems = FXCollections.observableArrayList();
    private ObservableList<String> adoptionListItems = FXCollections.observableArrayList();
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        controller = new AppController(HOST, PET_PORT, ADOPTION_PORT);

        primaryStage.setTitle("Pet Adoption App");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(buildPetsTab(), buildAdoptionsTab());

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-padding: 5; -fx-font-style: italic;");

        VBox root = new VBox(tabPane, statusLabel);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 700, 550);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshPets();
        refreshAdoptions();
    }

    // ─── PETS TAB ──────────────────────────────────────────────────────────────

    private Tab buildPetsTab() {
        Tab tab = new Tab("Pets");
        tab.setClosable(false);

        ListView<String> listView = new ListView<>(petListItems);
        listView.setPrefHeight(200);

        // Form fields
        TextField idField = new TextField(); idField.setPromptText("ID");
        TextField nameField = new TextField(); nameField.setPromptText("Name");
        TextField speciesField = new TextField(); speciesField.setPromptText("Species");
        TextField breedField = new TextField(); breedField.setPromptText("Breed");
        TextField ageField = new TextField(); ageField.setPromptText("Age");
        TextField descField = new TextField(); descField.setPromptText("Description");

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(6);
        form.addRow(0, new Label("ID:"), idField, new Label("Name:"), nameField);
        form.addRow(1, new Label("Species:"), speciesField, new Label("Breed:"), breedField);
        form.addRow(2, new Label("Age:"), ageField, new Label("Description:"), descField);

        Button addBtn = new Button("Add Pet");
        Button updateBtn = new Button("Update Pet");
        Button deleteBtn = new Button("Delete Pet");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> {
            try {
                Pet pet = new Pet(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(), speciesField.getText(),
                    breedField.getText(), Integer.parseInt(ageField.getText()),
                    descField.getText()
                );
                String msg = controller.addPet(pet);
                setStatus(msg);
                refreshPets();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                Pet pet = new Pet(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(), speciesField.getText(),
                    breedField.getText(), Integer.parseInt(ageField.getText()),
                    descField.getText()
                );
                String msg = controller.updatePet(pet);
                setStatus(msg);
                refreshPets();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                String msg = controller.deletePet(Integer.parseInt(idField.getText()));
                setStatus(msg);
                refreshPets();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        refreshBtn.setOnAction(e -> refreshPets());

        HBox buttons = new HBox(8, addBtn, updateBtn, deleteBtn, refreshBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10, listView, form, buttons);
        content.setPadding(new Insets(12));
        tab.setContent(content);
        return tab;
    }

    // ─── ADOPTIONS TAB ─────────────────────────────────────────────────────────

    private Tab buildAdoptionsTab() {
        Tab tab = new Tab("Adoptions");
        tab.setClosable(false);

        ListView<String> listView = new ListView<>(adoptionListItems);
        listView.setPrefHeight(200);

        TextField idField = new TextField(); idField.setPromptText("Request ID");
        TextField userIdField = new TextField(); userIdField.setPromptText("User ID");
        TextField petIdField = new TextField(); petIdField.setPromptText("Pet ID");
        TextField messageField = new TextField(); messageField.setPromptText("Message");

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(6);
        form.addRow(0, new Label("Request ID:"), idField, new Label("User ID:"), userIdField);
        form.addRow(1, new Label("Pet ID:"), petIdField, new Label("Message:"), messageField);

        Button submitBtn = new Button("Submit");
        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");
        Button refreshBtn = new Button("Refresh");

        submitBtn.setOnAction(e -> {
            try {
                AdoptionRequest req = new AdoptionRequest(
                    Integer.parseInt(idField.getText()),
                    Integer.parseInt(userIdField.getText()),
                    Integer.parseInt(petIdField.getText()),
                    messageField.getText()
                );
                String msg = controller.submitAdoption(req);
                setStatus(msg);
                refreshAdoptions();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        approveBtn.setOnAction(e -> {
            try {
                String msg = controller.approveAdoption(Integer.parseInt(idField.getText()));
                setStatus(msg);
                refreshAdoptions();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        rejectBtn.setOnAction(e -> {
            try {
                String msg = controller.rejectAdoption(Integer.parseInt(idField.getText()));
                setStatus(msg);
                refreshAdoptions();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        refreshBtn.setOnAction(e -> refreshAdoptions());

        HBox buttons = new HBox(8, submitBtn, approveBtn, rejectBtn, refreshBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10, listView, form, buttons);
        content.setPadding(new Insets(12));
        tab.setContent(content);
        return tab;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private void refreshPets() {
        petListItems.clear();
        List<Pet> pets = controller.getAllPets();
        if (pets.isEmpty()) {
            petListItems.add("(no pets)");
        } else {
            for (Pet p : pets) {
                petListItems.add(p.toString());
            }
        }
    }

    private void refreshAdoptions() {
        adoptionListItems.clear();
        List<AdoptionRequest> list = controller.getAllAdoptions();
        if (list.isEmpty()) {
            adoptionListItems.add("(no adoption requests)");
        } else {
            for (AdoptionRequest r : list) {
                adoptionListItems.add(r.toString());
            }
        }
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
