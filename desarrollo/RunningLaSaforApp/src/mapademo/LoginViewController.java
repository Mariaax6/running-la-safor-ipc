package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class LoginViewController {

    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;

    @FXML private Label nickError;
    @FXML private Label passError;
    @FXML private Label loginError;

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
        String nick = nicknameField.getText();
        String pass = passwordField.getText();

        boolean nickOk = User.checkNickName(nick);
        boolean passOk = User.checkPassword(pass);

        marcarCampo(nicknameField, nickError, nickOk, !nick.isEmpty());
        marcarCampo(passwordField, passError, passOk, !pass.isEmpty());

        btnLogin.setDisable(!(nickOk && passOk));

        // Ocultar error de login mientras escribe
        loginError.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();

            boolean ok = app.login(
                    nicknameField.getText(),
                    passwordField.getText()
            );

            if (!ok) {
                // mostrar error abajo
                loginError.setVisible(true);

                // poner ambos campos en rojo
                nicknameField.setStyle("-fx-background-color: #FCE5E0");
                passwordField.setStyle("-fx-background-color: #FCE5E0");
                return;
            }

            // ir a main
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            nicknameField.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirRegistro() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("RegistroView.fxml"));
        nicknameField.getScene().setRoot(root);
    }

    @FXML
    private void entrarSinLogin() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        nicknameField.getScene().setRoot(root);
    }
}