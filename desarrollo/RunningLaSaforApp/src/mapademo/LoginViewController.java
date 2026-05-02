package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginViewController {

    @FXML
    private TextField nicknameField;

    @FXML
    private PasswordField passwordField;

    @FXML
private void handleLogin() throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
    Scene scene = nicknameField.getScene();
    scene.setRoot(root);
}
}