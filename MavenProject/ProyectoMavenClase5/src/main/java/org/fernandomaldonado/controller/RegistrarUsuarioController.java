package org.fernandomaldonado.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.Registro;
import org.fernandomaldonado.system.Main;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RegistrarUsuarioController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtCorreoElectronico;

    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtContrasenaVisible;

    @FXML private CheckBox cbMostrarContrasena;

    @FXML private TextField txtNumeroTelefono;
    @FXML private DatePicker txtFechaNacimiento;

    @FXML private ComboBox<String> cbmTipoDeCuenta; // <-- Nuevo ComboBox

    @FXML private Button btnRegistrar;
    @FXML private Button btnRegresar;

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void initialize() {
        // Inicializar opciones del ComboBox tipo de cuenta
        cbmTipoDeCuenta.getItems().addAll("Administrador", "Usuario");
        cbmTipoDeCuenta.getSelectionModel().select("Usuario"); // valor por defecto

        // Sincronizar texto de contraseña
        txtContrasenaVisible.textProperty().bindBidirectional(txtContrasena.textProperty());

        // Mostrar/ocultar contraseña
        cbMostrarContrasena.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                txtContrasenaVisible.setVisible(true);
                txtContrasenaVisible.setManaged(true);
                txtContrasena.setVisible(false);
                txtContrasena.setManaged(false);
            } else {
                txtContrasenaVisible.setVisible(false);
                txtContrasenaVisible.setManaged(false);
                txtContrasena.setVisible(true);
                txtContrasena.setManaged(true);
            }
        });
    }

    private Registro obtenerModelo() {
        return new Registro(
            0,
            txtUsername.getText(),
            txtNombreCompleto.getText(),
            txtCorreoElectronico.getText(),
            txtContrasena.getText(),
            txtNumeroTelefono.getText(),
            txtFechaNacimiento.getValue(),
            cbmTipoDeCuenta.getValue()  // <-- Nuevo campo
        );
    }

    private void limpiarCampos() {
        txtUsername.clear();
        txtNombreCompleto.clear();
        txtCorreoElectronico.clear();
        txtContrasena.clear();
        txtNumeroTelefono.clear();
        txtFechaNacimiento.setValue(null);
        cbmTipoDeCuenta.getSelectionModel().select("Usuario");
    }

   @FXML
        private void btnRegistrarAction() {
            Registro nuevoUsuario = obtenerModelo();
            try {
                CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregar_usuario(?, ?, ?, ?, ?, ?, ?);");
                cs.setString(1, nuevoUsuario.getUsername());
                cs.setString(2, nuevoUsuario.getNombreCompleto());
                cs.setString(3, nuevoUsuario.getCorreoElectronico());
                cs.setString(4, nuevoUsuario.getPassword());
                cs.setString(5, nuevoUsuario.getNumeroTelefono());

                if (nuevoUsuario.getFechaNacimiento() != null) {
                    cs.setDate(6, java.sql.Date.valueOf(nuevoUsuario.getFechaNacimiento()));
                } else {
                    cs.setNull(6, Types.DATE);
                }

                cs.setString(7, nuevoUsuario.getTipoDeCuenta());

                cs.execute();

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Registro exitoso");
                alerta.setHeaderText(null);
                alerta.setContentText("¡Usuario registrado correctamente! Presione 'Aceptar' para iniciar sesión.");
                alerta.showAndWait();
                principal.Login();
                limpiarCampos();

            } catch (SQLException e) {
                if (e.getMessage().contains("El nombre de usuario ya existe")) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Usuario existente");
                    alerta.setHeaderText(null);
                    alerta.setContentText("El nombre de usuario ya está registrado. Por favor, elige otro.");
                    alerta.showAndWait();
                } else {
                    e.printStackTrace();
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error");
                    alerta.setHeaderText("No se pudo registrar");
                    alerta.setContentText("Verifica los datos ingresados.");
                    alerta.showAndWait();
                }
            }
        }


    @FXML
    private void btnRegresarAction() {
        principal.PantallaInicio();
    }
}
