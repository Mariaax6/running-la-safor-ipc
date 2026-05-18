
package mapademo.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mapademo.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class MainViewController {

    @FXML private BorderPane mainPane;
    @FXML private MenuButton userMenuButton;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        cargarUsuarioActual();

        Platform.runLater(() -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (app.getCurrentUser() != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Salir de la aplicación");
                    confirm.setHeaderText("¿Seguro que quieres salir?");
                    confirm.setContentText("Se cerrará tu sesión actual y se guardarán los datos.");
                    ButtonType btnSalir = new ButtonType("Salir", ButtonBar.ButtonData.OK_DONE);
                    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                    confirm.getButtonTypes().setAll(btnSalir, btnCancelar);
                    confirm.getDialogPane().getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == btnSalir) {
                        app.logout();
                        stage.close();
                    } else {
                        event.consume();
                    }
                }
            });
        });
    }

    private void cargarUsuarioActual() {
        User user = app.getCurrentUser();
        if (user != null) {
            Label nameLabel = new Label(user.getNickName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            ImageView avatarView = new ImageView();
            avatarView.setFitWidth(30);
            avatarView.setFitHeight(30);
            avatarView.setPreserveRatio(true);

            String avatarPath = user.getAvatarPath();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    avatarView.setImage(new Image(avatarFile.toURI().toString()));
                }
            } else {
                try {
                    avatarView.setImage(new Image(getClass().getResourceAsStream("/resources/default_avatar.png")));
                } catch (Exception ignored) {}
            }

            HBox hbox = new HBox(8);
            hbox.setAlignment(javafx.geometry.Pos.CENTER);
            hbox.getChildren().addAll(avatarView, nameLabel);
            userMenuButton.setGraphic(hbox);
            userMenuButton.setText(null);

            // Crear menú contextual (sustituye cualquier menú anterior)
            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);");

            MenuItem perfil = new MenuItem("Perfil");
            perfil.setStyle("-fx-text-fill: #1d1d1f; -fx-font-size: 14px;");
            perfil.setOnAction(e -> showProfile());

            MenuItem logout = new MenuItem("Cerrar sesión");
            logout.setStyle("-fx-text-fill: #1d1d1f; -fx-font-size: 14px;");
            logout.setOnAction(e -> confirmLogout());

            menu.getItems().addAll(perfil, new SeparatorMenuItem(), logout);

            userMenuButton.setContextMenu(menu);
        }
    }

    @FXML
    private void confirmLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar sesión");
        confirm.setHeaderText("¿Estás seguro de que deseas cerrar sesión?");
        confirm.setContentText("Se guardarán los datos de la sesión actual.");
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnAceptar, btnCancelar);
        confirm.getDialogPane().getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnAceptar) {
            app.logout();
            cambiarEscena("/mapademo/fxml/LoginView.fxml", "Running la Safor - Inicio de sesión");
        }
    }

    @FXML private void showActivities() { loadView("/mapademo/fxml/ActividadesView.fxml"); }
    @FXML private void showProfile() { loadView("/mapademo/fxml/PerfilView.fxml"); }
    @FXML private void showHistory() { loadView("/mapademo/fxml/HistorialView.fxml"); }
    @FXML private void showMapManagement() { loadView("/mapademo/fxml/MapManagement.fxml"); }

    @FXML
    private void importActivity() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar archivo GPX");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx"));
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file != null) {
            try {
                Activity act = app.importActivity(file);
                loadMapView(act);
            } catch (Exception ex) {
                mostrarError("Error al importar", ex.getMessage());
            }
        }
    }

    private void loadMapView(Activity act) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/MapasView.fxml"));
            Parent view = loader.load();
            MapasViewController mapCtrl = loader.getController();
            mapCtrl.setActivity(act);
            mainPane.setCenter(view);
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar la vista del mapa.\n" + e.getMessage());
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            mainPane.setCenter(loader.load());
        } catch (IOException e) {
            mostrarError("Error al cargar vista", "No se pudo cargar: " + fxml + "\n" + e.getMessage());
        }
    }

    private void cambiarEscena(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load(), MainApp.APP_WIDTH, MainApp.APP_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        alert.showAndWait();
    }
}