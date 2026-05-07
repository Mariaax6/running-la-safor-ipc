package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class HistorialViewController {

    @FXML
    private Button btnVolver;

    @FXML
    private void volverMenu() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        btnVolver.getScene().setRoot(root);
    }
}