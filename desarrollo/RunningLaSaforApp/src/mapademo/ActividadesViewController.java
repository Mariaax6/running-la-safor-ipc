package mapademo;

import java.io.File;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

public class ActividadesViewController {

    @FXML
    private void importarGPX() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo GPX");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx")
        );

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            System.out.println("Archivo seleccionado: " + archivo.getAbsolutePath());
        }
    }
}