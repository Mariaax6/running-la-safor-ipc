package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import upv.ipc.sportlib.SportActivityApp;

public class LoginViewController {

    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;

    @FXML private Label nickError;
    @FXML private Label passError;

    @FXML private Button btnLogin;

    @FXML
    private void initialize() {
        btnLogin.setDisable(true);

        nicknameField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        passwordField.textProperty().addListener((obs, o, n) -> comprobarCampos());
    }

    private void marcarCampo(Control field, Label errorLabel, boolean correcto, boolean tieneTexto) {
        if (!tieneTexto) {
            errorLabel.setVisible(false);
            field.setStyle("");
        } else {
            errorLabel.setVisible(!correcto);
            field.setStyle(correcto ? "" : "-fx-background-color: #FCE5E0");
        }
    }

    private void comprobarCampos() {
        String nickname = nicknameField.getText();
        String password = passwordField.getText();

        boolean nickOk = !nickname.isEmpty();
        boolean passOk = !password.isEmpty();

        marcarCampo(nicknameField, nickError, nickOk, !nickname.isEmpty());
        marcarCampo(passwordField, passError, passOk, !password.isEmpty());

        btnLogin.setDisable(!(nickOk && passOk));
    }

    @FXML
    private void login() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();

            boolean ok = app.login(
                nicknameField.getText(),
                passwordField.getText()
            );

            if (!ok) {
                passError.setText("Usuario o contraseña incorrectos");
                passError.setVisible(true);
                passwordField.setStyle("-fx-background-color: #FCE5E0");
                return;
            }

            // ✅ navegación correcta (como antes)
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            Scene scene = nicknameField.getScene();
            scene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void entrarSinLogin() {
        try {
            // ✅ mismo comportamiento que antes
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            Scene scene = nicknameField.getScene();
            scene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irRegistro() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("RegistroView.fxml"));
        nicknameField.getScene().setRoot(root);
    }
}