package mapademo;

import java.io.File;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;

public class ActividadesViewController {

    @FXML private TableView<ActividadFila> tablaActividades;
    @FXML private TableColumn<ActividadFila, String> colNombre;
    @FXML private TableColumn<ActividadFila, String> colFecha;
    @FXML private TableColumn<ActividadFila, String> colDistancia;
    @FXML private TableColumn<ActividadFila, String> colDuracion;

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
            tablaActividades.getItems().add(
                new ActividadFila(archivo.getName(), "04/05/2026", "5 km", "30 min")
            );
        }
    }

    @FXML
    private void borrarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            tablaActividades.getItems().remove(seleccionada);
        }
    }

    @FXML
    private void renombrarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
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
    }
    @FXML
    private void visualizarActividad() {
        ActividadFila seleccionada = tablaActividades.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Visualizar actividad");
            alert.setHeaderText("Actividad seleccionada");
            alert.setContentText(
                "Nombre: " + seleccionada.getNombre() + "\n" +
                "Fecha: " + seleccionada.getFecha() + "\n" +
                "Distancia: " + seleccionada.getDistancia() + "\n" +
                "Duración: " + seleccionada.getDuracion()
            );
            alert.showAndWait();
        }
    }
}