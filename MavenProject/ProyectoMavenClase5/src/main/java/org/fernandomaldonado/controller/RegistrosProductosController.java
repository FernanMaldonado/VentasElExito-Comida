/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.fernandomaldonado.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.fernandomaldonado.system.Main;

/**
 * FXML Controller class
 *
 * @author acord
 */
public class RegistrosProductosController implements Initializable {
    private Main principal;

@FXML private Button btnRegresar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    @FXML
    public void clickRP(ActionEvent evento) {
        if (evento.getSource() == btnRegresar) {
            System.out.println("Regresando...");
            principal.Inicio();
        }
    }
    
}
