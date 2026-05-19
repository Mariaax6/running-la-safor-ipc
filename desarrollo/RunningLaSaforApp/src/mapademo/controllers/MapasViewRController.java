/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author albeg
 */
public class MapasViewRController implements Initializable {

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Group contentGroup;
    @FXML
    private Group zoomGroup;
    @FXML
    private Pane mapPane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private Button borrarButton;
    @FXML
    private ListView<?> annotationList;
    @FXML
    private LineChart<?, ?> elevationChart;
    
    
    private SportActivityApp app = SportActivityApp.getInstance();
    private Activity currentActivity;
    private MapProjection projection;
    private MapRegion currentRegion;
    private ImageView mapImageView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoom_slider.setMin(0.25);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(0.75);
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> zoom(newVal.doubleValue()));
    }    

    @FXML
    private void zoomOut(ActionEvent event) {
    }

    @FXML
    private void zoomIn(ActionEvent event) {
    }

    @FXML
    private void borrar(ActionEvent event) {
    }
    
    public void setActivity(Activity act) {
        this.currentActivity = act;
        loadMap();
    }
    
    private void loadMap(){
        mapPane.getChildren().clear();
        mapImageView = null;

        // Obtener región de mapa
        currentRegion = currentActivity.getSuggestedMap();
        if (currentRegion == null) {
            currentRegion = app.findMapForActivity(currentActivity);
        }
        if (currentRegion == null) {
            mapPane.getChildren().add(new Label("No se encontró un mapa adecuado."));
            return;
        }

        // Cargar imagen
        File imgFile = new File(currentRegion.getImagePath());
        if (!imgFile.exists()) {
            mapPane.getChildren().add(new Label("Imagen de mapa no encontrada."));
            return;
        }
        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();

        mapImageView = new ImageView(img);
        mapImageView.setFitWidth(W);
        mapImageView.setFitHeight(H);

        mapPane.setPrefSize(W, H);
        mapPane.setMinSize(W, H);
        mapPane.setMaxSize(W, H);
        mapPane.getChildren().add(mapImageView);

        // Crear proyección
        projection = new MapProjection(currentRegion, W, H);

        // Ajustar tamaño del zoomGroup (necesario para que el ScrollPane calcule bien las barras)
        zoomGroup.setLayoutX(0);
        zoomGroup.setLayoutY(0);
    }
    
    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }
}
