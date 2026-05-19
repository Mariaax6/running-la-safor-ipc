package mapademo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import java.io.IOException;

public class LoginViewController {

    @FXML private TextField nickField;
    @FXML private PasswordField passwordField;
    @FXML private Label nickErrorLabel;
    @FXML private Label passErrorLabel;
    @FXML private Label loginErrorLabel;
    @FXML private Button btnLogin;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    private void initialize() {
        btnLogin.setDisable(true);
        btnLogin.setStyle("-fx-opacity: 0.6;");
        
        nickField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        
        nickField.textProperty().addListener((obs, oldVal, newVal) -> loginErrorLabel.setVisible(false));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> loginErrorLabel.setVisible(false));
    }
    
    private void validateFields() {
        String nick = nickField.getText();
        String pass = passwordField.getText();
        
        boolean nickOk = User.checkNickName(nick);
        boolean passOk = User.checkPassword(pass);
        
        if (!nick.isEmpty() && !nickOk) {
            nickErrorLabel.setText("❌ 6-15 caracteres (letras, números, - o _)");
            nickField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
        } else {
            nickErrorLabel.setText("");
            if (!nick.isEmpty() && nickOk) {
                nickField.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
            } else if (nick.isEmpty()) {
                nickField.setStyle("");
            }
        }
        
        if (!pass.isEmpty() && !passOk) {
            passErrorLabel.setText("❌ 8-20 caracteres, con mayúscula, minúscula, número y símbolo");
            passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
        } else {
            passErrorLabel.setText("");
            if (!pass.isEmpty() && passOk) {
                passwordField.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
            } else if (pass.isEmpty()) {
                passwordField.setStyle("");
            }
        }
        
        boolean allValid = nickOk && passOk;
        btnLogin.setDisable(!allValid);
        if (allValid) {
            btnLogin.setStyle("-fx-opacity: 1.0;");
        } else {
            btnLogin.setStyle("-fx-opacity: 0.6;");
        }
    }

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
            loginErrorLabel.setText("❌ Nickname o contraseña incorrectos");
            loginErrorLabel.setVisible(true);
            
            nickField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
            passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
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
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.showAndWait();
    }
}