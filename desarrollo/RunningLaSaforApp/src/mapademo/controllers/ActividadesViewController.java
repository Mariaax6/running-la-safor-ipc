package mapademo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

public class ActividadesViewController {

    @FXML private ListView<Activity> activityListView;
    @FXML private Label statsLabel;
    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        List<Activity> activities = app.getUserActivities();
        activityListView.getItems().addAll(activities);
        activityListView.setCellFactory(lv -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity act, boolean empty) {
                super.updateItem(act, empty);
                if (empty || act == null) {
                    setText(null);
                } else {
                    setText(act.getName() + " - " + act.getStartTime().toString());
                }
            }
        });

        // Doble clic para abrir la actividad en el mapa
        activityListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Activity selected = activityListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    abrirActividadEnMapa(selected);
                }
            }
        });
    }

    @FXML
    private void verActividad() {
        Activity selected = activityListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Selecciona una actividad", "Por favor, selecciona una actividad de la lista.");
            return;
        }
        abrirActividadEnMapa(selected);
    }

    private void abrirActividadEnMapa(Activity act) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MapasView.fxml"));
            Parent view = loader.load();
            MapasViewController mapCtrl = loader.getController();
            mapCtrl.setActivity(act);
            BorderPane mainPane = (BorderPane) activityListView.getScene().getRoot();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarActividad() {
        Activity selected = activityListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Selecciona una actividad", "Por favor, selecciona una actividad para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar actividad");
        confirm.setHeaderText("¿Estás seguro de que deseas eliminar esta actividad?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        ButtonType btnSi = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnSi, btnCancelar);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnSi) {
            app.removeActivity(selected);
            activityListView.getItems().remove(selected);
            mostrarAlerta("Eliminada", "La actividad se ha eliminado correctamente.");
        }
    }

   @FXML
private void showMonthlyStats() {
    List<Activity> activities = app.getUserActivities();
    LocalDate now = LocalDate.now();
    double totalDist = 0, totalGain = 0, totalLoss = 0;
    long totalSeconds = 0;
    int count = 0;

    for (Activity a : activities) {
        // Si no tiene fecha de inicio, usamos la fecha de hoy
        LocalDate date = (a.getStartTime() != null) ? a.getStartTime().toLocalDate() : now;

        if (date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
            totalDist += a.getTotalDistance();
            totalSeconds += a.getDuration().getSeconds();
            totalGain += a.getElevationGain();
            totalLoss += a.getElevationLoss();
            count++;
        }
    }

    if (count == 0) {
        statsLabel.setText("No hay actividades registradas en " +
                now.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault()) +
                " de " + now.getYear() + ".");
    } else {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        statsLabel.setText(
            String.format("Actividades del mes: %d\nDistancia: %.2f km\nTiempo: %dh %dm\nDesnivel positivo: %.0f m\nDesnivel negativo: %.0f m",
                count, totalDist/1000.0, hours, minutes, totalGain, totalLoss));
    }
}
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}