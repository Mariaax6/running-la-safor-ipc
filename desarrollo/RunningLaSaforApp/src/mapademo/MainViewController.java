
package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

public class MainViewController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private void mostrarActividades() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("ActividadesView.fxml"));
        rootPane.setCenter(vista);
    }
    @FXML
    private void mostrarHistorial() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("HistorialView.fxml"));
        rootPane.setCenter(vista);
    }
    @FXML
    private void mostrarPerfil() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("PerfilView.fxml"));
        rootPane.setCenter(vista);
    }
    @FXML
    private void cerrarSesion() throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
    rootPane.getScene().setRoot(root);
    }  
    @FXML
    private void mostrarMapas() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("MapasView.fxml"));
        rootPane.setCenter(vista);
    }
}
