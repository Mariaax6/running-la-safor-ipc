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

public class PerfilViewController {

    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker fechaPicker;

    @FXML private Label emailError;
    @FXML private Label passError;
    @FXML private Label fechaError;

    @FXML private Label avatarLabel;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private String avatarPath = null;

    @FXML
    private void initialize() {
        try {
            User user = SportActivityApp.getInstance().getCurrentUser();

            nicknameField.setText(user.getNickName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword()); // ✔ se carga pero oculta
            fechaPicker.setValue(user.getBirthDate());

            nicknameField.setDisable(true);

            btnGuardar.setDisable(true);

            emailField.textProperty().addListener((obs, o, n) -> comprobarCampos());
            passwordField.textProperty().addListener((obs, o, n) -> comprobarCampos());
            fechaPicker.valueProperty().addListener((obs, o, n) -> comprobarCampos());

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String email = emailField.getText();
        String password = passwordField.getText();
        LocalDate fecha = fechaPicker.getValue();

        boolean emailOk = User.checkEmail(email);
        boolean passOk = User.checkPassword(password);
        boolean fechaOk = fecha != null && User.isOlderThan(fecha, 12);

        marcarCampo(emailField, emailError, emailOk, !email.isEmpty());
        marcarCampo(passwordField, passError, passOk, !password.isEmpty());
        marcarCampo(fechaPicker, fechaError, fechaOk, fecha != null);

        boolean valido = emailOk && passOk && fechaOk;
        btnGuardar.setDisable(!valido);
    }

    @FXML
    private void seleccionarAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar avatar");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            avatarPath = file.getAbsolutePath();
            avatarLabel.setText(file.getName());
        }
    }

    @FXML
    private void guardarCambios() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();

            Image avatar = null;
            if (avatarPath != null) {
                avatar = new Image("file:" + avatarPath);
            }

            app.updateCurrentUser(
                emailField.getText(),
                passwordField.getText(),
                fechaPicker.getValue(),
                avatar
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Perfil");
            alert.setHeaderText(null);
            alert.setContentText("Los cambios del perfil se han guardado correctamente.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volverMenu() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        btnVolver.getScene().setRoot(root);
    }
}