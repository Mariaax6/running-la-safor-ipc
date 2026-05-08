package mapademo;

// Código generado con ayuda de IA y revisado para el proyecto IPC.

public class SesionApp {

    public static int actividadesImportadas = 0;
    public static int actividadesVisualizadas = 0;
    public static int anotacionesCreadas = 0;

    public static void registrarImportacion() {
        actividadesImportadas++;
    }

    public static void registrarVisualizacion() {
        actividadesVisualizadas++;
    }

    public static void registrarAnotacion() {
        anotacionesCreadas++;
    }
}