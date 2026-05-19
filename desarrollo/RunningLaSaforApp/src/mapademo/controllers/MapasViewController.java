package mapademo.controllers;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.AnnotationType;
import static upv.ipc.sportlib.AnnotationType.CIRCLE;
import static upv.ipc.sportlib.AnnotationType.LINE;
import static upv.ipc.sportlib.AnnotationType.POINT;
import static upv.ipc.sportlib.AnnotationType.TEXT;
import upv.ipc.sportlib.GeoPoint;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.TrackPoint;

public class MapasViewController implements Initializable {

    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private Group contentGroup;
    @FXML private Group zoomGroup;
    @FXML private Pane mapPane;
    @FXML private ListView<Annotation> annotationList;
    @FXML private LineChart<Number, Number> elevationChart;
    private NumberAxis xAxis;

    private SportActivityApp app = SportActivityApp.getInstance();
    private Activity currentActivity;
    private MapProjection projection;
    private MapRegion currentRegion;
    private ImageView mapImageView;

    private final Color START_COLOR = Color.GREEN;
    private final Color END_COLOR = Color.RED;
    private Circle highlightCircle; // punto resaltado en mapa al pasar ratón sobre gráfico
    @FXML
    private Button borrarButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar slider de zoom
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> zoom(newVal.doubleValue()));

        // Inicializar nodos de zoom
        // En el FXML ya están definidos contentGroup, zoomGroup, mapPane
        // El mapPane se llenará en setActivity
    }

    public void setActivity(Activity act) {
        this.currentActivity = act;
        loadMap();
        drawRoute();
        drawAnnotations();
        buildElevationProfile();
        setupMapClickHandler();
        updateAnnotationList();
    }

    private void loadMap() {
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

    private void drawRoute() {
        if (currentActivity == null || projection == null) {
            System.out.println("drawRoute: abortado. activity=" + currentActivity + " projection=" + projection);
            return;
        }
        List<TrackPoint> points = currentActivity.getTrackPoints();
        System.out.println("drawRoute: " + points.size() + " puntos");
        if (points.size() < 2) return;

        // Dibujar segmentos coloreados por velocidad
        for (int i = 1; i < points.size(); i++) {
            TrackPoint tp1 = points.get(i - 1);
            TrackPoint tp2 = points.get(i);
            double speed = tp1.speedTo(tp2);
            Color color = speedToColor(speed);

            Point2D p1 = projection.project(tp1);
            Point2D p2 = projection.project(tp2);
            Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            line.setStroke(color);
            line.setStrokeWidth(3);
            mapPane.getChildren().add(line);
        }

        // Punto inicio y fin
        TrackPoint start = currentActivity.getStartPoint();
        TrackPoint end = currentActivity.getEndPoint();
        if (start != null) {
            Point2D p = projection.project(start);
            Circle startCircle = new Circle(p.getX(), p.getY(), 7, START_COLOR);
            startCircle.setStroke(Color.BLACK);
            mapPane.getChildren().add(startCircle);
        }
        if (end != null) {
            Point2D p = projection.project(end);
            Circle endCircle = new Circle(p.getX(), p.getY(), 7, END_COLOR);
            endCircle.setStroke(Color.BLACK);
            mapPane.getChildren().add(endCircle);
        }
    }

    private Color speedToColor(double speed) {
        if (speed < 8) return Color.GREEN;
        else if (speed < 12) return Color.YELLOW;
        else if (speed < 16) return Color.ORANGE;
        else return Color.RED;
    }

    private void drawAnnotations() {
        if (currentActivity == null || projection == null) return;
        for (Annotation ann : currentActivity.getAnnotations()) {
            drawAnnotation(ann);
        }
    }

    private void drawAnnotation(Annotation ann) {
        if (ann.getGeoPoints().isEmpty()) return;
        Color color = Color.web(ann.getColor());
        double width = ann.getStrokeWidth();

        switch (ann.getType()) {
            case POINT:
            case TEXT:
                GeoPoint gp = ann.getGeoPoints().get(0);
                Point2D pt = projection.project(gp);
                if (ann.getType() == AnnotationType.POINT) {
                    Circle circle = new Circle(pt.getX(), pt.getY(), 5, color);
                    circle.setStroke(Color.BLACK);
                    mapPane.getChildren().add(circle);
                }
                if (ann.getText() != null && !ann.getText().isEmpty()) {
                    Text text = new Text(pt.getX() + 7, pt.getY() - 5, ann.getText());
                    text.setFill(color);
                    mapPane.getChildren().add(text);
                }
                break;

            case LINE:
                if (ann.getGeoPoints().size() < 2) return;
                Point2D p1 = projection.project(ann.getGeoPoints().get(0));
                Point2D p2 = projection.project(ann.getGeoPoints().get(1));
                Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                line.setStroke(color);
                line.setStrokeWidth(width);
                mapPane.getChildren().add(line);
                break;

            case CIRCLE:
                if (ann.getGeoPoints().size() < 2) return;
                Point2D center = projection.project(ann.getGeoPoints().get(0));
                Point2D border = projection.project(ann.getGeoPoints().get(1));
                double radius = Math.sqrt(
                    Math.pow(border.getX() - center.getX(), 2) +
                    Math.pow(border.getY() - center.getY(), 2));
                Circle c = new Circle(center.getX(), center.getY(), radius);
                c.setStroke(color);
                c.setStrokeWidth(width);
                c.setFill(Color.TRANSPARENT);
                mapPane.getChildren().add(c);
                break;
        }
    }

   private void buildElevationProfile() {
    xAxis = (NumberAxis) elevationChart.getXAxis();
    elevationChart.getData().clear();
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    double distance = 0;
    TrackPoint prev = null;
    for (TrackPoint tp : currentActivity.getTrackPoints()) {
        if (prev != null) {
            distance += prev.distanceTo(tp);
        }
        prev = tp;
        series.getData().add(new XYChart.Data<>(distance / 1000.0, tp.getElevation()));
    }
    elevationChart.getData().add(series);

    // --- aquí mantienes tu código de resaltado con el ratón ---
    elevationChart.setOnMouseMoved(e -> {
        if (highlightCircle != null) {
            mapPane.getChildren().remove(highlightCircle);
            highlightCircle = null;
        }
        double mouseX = xAxis.getValueForDisplay(e.getX()).doubleValue();
        double minDist = Double.MAX_VALUE;
        TrackPoint closest = null;
        TrackPoint prevPoint = null;   // variable local a la lambda
        double dist = 0;
        for (TrackPoint tp : currentActivity.getTrackPoints()) {
            if (prevPoint != null) {
                dist += prevPoint.distanceTo(tp);
            }
            prevPoint = tp;
            double d = Math.abs(mouseX - dist / 1000.0);
            if (d < minDist) {
                minDist = d;
                closest = tp;
            }
        }
        if (closest != null) {
            Point2D pt = projection.project(closest);
            highlightCircle = new Circle(pt.getX(), pt.getY(), 5, Color.YELLOW);
            highlightCircle.setStroke(Color.BLACK);
            mapPane.getChildren().add(highlightCircle);
        }
    });
}
   
    private ContextMenu activeContextMenu;

    private void setupMapClickHandler() {
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // Cerrar menú anterior si existe
                if (activeContextMenu != null) {
                    activeContextMenu.hide();
                    activeContextMenu = null;
                }
                
                // Mostrar menú contextual para añadir anotación
                final double clickX = e.getX();
                final double clickY = e.getY();
                GeoPoint geo = projection.unproject(clickX, clickY);

                ContextMenu menu = new ContextMenu();

                MenuItem pointItem = new MenuItem("Añadir punto");
                pointItem.setOnAction(ev -> addAnnotation(AnnotationType.POINT, geo));
                MenuItem textItem = new MenuItem("Añadir texto");
                textItem.setOnAction(ev -> addAnnotation(AnnotationType.TEXT, geo));
                MenuItem lineItem = new MenuItem("Añadir línea (clic para segundo punto)");
                lineItem.setOnAction(ev -> startLineAnnotation(geo));
                MenuItem circleItem = new MenuItem("Añadir círculo (clic para borde)");
                circleItem.setOnAction(ev -> startCircleAnnotation(geo));

                menu.getItems().addAll(pointItem, textItem, lineItem, circleItem);
                menu.show(mapPane, e.getScreenX(), e.getScreenY());
                
                // Guardar referencia al menú activo
                activeContextMenu = menu;
                
                // Limpiar referencia cuando se cierre
                menu.setOnHidden(ev -> activeContextMenu = null);
            }
        });
    }

    private void addAnnotation(AnnotationType type, GeoPoint... points) {
        // Pedir texto y color al usuario
        Dialog<Annotation> dialog = new Dialog<>();
        dialog.setTitle("Nueva anotación");
        dialog.setHeaderText("Introduce los detalles");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField textField = new TextField();
        textField.setPromptText("Texto (opcional)");
        TextField colorField = new TextField("#FF0000");
        colorField.setPromptText("Color CSS (ej. #FF0000)");

        VBox vbox = new VBox(10, new Label("Texto:"), textField, new Label("Color:"), colorField);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Annotation(
                    type,
                    textField.getText(),
                    colorField.getText(),
                    2.0, // grosor
                    List.of(points)
                );
            }
            return null;
        });

        Optional<Annotation> result = dialog.showAndWait();
        result.ifPresent(ann -> {
            Annotation saved = app.addAnnotation(currentActivity, ann);
            if (saved != null) {
                drawAnnotation(saved);
                updateAnnotationList();
            }
        });
    }

    private void startLineAnnotation(GeoPoint start) {
        // Segundo clic para completar línea
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) { // Podría ser izquierdo
                GeoPoint end = projection.unproject(e.getX(), e.getY());
                addAnnotation(AnnotationType.LINE, start, end);
                // Restaurar el manejador de clics original
                setupMapClickHandler();
            }
        });
    }

    private void startCircleAnnotation(GeoPoint center) {
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                GeoPoint border = projection.unproject(e.getX(), e.getY());
                addAnnotation(AnnotationType.CIRCLE, center, border);
                setupMapClickHandler();
            }
        });
    }

    private void updateAnnotationList() {
        annotationList.getItems().clear();
        annotationList.getItems().addAll(currentActivity.getAnnotations());
        annotationList.setCellFactory(lv -> new ListCell<Annotation>() {
            @Override
            protected void updateItem(Annotation ann, boolean empty) {
                super.updateItem(ann, empty);
                if (empty || ann == null) {
                    setText(null);
                } else {
                    setText(ann.getType() + " - " + ann.getText());
                }
            }
        });
    }

    // Métodos de zoom
    @FXML
    private void zoomIn() {
        double val = zoom_slider.getValue() + 0.1;
        if (val > zoom_slider.getMax()) val = zoom_slider.getMax();
        zoom_slider.setValue(val);
    }

    @FXML
    private void zoomOut() {
        double val = zoom_slider.getValue() - 0.1;
        if (val < zoom_slider.getMin()) val = zoom_slider.getMin();
        zoom_slider.setValue(val);
    }

    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    @FXML
    private void borrar(ActionEvent event) {
        
    }
}