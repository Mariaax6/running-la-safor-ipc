package mapademo.controllers;

import upv.ipc.sportlib.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MapasViewController implements Initializable {

    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private Group contentGroup;
    @FXML private Group zoomGroup;
    @FXML private Pane mapPane;
    @FXML private ListView<Annotation> annotationList;
    @FXML private LineChart<Number, Number> elevationChart;
    @FXML private NumberAxis xAxis, yAxis;
    @FXML private Label altitudeInfoLabel;

    @FXML private Label lblDistancia, lblDuracion, lblVelocidadMedia, lblRitmoMedio,
                        lblDesnivelPositivo, lblDesnivelNegativo, lblAltitudMin, lblAltitudMax;

    private SportActivityApp app = SportActivityApp.getInstance();
    private Activity currentActivity;
    private MapProjection projection;
    private MapRegion currentRegion;
    private ImageView mapImageView;

    private final Color START_COLOR = Color.GREEN;
    private final Color END_COLOR = Color.RED;
    private Circle highlightCircle;

    // Lista propia de anotaciones para control total
    private final ObservableList<Annotation> annotationItems = FXCollections.observableArrayList();

    // Menú contextual reutilizable
    private ContextMenu annotationMenu;
    private double currentClickX, currentClickY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> zoom(newVal.doubleValue()));

        // Menú contextual
        annotationMenu = new ContextMenu();
        MenuItem pointItem = new MenuItem("Añadir punto");
        pointItem.setOnAction(ev -> addAnnotationAtCurrentClick(AnnotationType.POINT));
        MenuItem textItem = new MenuItem("Añadir texto");
        textItem.setOnAction(ev -> addAnnotationAtCurrentClick(AnnotationType.TEXT));
        MenuItem lineItem = new MenuItem("Añadir línea (clic para segundo punto)");
        lineItem.setOnAction(ev -> addAnnotationAtCurrentClick(AnnotationType.LINE));
        MenuItem circleItem = new MenuItem("Añadir círculo (clic para borde)");
        circleItem.setOnAction(ev -> addAnnotationAtCurrentClick(AnnotationType.CIRCLE));
        annotationMenu.getItems().addAll(pointItem, textItem, lineItem, circleItem);
    }

    private void setupMapClickHandler() {
        mapPane.setOnMouseClicked(e -> {
            System.out.println("Clic en mapPane: " + e.getButton());
            if (e.getButton() == MouseButton.SECONDARY) {
                currentClickX = e.getX();
                currentClickY = e.getY();
                annotationMenu.show(mapPane, e.getScreenX(), e.getScreenY());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                GeoPoint clicked = projection.unproject(e.getX(), e.getY());
                TrackPoint nearest = findNearestTrackPoint(clicked);
                if (nearest != null) {
                    highlightElevationPoint(nearest);
                }
            }
        });
    }

    private void addAnnotationAtCurrentClick(AnnotationType type) {
        GeoPoint geo = projection.unproject(currentClickX, currentClickY);
        if (type == AnnotationType.LINE || type == AnnotationType.CIRCLE) {
            startTwoPointAnnotation(type, geo);
        } else {
            addAnnotation(type, geo);
        }
    }

    public void setActivity(Activity act) {
        this.currentActivity = act;
        // Inicializar nuestra lista con las anotaciones existentes
        annotationItems.clear();
        annotationItems.addAll(act.getAnnotations());
        loadMap();
        drawRoute();
        drawAnnotations();
        buildElevationProfile();
        setupMapClickHandler();
        updateAnnotationList();
        showStats();
    }

    private void loadMap() {
        mapPane.getChildren().clear();
        mapImageView = null;

        currentRegion = currentActivity.getSuggestedMap();
        if (currentRegion == null) currentRegion = app.findMapForActivity(currentActivity);
        if (currentRegion == null) {
            mapPane.getChildren().add(new Label("No se encontró un mapa adecuado."));
            return;
        }

        File imgFile = new File(currentRegion.getImagePath());
        if (!imgFile.exists()) {
            mapPane.getChildren().add(new Label("Imagen de mapa no encontrada."));
            return;
        }
        Image img = new Image(imgFile.toURI().toString());
        mapImageView = new ImageView(img);
        mapImageView.setPickOnBounds(false);
        mapImageView.setMouseTransparent(true);

        double W = img.getWidth();
        double H = img.getHeight();
        mapImageView.setFitWidth(W);
        mapImageView.setFitHeight(H);
        mapPane.setPrefSize(W, H);
        mapPane.setMinSize(W, H);
        mapPane.setMaxSize(W, H);
        mapPane.getChildren().add(mapImageView);

        projection = new MapProjection(currentRegion, W, H);
        zoomGroup.setLayoutX(0);
        zoomGroup.setLayoutY(0);
    }

    private void drawRoute() {
        if (currentActivity == null || projection == null) return;
        List<TrackPoint> points = currentActivity.getTrackPoints();
        if (points.size() < 2) return;

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
        TrackPoint start = currentActivity.getStartPoint();
        TrackPoint end = currentActivity.getEndPoint();
        if (start != null) {
            Point2D p = projection.project(start);
            Circle c = new Circle(p.getX(), p.getY(), 7, START_COLOR);
            c.setStroke(Color.BLACK);
            mapPane.getChildren().add(c);
        }
        if (end != null) {
            Point2D p = projection.project(end);
            Circle c = new Circle(p.getX(), p.getY(), 7, END_COLOR);
            c.setStroke(Color.BLACK);
            mapPane.getChildren().add(c);
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
        // Dibujar desde nuestra lista propia
        for (Annotation ann : annotationItems) {
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
                    Circle c = new Circle(pt.getX(), pt.getY(), 5, color);
                    c.setStroke(Color.BLACK);
                    mapPane.getChildren().add(c);
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
                double radius = Math.sqrt(Math.pow(border.getX()-center.getX(),2) + Math.pow(border.getY()-center.getY(),2));
                Circle c = new Circle(center.getX(), center.getY(), radius);
                c.setStroke(color);
                c.setStrokeWidth(width);
                c.setFill(Color.TRANSPARENT);
                mapPane.getChildren().add(c);
                break;
        }
    }

    private void buildElevationProfile() {
        elevationChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double distance = 0;
        TrackPoint prev = null;
        for (TrackPoint tp : currentActivity.getTrackPoints()) {
            if (prev != null) distance += prev.distanceTo(tp);
            prev = tp;
            series.getData().add(new XYChart.Data<>(distance / 1000.0, tp.getElevation()));
        }
        elevationChart.getData().add(series);
        elevationChart.setAnimated(false);
        if (xAxis != null) xAxis.setAutoRanging(true);
        if (yAxis != null) yAxis.setAutoRanging(true);
        elevationChart.layout();

        elevationChart.setOnMouseMoved(e -> {
            if (highlightCircle != null) {
                mapPane.getChildren().remove(highlightCircle);
                highlightCircle = null;
            }
            if (xAxis == null) return;
            double mouseX = xAxis.getValueForDisplay(e.getX()).doubleValue();
            double minDist = Double.MAX_VALUE;
            TrackPoint closest = null;
            TrackPoint prevPoint = null;
            double dist = 0;
            for (TrackPoint tp : currentActivity.getTrackPoints()) {
                if (prevPoint != null) dist += prevPoint.distanceTo(tp);
                prevPoint = tp;
                double d = Math.abs(mouseX - dist / 1000.0);
                if (d < minDist) { minDist = d; closest = tp; }
            }
            if (closest != null) {
                Point2D pt = projection.project(closest);
                highlightCircle = new Circle(pt.getX(), pt.getY(), 5, Color.YELLOW);
                highlightCircle.setStroke(Color.BLACK);
                mapPane.getChildren().add(highlightCircle);
            }
        });
    }

    private TrackPoint findNearestTrackPoint(GeoPoint target) {
        List<TrackPoint> points = currentActivity.getTrackPoints();
        if (points.isEmpty()) return null;
        TrackPoint nearest = null;
        double minDist = Double.MAX_VALUE;
        for (TrackPoint tp : points) {
            GeoPoint tpGeo = new GeoPoint(tp.getLatitude(), tp.getLongitude());
            double d = GeoUtils.distance(target, tpGeo);
            if (d < minDist) { minDist = d; nearest = tp; }
        }
        return nearest;
    }

    private void highlightElevationPoint(TrackPoint tp) {
        double dist = 0;
        TrackPoint prev = null;
        for (TrackPoint p : currentActivity.getTrackPoints()) {
            if (prev != null) dist += prev.distanceTo(p);
            prev = p;
            if (p == tp) break;
        }
        altitudeInfoLabel.setText(String.format("Altitud: %.0f m  |  Distancia: %.2f km", tp.getElevation(), dist/1000.0));
    }

    private void addAnnotation(AnnotationType type, GeoPoint... points) {
        System.out.println("Abriendo diálogo para anotación tipo " + type);
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
            if (dialogButton == okButton)
                return new Annotation(type, textField.getText(), colorField.getText(), 2.0, List.of(points));
            return null;
        });
        Optional<Annotation> result = dialog.showAndWait();
        result.ifPresent(ann -> {
            Annotation saved = app.addAnnotation(currentActivity, ann);
            if (saved != null) {
                // Añadir a nuestra lista y redibujar
                annotationItems.add(saved);
                drawAnnotations();
                updateAnnotationList();
                System.out.println("Anotación guardada. Total en lista: " + annotationItems.size());
            } else {
                System.out.println("Error: la anotación no se pudo guardar.");
            }
        });
    }

    private void startTwoPointAnnotation(AnnotationType type, GeoPoint firstPoint) {
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                GeoPoint secondPoint = projection.unproject(e.getX(), e.getY());
                addAnnotation(type, firstPoint, secondPoint);
                setupMapClickHandler(); // restaurar
            }
        });
    }

    private void updateAnnotationList() {
        // Vinculamos el ListView con nuestra lista observable
        annotationList.setItems(annotationItems);
        annotationList.setCellFactory(lv -> new ListCell<Annotation>() {
            @Override
            protected void updateItem(Annotation ann, boolean empty) {
                super.updateItem(ann, empty);
                setText(empty || ann == null ? null : ann.getType() + " - " + ann.getText());
            }
        });
    }

    private void showStats() {
        if (currentActivity == null) return;
        try {
            lblDistancia.setText(String.format("Distancia: %.2f km", currentActivity.getTotalDistance()/1000.0));
            Duration dur = currentActivity.getDuration();
            lblDuracion.setText("Duración: " + (dur != null ? formatDuration(dur) : "--"));
            lblVelocidadMedia.setText(String.format("Velocidad media: %.1f km/h", currentActivity.getAverageSpeed()));
            lblRitmoMedio.setText("Ritmo medio: " + formatPace(currentActivity.getAveragePace()));
            lblDesnivelPositivo.setText(String.format("Desnivel positivo: %.0f m", currentActivity.getElevationGain()));
            lblDesnivelNegativo.setText(String.format("Desnivel negativo: %.0f m", currentActivity.getElevationLoss()));
            lblAltitudMin.setText(String.format("Altitud mínima: %.0f m", currentActivity.getMinElevation()));
            lblAltitudMax.setText(String.format("Altitud máxima: %.0f m", currentActivity.getMaxElevation()));
        } catch (Exception e) {
            System.err.println("Error mostrando estadísticas: " + e.getMessage());
        }
    }

    private String formatDuration(Duration d) {
        if (d == null) return "--";
        return String.format("%dh %02dm %02ds", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
    }

    private String formatPace(Double pace) {
        if (pace == null || pace.isInfinite() || pace.isNaN()) return "--";
        int min = (int) pace.doubleValue();
        int sec = (int) ((pace - min) * 60);
        return String.format("%d:%02d min/km", min, sec);
    }

    @FXML private void zoomIn() { zoom_slider.setValue(Math.min(zoom_slider.getValue() + 0.1, zoom_slider.getMax())); }
    @FXML private void zoomOut(){ zoom_slider.setValue(Math.max(zoom_slider.getValue() - 0.1, zoom_slider.getMin())); }

    private void zoom(double scaleValue) {
        double h = map_scrollpane.getHvalue(), v = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(h);
        map_scrollpane.setVvalue(v);
    }
}