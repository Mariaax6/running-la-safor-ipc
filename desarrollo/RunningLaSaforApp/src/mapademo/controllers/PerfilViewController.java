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
    @FXML private Button btnGuardar;

    @FXML private Label emailError;
    @FXML private Label passError;
    @FXML private Label fechaError;

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
            passField.clear();
        }
        
        // Nickname deshabilitado visualmente
        nickField.setDisable(true);
        nickField.setStyle("-fx-opacity: 0.7; -fx-background-color: #f0f0f0;");

        // Deshabilitar botón al inicio
        btnGuardar.setDisable(true);

        // Listeners para validación en tiempo real
        emailField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        passField.textProperty().addListener((obs, o, n) -> comprobarCampos());
        birthPicker.valueProperty().addListener((obs, o, n) -> comprobarCampos());
    }

    private void marcarCampoEmail(Control field, Label errorLabel, boolean correcto, boolean tieneTexto) {
        if (!tieneTexto) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
            field.setStyle("");
        } else {
            errorLabel.setVisible(true);
            if (!correcto) {
                field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                field.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        }
    }
    
    private void marcarCampoPass(Control field, Label errorLabel, boolean correcto, boolean tieneTexto) {
        if (!tieneTexto) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
            field.setStyle("");
        } else {
            errorLabel.setVisible(true);
            if (!correcto) {
                field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                field.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        }
    }
    
    private void marcarCampoFecha(boolean correcto, boolean tieneTexto) {
        if (!tieneTexto) {
            fechaError.setVisible(false);
            fechaError.setText("");
            birthPicker.setStyle("");
        } else {
            if (!correcto) {
                fechaError.setVisible(true);
                fechaError.setText("❌ Debes tener más de 12 años");
                fechaError.setStyle("-fx-text-fill: #e74c3c;");
                birthPicker.setStyle("");
            } else {
                fechaError.setVisible(true);
                fechaError.setText("✓ Edad válida");
                fechaError.setStyle("-fx-text-fill: #4CAF50;");
                birthPicker.setStyle("");
            }
        }
    }

    private void comprobarCampos() {
        String email = emailField.getText();
        String password = passField.getText();
        LocalDate fecha = birthPicker.getValue();

        boolean emailOk = User.checkEmail(email);
        boolean passOk = password.isEmpty() || User.checkPassword(password);
        boolean fechaOk = fecha != null && User.isOlderThan(fecha, 12);

        // Email
        if (!email.isEmpty()) {
            if (!emailOk) {
                emailError.setText("❌ usuario@dominio");
                marcarCampoEmail(emailField, emailError, false, true);
            } else {
                emailError.setText("✓ Email válido");
                marcarCampoEmail(emailField, emailError, true, true);
            }
        } else {
            emailError.setText("");
            marcarCampoEmail(emailField, emailError, false, false);
        }

        // Contraseña
        if (!password.isEmpty()) {
            if (!passOk) {
                passError.setText("❌ 8-20 caracteres, con mayúscula, minúscula, número y símbolo");
                marcarCampoPass(passField, passError, false, true);
            } else {
                passError.setText("✓ Contraseña segura");
                marcarCampoPass(passField, passError, true, true);
            }
        } else {
            passError.setText("");
            marcarCampoPass(passField, passError, false, false);
        }

        // Fecha (solo mensaje, sin bordes)
        if (fecha != null) {
            marcarCampoFecha(fechaOk, true);
        } else {
            marcarCampoFecha(false, false);
        }

        // Habilitar/deshabilitar botón
        boolean valido = emailOk && (password.isEmpty() || passOk) && fechaOk;
        btnGuardar.setDisable(!valido);
    }

    @FXML
    private void chooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar avatar");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
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

        // Validaciones
        if (!email.isEmpty() && !User.checkEmail(email)) {
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

        // Si la contraseña está vacía, mantener la actual
        String finalPass = pass.isEmpty() ? currentUser.getPassword() : pass;
        
        // Si el email está vacío, mantener el actual
        String finalEmail = email.isEmpty() ? currentUser.getEmail() : email;

        boolean ok = app.updateCurrentUser(finalEmail, finalPass, birth, newAvatarPath);
        if (ok) {
            showAlert("Perfil actualizado", "Los cambios se han guardado correctamente.");
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