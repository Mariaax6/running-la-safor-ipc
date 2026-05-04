package mapademo;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

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
                new ActividadFila(archivo.getName(), "Pendiente", "Pendiente", "Pendiente")
            );
        }
    }
}