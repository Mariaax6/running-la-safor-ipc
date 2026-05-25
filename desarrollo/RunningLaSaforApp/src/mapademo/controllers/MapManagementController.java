package mapademo.controllers;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import javafx.scene.layout.VBox;
import upv.ipc.sportlib.MapRegion;

//hemos usado la IA para corregir el codigo y asegurarnos que esta bien

public class MapManagementController {

    @FXML private ListView<MapRegion> allMapsList;
    @FXML private ListView<MapRegion> unusedMapsList;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        refreshLists();
    }

    private void refreshLists() {
        allMapsList.getItems().clear();
        allMapsList.getItems().addAll(app.getMapRegions());
        unusedMapsList.getItems().clear();
        unusedMapsList.getItems().addAll(app.getUnusedMapRegions());
    }

    @FXML
    private void addMap() {
        // Pedir imagen
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen del mapa");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg"));
        File imgFile = chooser.showOpenDialog(allMapsList.getScene().getWindow());
        if (imgFile == null) return;

        // Pedir coordenadas mediante un diálogo
        Dialog<MapRegion> dialog = new Dialog<>();
        dialog.setTitle("Nuevo mapa");
        dialog.setHeaderText("Introduce las coordenadas del bounding box");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField nameField = new TextField("Nuevo mapa");
        TextField latMinField = new TextField();
        TextField latMaxField = new TextField();
        TextField lonMinField = new TextField();
        TextField lonMaxField = new TextField();

        VBox vbox = new VBox(8,
                new Label("Nombre:"), nameField,
                new Label("Latitud mínima:"), latMinField,
                new Label("Latitud máxima:"), latMaxField,
                new Label("Longitud mínima:"), lonMinField,
                new Label("Longitud máxima:"), lonMaxField);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == okButton) {
                try {
                    double latMin = Double.parseDouble(latMinField.getText());
                    double latMax = Double.parseDouble(latMaxField.getText());
                    double lonMin = Double.parseDouble(lonMinField.getText());
                    double lonMax = Double.parseDouble(lonMaxField.getText());
                    return app.addMapRegion(nameField.getText(), imgFile, latMin, latMax, lonMin, lonMax);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<MapRegion> result = dialog.showAndWait();
        result.ifPresent(region -> {
            if (region != null) {
                refreshLists();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al añadir el mapa. Verifica los datos.");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void removeMap() {
        MapRegion selected = unusedMapsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecciona un mapa no usado para eliminar.");
            alert.showAndWait();
            return;
        }
        boolean ok = app.removeMapRegion(selected);
        if (ok) {
            refreshLists();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el mapa.");
            alert.showAndWait();
        }
    }
}

