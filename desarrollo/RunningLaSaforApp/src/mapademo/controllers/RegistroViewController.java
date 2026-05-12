package mapademo.controllers;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
public class RegistroViewController {

   @FXML private TextField nickField;
    @FXML private TextField emailField;
    @FXML private PasswordField passField;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label avatarLabel;
    private String avatarPath = null;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    private void chooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Elegir avatar");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File f = chooser.showOpenDialog(avatarLabel.getScene().getWindow());
        if (f != null) {
            avatarPath = f.getAbsolutePath();
            avatarLabel.setText(f.getName());
        }
    }

    @FXML
    private void handleRegister() {
        String nick = nickField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passField.getText().trim();
        LocalDate birth = birthDatePicker.getValue();

        // Validaciones manuales o con la librería
        if (!User.checkNickName(nick)) {
            showAlert("Nickname inválido", "Debe tener entre 6 y 15 caracteres, solo letras, dígitos, guion o subguión.");
            return;
        }
        if (!User.checkEmail(email)) {
            showAlert("Email inválido", "El formato del correo no es correcto.");
            return;
        }
        if (!User.checkPassword(pass)) {
            showAlert("Contraseña inválida", "Debe tener entre 8 y 20 caracteres, al menos una mayúscula, una minúscula, un dígito y un símbolo (!@#$%&*()-+=).");
            return;
        }
        if (birth == null) {
            showAlert("Fecha requerida", "Debe introducir la fecha de nacimiento.");
            return;
        }
        if (!User.isOlderThan(birth, 12)) {
            showAlert("Edad insuficiente", "Debes tener más de 12 años.");
            return;
        }

        boolean ok = app.registerUser(nick, email, pass, birth, avatarPath);
        if (ok) {
            showAlert("Registro exitoso", "Ahora puedes iniciar sesión.");
            goBack();
        } else {
            showAlert("Error", "El nickname ya está en uso o hubo un problema al registrar.");
        }
    }

    @FXML
private void goBack() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load(), mapademo.MainApp.APP_WIDTH, mapademo.MainApp.APP_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        Stage stage = (Stage) nickField.getScene().getWindow();
        stage.setScene(scene);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}