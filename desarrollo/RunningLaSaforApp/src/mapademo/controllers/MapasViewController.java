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
<<<<<<< HEAD
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
=======
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871

public class MapasViewController implements Initializable {

    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private Group contentGroup;
    @FXML private Group zoomGroup;
    @FXML private Pane mapPane;
    @FXML private ListView<Annotation> annotationList;
    @FXML private LineChart<Number, Number> elevationChart;
<<<<<<< HEAD
    private NumberAxis xAxis;
=======
    @FXML private NumberAxis xAxis, yAxis;
    @FXML private Label altitudeInfoLabel;

    @FXML private Label lblDistancia, lblDuracion, lblVelocidadMedia, lblRitmoMedio,
                        lblDesnivelPositivo, lblDesnivelNegativo, lblAltitudMin, lblAltitudMax;
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871

    private SportActivityApp app = SportActivityApp.getInstance();
    private Activity currentActivity;
    private MapProjection projection;
    private MapRegion currentRegion;
    private ImageView mapImageView;

    private final Color START_COLOR = Color.GREEN;
    private final Color END_COLOR = Color.RED;
<<<<<<< HEAD
    private Circle highlightCircle; // punto resaltado en mapa al pasar ratón sobre gráfico
    
    private java.util.Map<Integer, List<javafx.scene.Node>> annotationNodes = new java.util.HashMap<>();
    private javafx.scene.Node previewNode = null;
    private final javafx.collections.ObservableList<Annotation> annotationItems = 
    javafx.collections.FXCollections.observableArrayList();
    private Circle chartHighlightCircle;
    private javafx.beans.value.ChangeListener<Annotation> selectionListener;
    
    
    @FXML
    private Button borrarButton;
    @FXML
    private Button editarButton;
=======
    private Circle highlightCircle;

    // Lista propia de anotaciones para control total
    private final ObservableList<Annotation> annotationItems = FXCollections.observableArrayList();

    // Menú contextual reutilizable
    private ContextMenu annotationMenu;
    private double currentClickX, currentClickY;
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> zoom(newVal.doubleValue()));

<<<<<<< HEAD
        // Listener de selección registrado una sola vez
        borrarButton.setDisable(true);
        annotationList.setItems(annotationItems);
        selectionListener = (obs, oldVal, newVal) -> {
            borrarButton.setDisable(newVal == null);
            editarButton.setDisable(newVal == null);
            if (newVal != null && projection != null) {
                focusAnnotationOnMap(newVal);
            }
        };
        annotationList.getSelectionModel().selectedItemProperty().addListener(selectionListener);
        
        // Deseleccionar al clicar fuera de la lista
        annotationList.setOnMouseClicked(e -> {
            javafx.scene.Node node = e.getPickResult().getIntersectedNode();
            // Subir por el árbol de nodos hasta encontrar un ListCell
            while (node != null && !(node instanceof ListCell)) {
                node = node.getParent();
            }
            if (node == null || ((ListCell<?>) node).isEmpty()) {
                annotationList.getSelectionModel().clearSelection();
            }
        });
        
        // Deseleccionar al clicar fuera de la lista
        annotationList.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                    javafx.scene.Node node = e.getPickResult().getIntersectedNode();
                    // Subir por el árbol hasta encontrar el borrarButton
                    javafx.scene.Node n = node;
                    while (n != null) {
                        if (n == borrarButton) return;
                        if (n == editarButton) return;
                        n = n.getParent();
                    }
                    if (!annotationList.getBoundsInParent().contains(
                            annotationList.getParent().sceneToLocal(e.getSceneX(), e.getSceneY()))) {
                        annotationList.getSelectionModel().clearSelection();
                    }
                });
                
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == javafx.scene.input.KeyCode.DELETE) {
                        borrar(null);
                    }
                });
            }
        });
        
        annotationList.setCellFactory(lv -> new ListCell<Annotation>() {
            @Override
            protected void updateItem(Annotation ann, boolean empty) {
                super.updateItem(ann, empty);
                if (empty || ann == null) setText(null);
                else {
                    String text = ann.getText();
                    if (text == null || text.isEmpty())
                        setText(ann.getType().toString());
                    else
                        setText(ann.getType() + " - " + text);
                }
            }
        });
        
        // Un solo bloque, sin duplicados
        map_scrollpane.setPannable(false);

        final double[] dragStart = new double[2];

        map_scrollpane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragStart[0] = e.getSceneX();
                dragStart[1] = e.getSceneY();
                map_scrollpane.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
            }
        });

        map_scrollpane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double dx = e.getSceneX() - dragStart[0];
                double dy = e.getSceneY() - dragStart[1];
                dragStart[0] = e.getSceneX();
                dragStart[1] = e.getSceneY();

                double contentWidth  = zoomGroup.getBoundsInParent().getWidth();
                double contentHeight = zoomGroup.getBoundsInParent().getHeight();
                double viewportWidth  = map_scrollpane.getViewportBounds().getWidth();
                double viewportHeight = map_scrollpane.getViewportBounds().getHeight();

                map_scrollpane.setHvalue(map_scrollpane.getHvalue() - dx / (contentWidth  - viewportWidth));
                map_scrollpane.setVvalue(map_scrollpane.getVvalue() - dy / (contentHeight - viewportHeight));
                map_scrollpane.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
            }
        });

        map_scrollpane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_RELEASED, e -> {
            map_scrollpane.setCursor(javafx.scene.Cursor.DEFAULT); // para cualquier botón
        });
        
        map_scrollpane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, e -> {
            e.consume(); // evita que el ScrollPane haga scroll normal

            double delta = e.getDeltaY() > 0 ? 0.1 : -0.1;
            double newVal = zoom_slider.getValue() + delta;
            newVal = Math.max(zoom_slider.getMin(), Math.min(zoom_slider.getMax(), newVal));
            zoom_slider.setValue(newVal);
        });
