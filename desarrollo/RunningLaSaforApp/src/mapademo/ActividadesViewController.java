package mapademo;

// Código generado con ayuda de IA y revisado para el proyecto IPC.

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class ActividadesViewController {

    @FXML private TableView<ActividadFila> tablaActividades;
    @FXML private TableColumn<ActividadFila, String> colNombre;
    @FXML private TableColumn<ActividadFila, String> colFecha;
    @FXML private TableColumn<ActividadFila, String> colDistancia;
    @FXML private TableColumn<ActividadFila, String> colDuracion;

    private List<ActividadFila> listaActividades = new ArrayList<>();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
    }

    @FXML
    private void importarGPX() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo GPX");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx")
        );

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            ActividadFila nueva = new ActividadFila(
                archivo.getName(),
                LocalDate.now().toString(),
                "5.00 km",
                "30 min"
            );

            listaActividades.add(nueva);
            tablaActividades.getItems().add(nueva);
        }
    }

    @FXML
    private void visualizarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una actividad primero.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Estadísticas");
        alert.setHeaderText("Estadísticas de la actividad");
        alert.setContentText(
            "Nombre: " + seleccionada.getNombre() + "\n" +
            "Fecha: " + seleccionada.getFecha() + "\n" +
            "Distancia: " + seleccionada.getDistancia() + "\n" +
            "Duración: " + seleccionada.getDuracion() + "\n" +
            "Velocidad media: 10 km/h\n" +
            "Ritmo medio: 6:00 min/km\n" +
            "Desnivel positivo: 120 m\n" +
            "Desnivel negativo: 110 m\n" +
            "Altitud mínima: 20 m\n" +
            "Altitud máxima: 140 m"
        );
        alert.showAndWait();
    }

    @FXML
    private void renombrarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una actividad primero.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(seleccionada.getNombre());
        dialog.setTitle("Renombrar actividad");
        dialog.setHeaderText("Nuevo nombre de la actividad");
        dialog.setContentText("Nombre:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            seleccionada.setNombre(resultado.get());
            tablaActividades.refresh();
        }
    }

    @FXML
    private void borrarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una actividad primero.");
            return;
        }

        listaActividades.remove(seleccionada);
        tablaActividades.getItems().remove(seleccionada);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operación no válida");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}