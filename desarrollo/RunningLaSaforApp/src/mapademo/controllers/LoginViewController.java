package mapademo.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import java.io.IOException;
import javafx.event.ActionEvent;

public class LoginViewController {

    @FXML private TextField nickField;
    @FXML private PasswordField passwordField;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    private void handleLogin() {
        String nick = nickField.getText().trim();
        String pass = passwordField.getText().trim();
        if (app.login(nick, pass)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MainView.fxml"));
                Scene scene = new Scene(loader.load(), mapademo.MainApp.APP_WIDTH, mapademo.MainApp.APP_HEIGHT);
                scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
                Stage stage = (Stage) nickField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Running la Safor - Panel principal");
            } catch (IOException e) {
                showAlert("Error", "No se pudo cargar la ventana principal.");
            }
        } else {
            showAlert("Error de autenticación", "Nickname o contraseña incorrectos.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/RegistroView.fxml"));
            Scene scene = new Scene(loader.load(), mapademo.MainApp.APP_WIDTH, mapademo.MainApp.APP_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
            Stage stage = (Stage) nickField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "No se pudo cargar el formulario de registro.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void skipLogin(ActionEvent event) {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MainView.fxml"));
                Scene scene = new Scene(loader.load(), mapademo.MainApp.APP_WIDTH, mapademo.MainApp.APP_HEIGHT);
                scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
                Stage stage = (Stage) nickField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Running la Safor - Panel principal");
            } catch (IOException e) {
                showAlert("Error", "No se pudo cargar la ventana principal.");
            }
    }
}