=======
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
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
    }

    public void setActivity(Activity act) {
        this.currentActivity = act;
<<<<<<< HEAD
        annotationItems.clear();
=======
        // Inicializar nuestra lista con las anotaciones existentes
        annotationItems.clear();
        annotationItems.addAll(act.getAnnotations());
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
        loadMap();
        drawRoute();
        drawAnnotations(); // drawAnnotations ya llena annotationNodes
        buildElevationProfile();
        setupMapClickHandler();
<<<<<<< HEAD
        annotationItems.addAll(currentActivity.getAnnotations()); // poblar con los objetos iniciales

        javafx.application.Platform.runLater(() -> {
            map_scrollpane.setHvalue(0.5);
            map_scrollpane.setVvalue(0.5);
        });
=======
        updateAnnotationList();
        showStats();
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
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
        if (currentActivity == null || projection == null) {
            System.out.println("drawRoute: abortado. activity=" + currentActivity + " projection=" + projection);
            return;
        }
        List<TrackPoint> points = currentActivity.getTrackPoints();
        System.out.println("drawRoute: " + points.size() + " puntos");
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
<<<<<<< HEAD
        annotationNodes.clear(); // limpiar mapa de nodos antes de redibujar
        for (Annotation ann : currentActivity.getAnnotations()) {
=======
        // Dibujar desde nuestra lista propia
        for (Annotation ann : annotationItems) {
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
            drawAnnotation(ann);
        }
    }

    private void drawAnnotation(Annotation ann) {
        if (ann.getGeoPoints().isEmpty()) return;
        Color color = Color.web(ann.getColor());
        double width = ann.getStrokeWidth();
<<<<<<< HEAD
        List<javafx.scene.Node> nodes = new ArrayList<>();

=======
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
        switch (ann.getType()) {
            case POINT:
            case TEXT:
                GeoPoint gp = ann.getGeoPoints().get(0);
                Point2D pt = projection.project(gp);
                if (ann.getType() == AnnotationType.POINT) {
<<<<<<< HEAD
                    Circle circle = new Circle(pt.getX(), pt.getY(), 5, color);
                    circle.setStroke(Color.BLACK);
                    mapPane.getChildren().add(circle);
                    nodes.add(circle);

                    // Área de clic más ancha
                    Circle pointHitArea = new Circle(pt.getX(), pt.getY(), 12);
                    pointHitArea.setFill(Color.TRANSPARENT);
                    pointHitArea.setStroke(null);
                    mapPane.getChildren().add(pointHitArea);
                    nodes.add(pointHitArea);
=======
                    Circle c = new Circle(pt.getX(), pt.getY(), 5, color);
                    c.setStroke(Color.BLACK);
                    mapPane.getChildren().add(c);
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
                }
                if (ann.getText() != null && !ann.getText().isEmpty()) {
                    Text text = new Text(pt.getX() + 7, pt.getY() - 5, ann.getText());
                    text.setFill(color);
                    mapPane.getChildren().add(text);
                    nodes.add(text);
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
                nodes.add(line);
                
                Line lineHitArea = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                lineHitArea.setStroke(Color.TRANSPARENT);
                lineHitArea.setStrokeWidth(10); // área de clic más ancha
                mapPane.getChildren().add(lineHitArea);
                nodes.add(lineHitArea);
                break;
            case CIRCLE:
                if (ann.getGeoPoints().size() < 2) return;
                Point2D center = projection.project(ann.getGeoPoints().get(0));
                Point2D border = projection.project(ann.getGeoPoints().get(1));
                double radius = Math.sqrt(Math.pow(border.getX()-center.getX(),2) + Math.pow(border.getY()-center.getY(),2));
                Circle c = new Circle(center.getX(), center.getY(), radius);
                c.setStroke(color);
                c.setStrokeWidth(width);
                c.setPickOnBounds(false);
                c.setFill(null);
                mapPane.getChildren().add(c);
                nodes.add(c);

                // Área de clic más ancha
                Circle cHitArea = new Circle(center.getX(), center.getY(), radius);
                cHitArea.setStroke(Color.TRANSPARENT);
                cHitArea.setStrokeWidth(10);
                cHitArea.setPickOnBounds(false);
                cHitArea.setFill(null);
                mapPane.getChildren().add(cHitArea);
                nodes.add(cHitArea);
                break;
        }
        
        for (javafx.scene.Node node : nodes) {
            node.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    annotationList.getSelectionModel().select(ann);
                    e.consume();
                }
            });
        }

        annotationNodes.put(ann.hashCode(), nodes);
       
        System.out.println("put hashCode: " + ann.hashCode() + " ann: " + ann);
        annotationNodes.put(ann.hashCode(), nodes);
    }

    private void buildElevationProfile() {
<<<<<<< HEAD
        xAxis = (NumberAxis) elevationChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) elevationChart.getYAxis();
        elevationChart.getData().clear();

