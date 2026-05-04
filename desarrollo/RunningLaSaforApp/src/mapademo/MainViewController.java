/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainViewController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private void mostrarActividades() throws Exception {
        Node vista = FXMLLoader.load(getClass().getResource("ActividadesView.fxml"));
        rootPane.setCenter(vista);
    }
}
