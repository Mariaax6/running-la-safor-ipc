package mapademo;

import java.io.File;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class RegistroViewController {

    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker fechaNacimientoPicker;

    @FXML private Label nickError;
    @FXML private Label emailError;
    @FXML private Label passError;
    @FXML private Label fechaError;

    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;
    @FXML private Label avatarLabel;

    private String avatarPath = null;

    @FXML
    private void initialize() {
        btnRegistrar.setDisable(true);

        nicknameField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        emailField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        passwordField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        fechaNacimientoPicker.valueProperty().addListener((obs, o, n) -> comprobarCampos());
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
        String email = emailField.getText();
        String password = passwordField.getText();
        LocalDate fecha = fechaNacimientoPicker.getValue();

        boolean nickOk = User.checkNickName(nickname);
        boolean emailOk = User.checkEmail(email);
        boolean passOk = User.checkPassword(password);
        boolean fechaOk = fecha != null && User.isOlderThan(fecha, 12);

        marcarCampo(nicknameField, nickError, nickOk, !nickname.isEmpty());
        marcarCampo(emailField, emailError, emailOk, !email.isEmpty());
        marcarCampo(passwordField, passError, passOk, !password.isEmpty());
        marcarCampo(fechaNacimientoPicker, fechaError, fechaOk, fecha != null);

        boolean valido = nickOk && emailOk && passOk && fechaOk;

        btnRegistrar.setDisable(!valido);
    }

    @FXML
    private void registrarUsuario() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();

            Image avatar = null;
            if (avatarPath != null) {
                avatar = new Image("file:" + avatarPath);
            }

            boolean registrado = app.registerUser(
                nicknameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                fechaNacimientoPicker.getValue(),
                avatar
            );

            if (!registrado) {
                nickError.setText("El nickname ya existe");
                nickError.setVisible(true);
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro");
            alert.setHeaderText("Usuario registrado");
            alert.setContentText("Registro correcto");
            alert.showAndWait();

            volverLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar avatar");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            avatarPath = archivo.getAbsolutePath();
            avatarLabel.setText(archivo.getName());
        }
    }

    @FXML
    private void volverLogin() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        btnVolver.getScene().setRoot(root);
    }
}