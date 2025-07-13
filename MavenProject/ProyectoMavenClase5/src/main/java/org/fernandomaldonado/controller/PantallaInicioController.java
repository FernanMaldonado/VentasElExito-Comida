package org.fernandomaldonado.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.fernandomaldonado.system.Main;

public class PantallaInicioController implements Initializable {

    private Main principal;

    @FXML
    private Button btnIniciarSesion, btnRegistrarse;
    
    @FXML
    private Label lblMarquee;

    @FXML
    private StackPane marqueePane;


    private ScaleTransition scaleUp;
    private ScaleTransition scaleDown;
    private ScaleTransition scaleUp2;
    private ScaleTransition scaleDown2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        IniciarAnimaion(url, rb);
        RegistrarAnimaion(url, rb);
        Platform.runLater(() -> iniciarAnimacionTexto());

    }
    
    public void IniciarAnimaion(URL url, ResourceBundle rb){
        // Configuramos animaciones para btnIniciarSesion
        scaleUp = new ScaleTransition(Duration.millis(200), btnIniciarSesion);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        scaleDown = new ScaleTransition(Duration.millis(200), btnIniciarSesion);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        btnIniciarSesion.setOnMouseEntered(e -> scaleUp.playFromStart());
        btnIniciarSesion.setOnMouseExited(e -> scaleDown.playFromStart());
        
    }
    
    public void RegistrarAnimaion(URL url, ResourceBundle rb){
       // Configuramos animaciones para btnIniciarSesion
        scaleUp2 = new ScaleTransition(Duration.millis(200), btnRegistrarse);
        scaleUp2.setToX(1.2);
        scaleUp2.setToY(1.2);

        scaleDown2 = new ScaleTransition(Duration.millis(200), btnRegistrarse);
        scaleDown2.setToX(1);
        scaleDown2.setToY(1);

        btnRegistrarse.setOnMouseEntered(e -> scaleUp2.playFromStart());
        btnRegistrarse.setOnMouseExited(e -> scaleDown2.playFromStart());
        
    }
    
    private void iniciarAnimacionTexto() {
        Platform.runLater(() -> {
            double textoWidth = lblMarquee.getWidth(); // tamaño real ya renderizado
            double paneWidth = marqueePane.getWidth();

            lblMarquee.setLayoutX(paneWidth);

            TranslateTransition marquee = new TranslateTransition(Duration.seconds(10), lblMarquee);
            marquee.setFromX(paneWidth);
            marquee.setToX(-textoWidth - 50); // le damos margen extra
            marquee.setCycleCount(Animation.INDEFINITE);
            marquee.setInterpolator(Interpolator.LINEAR);
            marquee.play();
        });
    }



    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private void IniciarPrograma(ActionEvent evento) {
        if (evento.getSource() == btnIniciarSesion) {
            principal.Login();
        } else if (evento.getSource() == btnRegistrarse) {
            principal.CrearRegistro();
        }
    }
}
