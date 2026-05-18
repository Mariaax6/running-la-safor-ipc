package mapademo.controllers;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;


import upv.ipc.sportlib.Activity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Date;  // solo si realmente lo usas en otra parte, si no, elimínala



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

        // Al seleccionar una actividad se carga el mapa
        activityListView.getSelectionModel().selectedItemProperty().addListener((obs, old, act) -> {
            if (act != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MapasView.fxml"));
                    Parent view = loader.load();
                    MapasViewController mapCtrl = loader.getController();
                    mapCtrl.setActivity(act);
                    // Obtenemos el BorderPane raíz del Main.fxml (estamos dentro del centro)
                    BorderPane mainPane = (BorderPane) activityListView.getScene().getRoot();
                    mainPane.setCenter(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void showMonthlyStats() {
        
        List<Activity> activities = app.getUserActivities();
        LocalDate now = LocalDate.now();
        double totalDist = 0, totalGain = 0, totalLoss = 0;
        long totalSeconds = 0;
        int count = 0;
        for (Activity a : activities) {
            if (a.getStartTime() != null) {
                LocalDateTime startTime = a.getStartTime();    // el método devuelve LocalDateTime
                LocalDate date = startTime.toLocalDate();       // extrae la fecha
                if (date.getMonth() == now.getMonth() && date.getYear() == now.getYear()) {
                    totalDist += a.getTotalDistance();
                    totalSeconds += a.getDuration().getSeconds();
                    totalGain += a.getElevationGain();
                    totalLoss += a.getElevationLoss();
                    count++;
                }
            }
        }
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        statsLabel.setText(
            String.format("Actividades del mes: %d\nDistancia: %.2f km\nTiempo: %dh %dm\nDesnivel positivo: %.0f m\nDesnivel negativo: %.0f m",
                count, totalDist/1000.0, hours, minutes, totalGain, totalLoss));
    }
}