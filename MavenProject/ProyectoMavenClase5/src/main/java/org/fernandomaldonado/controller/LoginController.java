package org.fernandomaldonado.controller;
import org.fernandomaldonado.conexion.Conexion;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.Connection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import javafx.event.ActionEvent;
import org.fernandomaldonado.system.Main;

    public class LoginController {
    private Main principal;
    @FXML
    private TextField txtUsuario;
    @FXML
    private Button btnRegresar;
    
    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

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
        if (evento.getSource()== btnRegresar){
            principal.PantallaInicio();
            }    
        }
        
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

}

