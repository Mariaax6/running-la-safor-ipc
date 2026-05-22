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
    
    private java.util.Map<Integer, List<javafx.scene.Node>> annotationNodes = new java.util.HashMap<>();
    private javafx.scene.Node previewNode = null;
    private final javafx.collections.ObservableList<Annotation> annotationItems = 
    javafx.collections.FXCollections.observableArrayList();
    private Circle chartHighlightCircle;
    
    
    @FXML
    private Button borrarButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> zoom(newVal.doubleValue()));

        // Listener de selección registrado una sola vez
        borrarButton.setDisable(true);
        annotationList.setItems(annotationItems);
        annotationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            borrarButton.setDisable(newVal == null);
            if (newVal != null && projection != null) {
                focusAnnotationOnMap(newVal);
            }
        });
        
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
                        if (n == borrarButton) return; // clic en el botón, no deseleccionar
                        n = n.getParent();
                    }
                    if (!annotationList.getBoundsInParent().contains(
                            annotationList.getParent().sceneToLocal(e.getSceneX(), e.getSceneY()))) {
                        annotationList.getSelectionModel().clearSelection();
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
    }

    public void setActivity(Activity act) {
        this.currentActivity = act;
        annotationItems.clear();
        loadMap();
        drawRoute();
        drawAnnotations(); // drawAnnotations ya llena annotationNodes
        buildElevationProfile();
        setupMapClickHandler();
        annotationItems.addAll(currentActivity.getAnnotations()); // poblar con los objetos iniciales

        javafx.application.Platform.runLater(() -> {
            map_scrollpane.setHvalue(0.5);
            map_scrollpane.setVvalue(0.5);
        });
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
        annotationNodes.clear(); // limpiar mapa de nodos antes de redibujar
        for (Annotation ann : currentActivity.getAnnotations()) {
            drawAnnotation(ann);
        }
    }

    private void drawAnnotation(Annotation ann) {
        if (ann.getGeoPoints().isEmpty()) return;
        Color color = Color.web(ann.getColor());
        double width = ann.getStrokeWidth();
        List<javafx.scene.Node> nodes = new ArrayList<>();

        switch (ann.getType()) {
            case POINT:
            case TEXT:
                GeoPoint gp = ann.getGeoPoints().get(0);
                Point2D pt = projection.project(gp);
                if (ann.getType() == AnnotationType.POINT) {
                    Circle circle = new Circle(pt.getX(), pt.getY(), 5, color);
                    circle.setStroke(Color.BLACK);
                    mapPane.getChildren().add(circle);
                    nodes.add(circle);
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
                nodes.add(c);
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
        xAxis = (NumberAxis) elevationChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) elevationChart.getYAxis();
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
            }
        });
    }

    private void addAnnotation(AnnotationType type, GeoPoint... points) {
        Dialog<Annotation> dialog = new Dialog<>();
        dialog.setTitle("Nueva anotación");
        dialog.setHeaderText("Introduce los detalles");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField textField = new TextField();
        textField.setPromptText("Texto (opcional)");

        // Reemplaza el TextField de color por un ColorPicker
        ColorPicker colorPicker = new ColorPicker(Color.RED);

        VBox vbox = new VBox(10,
            new Label("Texto:"), textField,
            new Label("Color:"), colorPicker
        );
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
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
            return null;
        });

        Optional<Annotation> result = dialog.showAndWait();
        result.ifPresent(ann -> {
            Annotation saved = app.addAnnotation(currentActivity, ann);
            if (saved != null) {
                drawAnnotation(saved);
                annotationItems.add(saved);
                annotationList.getSelectionModel().select(saved);
                javafx.application.Platform.runLater(() -> blinkAnnotation(saved));
            }
        });
    }

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
        Annotation selected = annotationList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        List<javafx.scene.Node> nodes = annotationNodes.remove(selected.hashCode());
        if (nodes != null) {
            mapPane.getChildren().removeAll(nodes);
        }

        app.removeAnnotation(selected);
        annotationItems.remove(selected);
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
}