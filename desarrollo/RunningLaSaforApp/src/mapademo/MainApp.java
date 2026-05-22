/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapademo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    // Tamaño deseado para la ventana principal
    public static final double APP_WIDTH = 1100;
    public static final double APP_HEIGHT = 750;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mapademo/fxml/LoginView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Running la Safor");
        primaryStage.setMinWidth(900);   // Evita que se encoja demasiado
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}