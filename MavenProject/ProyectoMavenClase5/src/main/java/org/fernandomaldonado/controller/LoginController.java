package org.fernandomaldonado.controller;

import org.fernandomaldonado.conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.fernandomaldonado.system.Main;

public class LoginController {

    private Main principal;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox cbMostrarPassword;

    @FXML private Button btnRegresar;
    @FXML private Label lblMensaje;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    public void initialize() {
        // Sincronizar el texto entre txtPassword y txtPasswordVisible
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        // Checkbox para mostrar/ocultar contraseña
        cbMostrarPassword.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
                txtPassword.setVisible(false);
                txtPassword.setManaged(false);
            } else {
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
                txtPassword.setVisible(true);
                txtPassword.setManaged(true);
            }
        });
    }

    @FXML
    private void loginAction() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Ingrese usuario y contraseña");
            return;
        }

        try {
            Connection conn = Conexion.getInstancia().getConexion();
            String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblMensaje.setText("¡Login exitoso!");
                principal.Inicio();
                // Aquí abres la ventana principal o haces otra acción
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error en la conexión");
        }
    }

    @FXML
    private void Regresar(ActionEvent evento){
        if (evento.getSource() == btnRegresar){
            principal.PantallaInicio();
        }    
    }
}
