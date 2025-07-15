package org.fernandomaldonado.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fernandomaldonado.controller.InicioController;
import org.fernandomaldonado.controller.RegistrosProductosController;

import java.io.IOException;
import javafx.scene.image.Image;
import org.fernandomaldonado.controller.CompraCliente;
import org.fernandomaldonado.controller.ComprasController;
import org.fernandomaldonado.controller.DetalleComprasController;
import org.fernandomaldonado.controller.LoginController;
import org.fernandomaldonado.controller.PantallaInicioController;
import org.fernandomaldonado.controller.RegistrarUsuarioController;
import org.fernandomaldonado.controller.RegistrarceController;

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
        Image icon = new Image(getClass().getResourceAsStream("/img/Logo3.png"));
        this.escenarioPrincipal.getIcons().add(icon);
        PantallaInicio();
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
            escenarioPrincipal.centerOnScreen(); 


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
        RegistrosProductosController rp = cambiarEntreEscenas("RegistrosProductosView.fxml", 1000, 800);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void Login() {
        LoginController rp = cambiarEntreEscenas("LoginView.fxml", 450, 400);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void PantallaInicio() {
        PantallaInicioController rp = cambiarEntreEscenas("PantallaInicioView.fxml", 1000, 600);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void Registro() {
        RegistrarceController rp = cambiarEntreEscenas("RegistrarceView.fxml", 1100, 750);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    
    }
    
    public void CrearRegistro() {
        RegistrarUsuarioController rp = cambiarEntreEscenas("RegistrarUsuarioView.fxml", 500, 500);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void Compras() {
        ComprasController rp = cambiarEntreEscenas("ComprasView.fxml", 1000, 600);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void DetalleCompras() {
        DetalleComprasController rp = cambiarEntreEscenas("DetalleComprasView.fxml", 1000, 750);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo.");
        }
    }
    
    public void ClienteCompra() {
        CompraCliente rp = cambiarEntreEscenas("CompraCliente.fxml", 600, 600);
        if (rp != null) {
            rp.setPrincipal(this);
        } else {
            System.err.println("No se pudo cargar la vista de Registros de Productos o su controlador es nulo .");
        }
    }
}