=======
        elevationChart.getData().clear();
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double distance = 0;
        TrackPoint prev = null;
        for (TrackPoint tp : currentActivity.getTrackPoints()) {
            if (prev != null) distance += prev.distanceTo(tp);
            prev = tp;
            series.getData().add(new XYChart.Data<>(distance / 1000.0, tp.getElevation()));
        }
        elevationChart.getData().add(series);
<<<<<<< HEAD

        elevationChart.setOnMouseMoved(e -> {
            if (highlightCircle != null) {
                mapPane.getChildren().remove(highlightCircle);
                highlightCircle = null;
            }
            if (chartHighlightCircle != null) {
                chartHighlightCircle.setVisible(false);
            }

            double axisX = xAxis.sceneToLocal(e.getSceneX(), e.getSceneY()).getX();
            double mouseKm = xAxis.getValueForDisplay(axisX).doubleValue();

            double minDiff = Double.MAX_VALUE;
            TrackPoint closest = null;
            double closestKm = 0;
            TrackPoint prevPoint = null;
            double dist = 0;
            for (TrackPoint tp : currentActivity.getTrackPoints()) {
                if (prevPoint != null) dist += prevPoint.distanceTo(tp);
                prevPoint = tp;
                double d = Math.abs(mouseKm - dist / 1000.0);
                if (d < minDiff) {
                    minDiff = d;
                    closest = tp;
                    closestKm = dist / 1000.0;
                }
            }

            if (closest != null) {
                // Punto en el mapa
                Point2D pt = projection.project(closest);
                highlightCircle = new Circle(pt.getX(), pt.getY(), 5, Color.YELLOW);
                highlightCircle.setStroke(Color.BLACK);
                mapPane.getChildren().add(highlightCircle);

                // Coordenadas en escena del punto del gráfico
                final double screenX = xAxis.localToScene(xAxis.getDisplayPosition(closestKm), 0).getX();
                final double screenY = yAxis.localToScene(0, yAxis.getDisplayPosition(closest.getElevation())).getY();

                if (chartHighlightCircle == null) {
                    chartHighlightCircle = new Circle(6, Color.YELLOW);
                    chartHighlightCircle.setStroke(Color.BLACK);
                    chartHighlightCircle.setStrokeWidth(1.5);
                    chartHighlightCircle.setMouseTransparent(true);
                }

                javafx.application.Platform.runLater(() -> {
                    javafx.scene.layout.Pane chartPane = (javafx.scene.layout.Pane)
                        elevationChart.lookup(".chart-plot-background").getParent();

                    if (!chartPane.getChildren().contains(chartHighlightCircle)) {
                        chartPane.getChildren().add(chartHighlightCircle);
                    }

                    double localX = chartPane.sceneToLocal(screenX, screenY).getX();
                    double localY = chartPane.sceneToLocal(screenX, screenY).getY();

                    chartHighlightCircle.setCenterX(localX);
                    chartHighlightCircle.setCenterY(localY);
                    chartHighlightCircle.setVisible(true);
                });
            }
        });

        elevationChart.setOnMouseExited(e -> {
            if (highlightCircle != null) {
                mapPane.getChildren().remove(highlightCircle);
                highlightCircle = null;
            }
            if (chartHighlightCircle != null) {
                chartHighlightCircle.setVisible(false);
            }
        });
    }
   
    private ContextMenu activeContextMenu;

    private void setupMapClickHandler() {
        if (previewNode != null) {
            mapPane.getChildren().remove(previewNode);
            previewNode = null;
        }

        // Hover sobre la ruta en el mapa
        mapPane.setOnMouseMoved(e -> {
            List<TrackPoint> points = currentActivity.getTrackPoints();
            if (points.isEmpty() || projection == null) return;

            double mouseX = e.getX();
            double mouseY = e.getY();

            // Buscar el TrackPoint más cercano en píxeles
            double minDist = Double.MAX_VALUE;
            TrackPoint closest = null;
            double closestKm = 0;
            TrackPoint prevPoint = null;
            double dist = 0;

            for (TrackPoint tp : points) {
                if (prevPoint != null) dist += prevPoint.distanceTo(tp);
                prevPoint = tp;

                Point2D pt = projection.project(tp);
                double d = Math.sqrt(
                    Math.pow(pt.getX() - mouseX, 2) +
                    Math.pow(pt.getY() - mouseY, 2)
                );
                if (d < minDist) {
                    minDist = d;
                    closest = tp;
                    closestKm = dist / 1000.0;
                }
            }

            // Solo actuar si el ratón está cerca de la ruta (umbral en píxeles)
            final double THRESHOLD = 60.0;
            if (minDist > THRESHOLD) {
                if (highlightCircle != null) {
                    mapPane.getChildren().remove(highlightCircle);
                    highlightCircle = null;
                }
                if (chartHighlightCircle != null) {
                    chartHighlightCircle.setVisible(false);
                }
                return;
            }

            // Punto amarillo en el mapa
            if (highlightCircle != null) mapPane.getChildren().remove(highlightCircle);
            Point2D pt = projection.project(closest);
            highlightCircle = new Circle(pt.getX(), pt.getY(), 5, Color.YELLOW);
            highlightCircle.setStroke(Color.BLACK);
            mapPane.getChildren().add(highlightCircle);

            // Punto amarillo en el gráfico
            if (chartHighlightCircle == null) {
                chartHighlightCircle = new Circle(6, Color.YELLOW);
                chartHighlightCircle.setStroke(Color.BLACK);
                chartHighlightCircle.setStrokeWidth(1.5);
                chartHighlightCircle.setMouseTransparent(true);
            }

            final TrackPoint finalClosest = closest;
            final double finalKm = closestKm;
            final double screenX = xAxis.localToScene(xAxis.getDisplayPosition(finalKm), 0).getX();
            final double screenY = ((NumberAxis) elevationChart.getYAxis())
                .localToScene(0, ((NumberAxis) elevationChart.getYAxis())
                .getDisplayPosition(finalClosest.getElevation())).getY();

            javafx.application.Platform.runLater(() -> {
                javafx.scene.layout.Pane chartPane = (javafx.scene.layout.Pane)
                    elevationChart.lookup(".chart-plot-background").getParent();

                if (!chartPane.getChildren().contains(chartHighlightCircle)) {
                    chartPane.getChildren().add(chartHighlightCircle);
                }

                double localX = chartPane.sceneToLocal(screenX, screenY).getX();
                double localY = chartPane.sceneToLocal(screenX, screenY).getY();

                chartHighlightCircle.setCenterX(localX);
                chartHighlightCircle.setCenterY(localY);
                chartHighlightCircle.setVisible(true);
            });
        });
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
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
=======
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
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
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
<<<<<<< HEAD
=======
        System.out.println("Abriendo diálogo para anotación tipo " + type);
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
        Dialog<Annotation> dialog = new Dialog<>();
        dialog.setTitle("Nueva anotación");
        dialog.setHeaderText("Introduce los detalles");
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        TextField textField = new TextField();
        textField.setPromptText("Texto (opcional)");
<<<<<<< HEAD

        // Reemplaza el TextField de color por un ColorPicker
        ColorPicker colorPicker = new ColorPicker(Color.RED);

        VBox vbox = new VBox(10,
            new Label("Texto:"), textField,
            new Label("Color:"), colorPicker
        );
=======
        TextField colorField = new TextField("#FF0000");
        colorField.setPromptText("Color CSS (ej. #FF0000)");
        VBox vbox = new VBox(10, new Label("Texto:"), textField, new Label("Color:"), colorField);
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
        dialog.getDialogPane().setContent(vbox);
        dialog.setResultConverter(dialogButton -> {
<<<<<<< HEAD
            if (dialogButton == okButton) {
                // Convierte el Color de JavaFX a hex CSS (#RRGGBB)
                Color c = colorPicker.getValue();
                String hex = String.format("#%02X%02X%02X",
                    (int) (c.getRed()   * 255),
                    (int) (c.getGreen() * 255),
                    (int) (c.getBlue()  * 255));
                return new Annotation(
                    type,
                    textField.getText(),
                    hex,
                    2.0,
                    List.of(points)
                );
            }
=======
            if (dialogButton == okButton)
                return new Annotation(type, textField.getText(), colorField.getText(), 2.0, List.of(points));
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
            return null;
        });
        Optional<Annotation> result = dialog.showAndWait();
        result.ifPresent(ann -> {
            Annotation saved = app.addAnnotation(currentActivity, ann);
            if (saved != null) {
<<<<<<< HEAD
                drawAnnotation(saved);
                annotationItems.add(saved);
                annotationList.getSelectionModel().select(saved);
                javafx.application.Platform.runLater(() -> blinkAnnotation(saved));
=======
                // Añadir a nuestra lista y redibujar
                annotationItems.add(saved);
                drawAnnotations();
                updateAnnotationList();
                System.out.println("Anotación guardada. Total en lista: " + annotationItems.size());
            } else {
                System.out.println("Error: la anotación no se pudo guardar.");
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
            }
        });
    }

