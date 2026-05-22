package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import upv.ipc.sportlib.SportActivityApp;

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
        ButtonType cancelarBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType cerrarBtn = new ButtonType("Cerrar sesión", ButtonBar.ButtonData.OK_DONE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres cerrar sesión?");

        alert.getButtonTypes().setAll(cancelarBtn, cerrarBtn);

        alert.setOnShown(e -> {
            Button cancelar = (Button) alert.getDialogPane().lookupButton(cancelarBtn);
            Button cerrar = (Button) alert.getDialogPane().lookupButton(cerrarBtn);

            cerrar.setDefaultButton(false);
            cancelar.setDefaultButton(true);
            cancelar.requestFocus();
        });

        if (alert.showAndWait().get() == cerrarBtn) {
            SportActivityApp.getInstance().logout();

            Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
            rootPane.getScene().setRoot(root);
        }
    }

    @FXML
    private void mostrarMapas() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("MapasView.fxml"));
        rootPane.setCenter(vista);
    }
}