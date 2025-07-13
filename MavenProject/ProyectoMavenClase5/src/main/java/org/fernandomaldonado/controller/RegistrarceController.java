package org.fernandomaldonado.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.Registro;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class RegistrarceController implements Initializable {

    @FXML private TableView<Registro> tablaUsuarios;
    @FXML private TableColumn<?, ?> colIdRegistro, colUsername, colNombreCompleto,
            colCorreoElectronico, colpassword, colNumeroTelefono, colFechaNacimiento;
    @FXML private TextField txtIdRegistro, txtUsername, txtNombreCompleto, txtCorreoElectronico, txtContrasena, txtNumeroTelefono;
    @FXML private DatePicker txtFechaNacimiento;

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnCancelar, btnGuardar, btnRegresar;

    private ObservableList<Registro> listaUsuarios;
    private Registro modeloUsuario;
    private Main principal;

    private enum Accion { AGREGAR, EDITAR, NINGUNA }
    private Accion tipoAccion = Accion.NINGUNA;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarUsuarios();
        tablaUsuarios.setOnMouseClicked(e -> cargarTextFields());
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnas() {
        colIdRegistro.setCellValueFactory(new PropertyValueFactory<>("idRegistro"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colCorreoElectronico.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colNumeroTelefono.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
    }

    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(listarUsuarios());
        tablaUsuarios.setItems(listaUsuarios);
        tablaUsuarios.getSelectionModel().selectFirst();
        cargarTextFields();
    }

    private ArrayList<Registro> listarUsuarios() {
        ArrayList<Registro> usuarios = new ArrayList<>();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listar_usuarios();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                usuarios.add(new Registro(
                    rs.getInt("idUsuario"),
                    rs.getString("username"),
                    rs.getString("nombreCompleto"),
                    rs.getString("correoElectronico"),
                    rs.getString("password"),
                    rs.getString("numeroTelefono"),
                    rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    private Registro obtenerModelo() {
        int id = txtIdRegistro.getText().isEmpty() ? 0 : Integer.parseInt(txtIdRegistro.getText());
        return new Registro(
            id,
            txtUsername.getText(),
            txtNombreCompleto.getText(),
            txtCorreoElectronico.getText(),
            txtContrasena.getText(),
            txtNumeroTelefono.getText(),
            txtFechaNacimiento.getValue()
        );
    }

    private void cargarTextFields() {
        Registro usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario != null) {
            txtIdRegistro.setText(String.valueOf(usuario.getIdRegistro()));
            txtUsername.setText(usuario.getUsername());
            txtNombreCompleto.setText(usuario.getNombreCompleto());
            txtCorreoElectronico.setText(usuario.getCorreoElectronico());
            txtContrasena.setText(usuario.getPassword());
            txtNumeroTelefono.setText(usuario.getNumeroTelefono());
            txtFechaNacimiento.setValue(usuario.getFechaNacimiento());
        }
    }

    private void limpiarCampos() {
        txtIdRegistro.clear();
        txtUsername.clear();
        txtNombreCompleto.clear();
        txtCorreoElectronico.clear();
        txtContrasena.clear();
        txtNumeroTelefono.clear();
        txtFechaNacimiento.setValue(null);
    }

    private void cambiarEstadoCampos(boolean estado) {
        txtUsername.setDisable(estado);
        txtNombreCompleto.setDisable(estado);
        txtCorreoElectronico.setDisable(estado);
        txtContrasena.setDisable(estado);
        txtNumeroTelefono.setDisable(estado);
        txtFechaNacimiento.setDisable(estado);
    }

    private void habilitarBotones(boolean estado) {
        btnNuevo.setDisable(!estado);
        btnEditar.setDisable(!estado);
        btnEliminar.setDisable(!estado);
        btnGuardar.setDisable(estado);
        btnCancelar.setDisable(estado);
    }

    @FXML
    private void btnNuevoAction() {
        limpiarCampos();
        cambiarEstadoCampos(false);
        habilitarBotones(false);
        tipoAccion = Accion.AGREGAR;
    }

    @FXML
    private void btnEditarAction() {
        cambiarEstadoCampos(false);
        habilitarBotones(false);
        tipoAccion = Accion.EDITAR;
    }

    @FXML
    private void btnEliminarAction() {
        modeloUsuario = obtenerModelo();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_eliminar_usuario(?);");
            cs.setInt(1, modeloUsuario.getIdRegistro());
            cs.execute();
            cargarUsuarios();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnGuardarAction() {
        modeloUsuario = obtenerModelo();
        try {
            CallableStatement cs;
            if (tipoAccion == Accion.AGREGAR) {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregar_usuario(?, ?, ?, ?, ?, ?);");
                cs.setString(1, modeloUsuario.getUsername());
                cs.setString(2, modeloUsuario.getNombreCompleto());
                cs.setString(3, modeloUsuario.getCorreoElectronico());
                cs.setString(4, modeloUsuario.getPassword());
                cs.setString(5, modeloUsuario.getNumeroTelefono());
                cs.setDate(6, java.sql.Date.valueOf(modeloUsuario.getFechaNacimiento()));

            } else {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_actualizar_usuario(?, ?, ?, ?, ?, ?, ?);");
                cs.setInt(1, modeloUsuario.getIdRegistro());
                cs.setString(2, modeloUsuario.getNombreCompleto());
                cs.setString(3, modeloUsuario.getCorreoElectronico());
                cs.setString(4, modeloUsuario.getPassword());
                cs.setString(5, modeloUsuario.getNumeroTelefono());
                cs.setDate(6, java.sql.Date.valueOf(modeloUsuario.getFechaNacimiento()));
                cs.setString(7, modeloUsuario.getUsername());
            }
            cs.execute();
            cargarUsuarios();
            cambiarEstadoCampos(true);
            habilitarBotones(true);
            tipoAccion = Accion.NINGUNA;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnCancelarAction() {
        cargarTextFields();
        cambiarEstadoCampos(true);
        habilitarBotones(true);
        tipoAccion = Accion.NINGUNA;
    }

    @FXML
    private void btnRegresarAction(ActionEvent a) {
        if(a.getSource()==btnRegresar){
            principal.Inicio();
        }
    }
}
