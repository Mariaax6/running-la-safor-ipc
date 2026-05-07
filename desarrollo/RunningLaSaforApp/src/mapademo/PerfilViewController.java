package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class PerfilViewController {

    @FXML
    private Button btnVolver;

    @FXML
    private void volverMenu() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        btnVolver.getScene().setRoot(root);
    }
    @FXML
    private void guardarCambios() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Perfil");
        alert.setHeaderText("Cambios guardados");
        alert.setContentText("Los cambios del perfil se han guardado correctamente.");
        alert.showAndWait();
    }
}