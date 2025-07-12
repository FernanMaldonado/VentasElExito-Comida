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
 * @author informatica
 */
public class InicioController implements Initializable {
    private Main principal;

@FXML private Button btnIniciar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    @FXML
    public void clickInicio(ActionEvent evento) {
        if (evento.getSource() == btnIniciar) {
            System.out.println("Iniciando...");
            principal.Login();
        }
    }
    
}