<<<<<<< HEAD
    private void startLineAnnotation(GeoPoint start) {
        Point2D p1 = projection.project(start);

        // Crear línea de preview
        Line preview = new Line(p1.getX(), p1.getY(), p1.getX(), p1.getY());
        preview.setStroke(Color.GRAY);
        preview.setStrokeWidth(2);
        preview.getStrokeDashArray().addAll(6.0, 4.0); // línea discontinua
        mapPane.getChildren().add(preview);
        previewNode = preview;

        mapPane.setOnMouseMoved(e -> {
            preview.setEndX(e.getX());
            preview.setEndY(e.getY());
        });

        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Limpiar preview y listener de movimiento
                mapPane.getChildren().remove(previewNode);
                previewNode = null;
                mapPane.setOnMouseMoved(null);

                GeoPoint end = projection.unproject(e.getX(), e.getY());
                addAnnotation(AnnotationType.LINE, start, end);
                setupMapClickHandler();
            }
        });
    }

    private void startCircleAnnotation(GeoPoint center) {
        Point2D pc = projection.project(center);

        // Crear círculo de preview
        Circle preview = new Circle(pc.getX(), pc.getY(), 1);
        preview.setStroke(Color.GRAY);
        preview.setStrokeWidth(2);
        preview.getStrokeDashArray().addAll(6.0, 4.0);
        preview.setFill(Color.TRANSPARENT);
        mapPane.getChildren().add(preview);
        previewNode = preview;

        mapPane.setOnMouseMoved(e -> {
            double radius = Math.sqrt(
                Math.pow(e.getX() - pc.getX(), 2) +
                Math.pow(e.getY() - pc.getY(), 2));
            preview.setRadius(radius);
        });

        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                mapPane.getChildren().remove(previewNode);
                previewNode = null;
                mapPane.setOnMouseMoved(null);

                GeoPoint border = projection.unproject(e.getX(), e.getY());
                addAnnotation(AnnotationType.CIRCLE, center, border);
                setupMapClickHandler();
            }
        });
    }
    
    // Métodos de zoom
    @FXML
    private void zoomIn() {
        double val = zoom_slider.getValue() + 0.1;
        if (val > zoom_slider.getMax()) val = zoom_slider.getMax();
        zoom_slider.setValue(val);
=======
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
>>>>>>> 05fc9111e36e4bd52023daa9354af4d264e61871
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

    @FXML
    private void borrar(ActionEvent event) {
        Annotation selected = annotationList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        annotationList.getSelectionModel().selectedItemProperty().removeListener(selectionListener);

        List<javafx.scene.Node> nodes = annotationNodes.remove(selected.hashCode());
        if (nodes != null) {
            mapPane.getChildren().removeAll(nodes);
        }

        app.removeAnnotation(selected);
        annotationItems.remove(selected);
        annotationList.getSelectionModel().clearSelection();

        annotationList.getSelectionModel().selectedItemProperty().addListener(selectionListener);
        borrarButton.setDisable(true);
        editarButton.setDisable(true);
    }
    
    private void focusAnnotationOnMap(Annotation ann) {
        if (ann.getGeoPoints().isEmpty()) return;
        Point2D pt = projection.project(ann.getGeoPoints().get(0));

        double contentWidth  = zoomGroup.getBoundsInParent().getWidth();
        double contentHeight = zoomGroup.getBoundsInParent().getHeight();
        double viewportWidth  = map_scrollpane.getViewportBounds().getWidth();
        double viewportHeight = map_scrollpane.getViewportBounds().getHeight();

        double scale = zoomGroup.getScaleX();
        double scaledX = pt.getX() * scale;
        double scaledY = pt.getY() * scale;

        double targetH = (scaledX - viewportWidth  / 2) / (contentWidth  - viewportWidth);
        double targetV = (scaledY - viewportHeight / 2) / (contentHeight - viewportHeight);

        targetH = Math.max(0, Math.min(1, targetH));
        targetV = Math.max(0, Math.min(1, targetV));

        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        timeline.getKeyFrames().add(new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(400),
            new javafx.animation.KeyValue(map_scrollpane.hvalueProperty(), targetH,
                javafx.animation.Interpolator.EASE_BOTH),
            new javafx.animation.KeyValue(map_scrollpane.vvalueProperty(), targetV,
                javafx.animation.Interpolator.EASE_BOTH)
        ));
        // Parpadeo siempre al terminar el scroll, haya habido movimiento o no
        timeline.setOnFinished(e -> javafx.application.Platform.runLater(() -> blinkAnnotation(ann)));
        timeline.play();
    }

    private void blinkAnnotation(Annotation ann) {
        System.out.println("blink hashCode: " + ann.hashCode());
        System.out.println("claves en annotationNodes: " + annotationNodes.keySet());
        List<javafx.scene.Node> nodes = annotationNodes.get(ann.hashCode());
        if (nodes != null) {
            nodes.forEach(n -> {
                javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(200), n);
                ft.setFromValue(1.0);
                ft.setToValue(0.2);
                ft.setCycleCount(4);
                ft.setAutoReverse(true);
                ft.play();
            });
        }
    }

    @FXML
    private void editar(ActionEvent event) {
        System.out.println("editar llamado");
        Annotation selected = annotationList.getSelectionModel().getSelectedItem();
        System.out.println("selected: " + selected);
        if (selected == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar anotación");
        dialog.setHeaderText("Modifica los detalles");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField textField = new TextField(selected.getText());
        ColorPicker colorPicker = new ColorPicker(Color.web(selected.getColor()));

        VBox vbox = new VBox(10,
            new Label("Texto:"), textField,
            new Label("Color:"), colorPicker
        );
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                // Eliminar nodos visuales y anotación antigua
                List<javafx.scene.Node> oldNodes = annotationNodes.remove(selected.hashCode());
                if (oldNodes != null) mapPane.getChildren().removeAll(oldNodes);
                app.removeAnnotation(selected);
                annotationItems.remove(selected);

                // Crear nueva anotación con los datos editados
                Color c = colorPicker.getValue();
                String hex = String.format("#%02X%02X%02X",
                    (int)(c.getRed()   * 255),
                    (int)(c.getGreen() * 255),
                    (int)(c.getBlue()  * 255));

                Annotation newAnn = new Annotation(
                    selected.getType(),
                    textField.getText(),
                    hex,
                    selected.getStrokeWidth(),
                    selected.getGeoPoints()
                );

                Annotation saved = app.addAnnotation(currentActivity, newAnn);
                if (saved != null) {
                    drawAnnotation(saved);
                    annotationItems.add(saved);
                    annotationList.getSelectionModel().select(saved);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}