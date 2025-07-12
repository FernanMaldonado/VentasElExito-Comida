package org.fernandomaldonado.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fernandomaldonado.controller.InicioController;
import org.fernandomaldonado.controller.RegistrosProductosController;

import java.io.IOException;
import org.fernandomaldonado.controller.LoginController;

public class Main extends Application {
    // CAMBIO AQUI: La ruta debe ser solo "/view/" porque tus FXML están directamente en src/main/resources/view/
    private static final String URL_VIEW = "/view/";
    private Stage escenarioPrincipal;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.escenarioPrincipal = stage;
        this.escenarioPrincipal.setTitle("Ventas El Éxito - Comida");
        Inicio();
        stage.show();
    }

    public <T> T cambiarEntreEscenas(String fxml, double ancho, double alto) {
        FXMLLoader cargadorFXML = null;
        try {
            String fullPath = URL_VIEW + fxml;
            if (getClass().getResource(fullPath) == null) {
                System.err.println("Error: El archivo FXML no se encontró en la ruta: " + fullPath);
                return null;
            }

            cargadorFXML = new FXMLLoader(getClass().getResource(fullPath));
            Parent archivoFXML = cargadorFXML.load();
            Scene escena = new Scene(archivoFXML, ancho, alto);
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.sizeToScene();

            return cargadorFXML.getController();

        } catch (IOException e) {
            System.err.println("Error de I/O al cargar la vista FXML '" + fxml + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado al cambiar de escena a '" + fxml + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void Inicio() {
        InicioController ic = cambiarEntreEscenas("InicioView.fxml", 800, 600);

        if (ic != null) {
            ic.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Inicio o su controlador es nulo.");
        }
    }

    public void RegistrosProductos() {
        RegistrosProductosController rp = cambiarEntreEscenas("RegistrosProductosView.fxml", 838, 700);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void Login() {
        LoginController rp = cambiarEntreEscenas("LoginView.fxml", 600, 400);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
}
