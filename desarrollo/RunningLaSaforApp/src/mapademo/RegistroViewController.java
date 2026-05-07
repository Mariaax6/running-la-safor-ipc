package mapademo;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class RegistroViewController {

    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private Button btnVolver;

    @FXML
    private void registrarUsuario() {
        String nickname = nicknameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        LocalDate fecha = fechaNacimientoPicker.getValue();

        if (!User.checkNickName(nickname)) {
            mostrarError("Nickname inválido. Debe tener entre 6 y 15 caracteres.");
            return;
        }

        if (!User.checkEmail(email)) {
            mostrarError("Email inválido.");
            return;
        }

        if (!User.checkPassword(password)) {
            mostrarError("Contraseña inválida.");
            return;
        }

        if (fecha == null || !User.isOlderThan(fecha, 12)) {
            mostrarError("Debes tener más de 12 años.");
            return;
        }

        try {
            SportActivityApp app = SportActivityApp.getInstance();

            Image avatar = null;

            boolean registrado = app.registerUser(
                nickname,
                email,
                password,
                fecha,
                avatar
            );

            if (!registrado) {
                mostrarError("No se ha podido registrar. Puede que el nickname ya exista.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro");
            alert.setHeaderText("Usuario registrado");
            alert.setContentText("El usuario se ha registrado correctamente.");
            alert.showAndWait();

            volverLogin();

        } catch (Exception e) {
            mostrarError("Error al registrar usuario.");
        }
    }

    @FXML
    private void volverLogin() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        btnVolver.getScene().setRoot(root);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Registro incorrecto");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}