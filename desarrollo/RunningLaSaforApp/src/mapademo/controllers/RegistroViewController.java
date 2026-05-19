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

    // Campos de entrada
    @FXML private TextField nickField;
    @FXML private TextField emailField;
    @FXML private PasswordField passField;
    @FXML private DatePicker birthDatePicker;
    
    // Etiquetas de error
    @FXML private Label nickError;
    @FXML private Label emailError;
    @FXML private Label passError;
    @FXML private Label fechaError;
    
    // Botones y otros elementos
    @FXML private Button btnRegister;
    @FXML private Label avatarLabel;
    
    private String avatarPath = null;
    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    private void initialize() {
        // Inicialmente el botón de registro está deshabilitado
        btnRegister.setDisable(true);
        btnRegister.setStyle("-fx-opacity: 0.6;");
        
        // Añadir listeners para validación en tiempo real
        nickField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        passField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        birthDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateFields());
    }
    
    private void markField(Control field, Label errorLabel, boolean isValid, boolean hasText) {
        if (!hasText) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
            field.setStyle("");
        } else {
            errorLabel.setVisible(!isValid);
            if (!isValid) {
                field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                field.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
                errorLabel.setStyle("-fx-text-fill: #4CAF50;");
                errorLabel.setVisible(true);
            }
        }
    }
    
    private void markFieldFecha(boolean isValid, boolean hasText) {
        if (!hasText) {
            fechaError.setVisible(false);
            fechaError.setText("");
            birthDatePicker.setStyle("");
        } else {
            if (!isValid) {
                fechaError.setVisible(true);
                fechaError.setText("❌ Debes tener más de 12 años");
                fechaError.setStyle("-fx-text-fill: #e74c3c;");
                birthDatePicker.setStyle("");
            } else {
                fechaError.setVisible(true);
                fechaError.setText("✓ Edad válida");
                fechaError.setStyle("-fx-text-fill: #4CAF50;");
                birthDatePicker.setStyle("");
            }
        }
    }
    
    private void validateFields() {
        String nick = nickField.getText();
        String email = emailField.getText();
        String pass = passField.getText();
        LocalDate birth = birthDatePicker.getValue();
        
        boolean nickOk = User.checkNickName(nick);
        boolean emailOk = User.checkEmail(email);
        boolean passOk = User.checkPassword(pass);
        boolean fechaOk = birth != null && User.isOlderThan(birth, 12);
        
        // Marcar campos con errores visuales y establecer mensajes
        if (!nick.isEmpty()) {
            if (!nickOk) {
                nickError.setText("❌ 6-15 caracteres (letras, números, - o _)");
                markField(nickField, nickError, false, true);
            } else {
                nickError.setText("✓ Nickname válido");
                markField(nickField, nickError, true, true);
            }
        } else {
            nickError.setText("");
            markField(nickField, nickError, false, false);
        }
        
        if (!email.isEmpty()) {
            if (!emailOk) {
                emailError.setText("❌ usuario@dominio");
                markField(emailField, emailError, false, true);
            } else {
                emailError.setText("✓ Email válido");
                markField(emailField, emailError, true, true);
            }
        } else {
            emailError.setText("");
            markField(emailField, emailError, false, false);
        }
        
        if (!pass.isEmpty()) {
            if (!passOk) {
                passError.setText("❌ 8-20 caracteres, con mayúscula, minúscula, número y símbolo");
                markField(passField, passError, false, true);
            } else {
                passError.setText("✓ Contraseña segura");
                markField(passField, passError, true, true);
            }
        } else {
            passError.setText("");
            markField(passField, passError, false, false);
        }
        
        // Fecha (solo mensaje, sin bordes)
        if (birth != null) {
            markFieldFecha(fechaOk, true);
        } else {
            markFieldFecha(false, false);
        }
        
        // Habilitar/deshabilitar botón de registro
        boolean allValid = nickOk && emailOk && passOk && fechaOk;
        btnRegister.setDisable(!allValid);
        if (allValid) {
            btnRegister.setStyle("-fx-opacity: 1.0;");
        } else {
            btnRegister.setStyle("-fx-opacity: 0.6;");
        }
    }

    @FXML
    private void chooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Elegir avatar");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
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

        // Validaciones finales por seguridad
        if (!User.checkNickName(nick)) {
            showAlert("Error de validación", "El nickname no es válido.");
            return;
        }
        if (!User.checkEmail(email)) {
            showAlert("Error de validación", "El email no es válido.");
            return;
        }
        if (!User.checkPassword(pass)) {
            showAlert("Error de validación", "La contraseña no cumple los requisitos.");
            return;
        }
        if (birth == null || !User.isOlderThan(birth, 12)) {
            showAlert("Error de validación", "Debes tener más de 12 años.");
            return;
        }

        boolean ok = app.registerUser(nick, email, pass, birth, avatarPath);
        if (ok) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("✅ Tu cuenta se ha creado correctamente.\n\nAhora puedes iniciar sesión.");
            alert.showAndWait();
            goBack();
        } else {
            nickError.setText("❌ El nickname ya está en uso");
            nickError.setVisible(true);
            nickError.setStyle("-fx-text-fill: #e74c3c;");
            nickField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
            
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
            showAlert("Error", "No se pudo volver a la pantalla de inicio.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.showAndWait();
    }
}