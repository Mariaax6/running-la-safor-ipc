package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class MapasViewController {

    @FXML
    private Button btnVolver;

    @FXML
    private void volverMenu() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        btnVolver.getScene().setRoot(root);
    }
    @FXML
    private void anadirMapa() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Añadir mapa");
        alert.setHeaderText("Función pendiente");
        alert.setContentText("Aquí se añadirá un mapa seleccionando imagen y coordenadas.");
        alert.showAndWait();
    }

    @FXML
    private void eliminarMapa() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Eliminar mapa");
        alert.setHeaderText("Función pendiente");
        alert.setContentText("Aquí se eliminará un mapa seleccionado.");
        alert.showAndWait();
    }
}