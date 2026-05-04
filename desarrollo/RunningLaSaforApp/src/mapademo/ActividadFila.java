package mapademo;

public class ActividadFila {

    private String nombre;
    private String fecha;
    private String distancia;
    private String duracion;

    public ActividadFila(String nombre, String fecha, String distancia, String duracion) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.distancia = distancia;
        this.duracion = duracion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}