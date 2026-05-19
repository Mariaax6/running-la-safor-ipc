
package mapademo.controllers;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.Duration;
import java.util.List;
import upv.ipc.sportlib.Session;

public class HistorialViewController {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> colStart, colEnd, colDuration;
    @FXML private TableColumn<Session, Number> colImported, colViewed, colAnnotations;
    @FXML private Label totalLabel;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        User user = app.getCurrentUser();
        if (user == null) return;

        // SE DESPLAZA AQUÍ: Fuerza al TableView a ajustar las columnas de forma dinámica
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Configurar fábricas de celdas
        colStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime().toString()));
        colEnd.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndTime() != null ?
                data.getValue().getEndTime().toString() : ""));
        colDuration.setCellValueFactory(data -> {
            Duration d = data.getValue().getDuration();
            if (d == null) return new SimpleStringProperty("");
            long h = d.toHours();
            long m = d.toMinutesPart();
            return new SimpleStringProperty(String.format("%dh %dm", h, m));
        });
        colImported.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getImportedActivities()));
        colViewed.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getViewedActivities()));
        colAnnotations.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAnnotationsCreated()));

        // Cargar datos
        List<Session> sessions = app.getSessionsByUser(user);
        sessionTable.setItems(FXCollections.observableArrayList(sessions));

        // Totales acumulados
        int totalImport = 0, totalView = 0, totalAnnot = 0;
        long totalSecs = 0;
        for (Session s : sessions) {
            totalImport += s.getImportedActivities();
            totalView += s.getViewedActivities();
            totalAnnot += s.getAnnotationsCreated();
            if (s.getDuration() != null) totalSecs += s.getDuration().getSeconds();
        }
        long h = totalSecs / 3600;
        long m = (totalSecs % 3600) / 60;
        totalLabel.setText(String.format(
            "Total sesiones: %d\nTiempo total: %dh %dm\nAct. importadas: %d\nAct. vistas: %d\nAnotaciones: %d",
            sessions.size(), h, m, totalImport, totalView, totalAnnot));
    }
}