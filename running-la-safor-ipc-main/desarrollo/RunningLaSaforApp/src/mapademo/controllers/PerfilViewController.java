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

public class PerfilViewController {

   @FXML private TextField nickField;
    @FXML private TextField emailField;
    @FXML private PasswordField passField;
    @FXML private DatePicker birthPicker;
    @FXML private Label avatarLabel;

    private SportActivityApp app = SportActivityApp.getInstance();
    private User currentUser;
    private String newAvatarPath = null;

    @FXML
    public void initialize() {
        currentUser = app.getCurrentUser();
        if (currentUser != null) {
            nickField.setText(currentUser.getNickName());
            emailField.setText(currentUser.getEmail());
            birthPicker.setValue(currentUser.getBirthDate());
            // No se muestra contraseña
            passField.clear();
        }
    }

    @FXML
    private void chooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar avatar");
        File f = chooser.showOpenDialog(avatarLabel.getScene().getWindow());
        if (f != null) {
            newAvatarPath = f.getAbsolutePath();
            avatarLabel.setText(f.getName());
        }
    }

    @FXML
    private void saveProfile() {
        if (currentUser == null) return;

        String email = emailField.getText().trim();
        String pass = passField.getText().trim();
        LocalDate birth = birthPicker.getValue();
        if (pass.isEmpty()) {
            pass = currentUser.getPassword(); // mantener actual
        }
        if (!User.checkEmail(email)) {
            showAlert("Email inválido", "Formato incorrecto.");
            return;
        }
        if (!pass.isEmpty() && !User.checkPassword(pass)) {
            showAlert("Contraseña inválida", "Debe cumplir los requisitos.");
            return;
        }
        if (birth == null || !User.isOlderThan(birth, 12)) {
            showAlert("Fecha no válida", "Debes ser mayor de 12 años.");
            return;
        }

        boolean ok = app.updateCurrentUser(email, pass, birth, newAvatarPath);
        if (ok) {
            showAlert("Perfil actualizado", "Los cambios se han guardado.");
            goBack();
        } else {
            showAlert("Error", "No se pudieron guardar los cambios.");
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load());
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