package mapademo;

import javafx.geometry.Point2D;

public class Poi {
    private String code;
    private Point2D position;

    // Constructor con argumentos
    public Poi(String code, double x, double y) {
        this.code = code;
        this.position = new Point2D(x, y);
    }

    // Constructor vacío (opcional)
    public Poi() {
        this.code = "";
        this.position = new Point2D(0, 0);
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }

    @Override
    public String toString() {
        return code + " [x:" + position.getX() + " y:" + position.getY() + "]";
    }
}