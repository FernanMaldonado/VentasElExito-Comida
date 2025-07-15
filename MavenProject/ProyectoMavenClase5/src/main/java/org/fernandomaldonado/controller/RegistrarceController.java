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

    @FXML private TableColumn<Registro, String> colTipoDeCuenta; // NUEVA COLUMNA

    @FXML private TextField txtIdRegistro, txtUsername, txtNombreCompleto, txtCorreoElectronico, txtContrasena, txtNumeroTelefono;
    @FXML private DatePicker txtFechaNacimiento;

    @FXML private ComboBox<String> cmbTipoDeCuenta;  // NUEVO COMBOBOX PARA TIPO DE CUENTA

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnCancelar, btnGuardar, btnRegresar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroBusqueda;

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

        cmbFiltroBusqueda.getItems().addAll("Buscar por:", "ID", "Username", "Nombre", "Correo", "Teléfono","Tipo De Cuenta");
        cmbFiltroBusqueda.getSelectionModel().selectFirst();

        // Inicializar ComboBox tipoDeCuenta
        cmbTipoDeCuenta.getItems().addAll("Administrador", "Usuario");
        cmbTipoDeCuenta.getSelectionModel().select("Usuario"); // Valor por defecto
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnas() {
        colIdRegistro.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colCorreoElectronico.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colNumeroTelefono.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTipoDeCuenta.setCellValueFactory(new PropertyValueFactory<>("tipoDeCuenta"));
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
                    rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null,
                    rs.getString("tipoDeCuenta")  // nuevo campo
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
            txtFechaNacimiento.getValue(),
            cmbTipoDeCuenta.getValue() // nuevo campo
        );
    }

    private void cargarTextFields() {
        Registro usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario != null) {
            txtIdRegistro.setText(String.valueOf(usuario.getIdUsuario()));
            txtUsername.setText(usuario.getUsername());
            txtNombreCompleto.setText(usuario.getNombreCompleto());
            txtCorreoElectronico.setText(usuario.getCorreoElectronico());
            txtContrasena.setText(usuario.getPassword());
            txtNumeroTelefono.setText(usuario.getNumeroTelefono());
            txtFechaNacimiento.setValue(usuario.getFechaNacimiento());
            cmbTipoDeCuenta.setValue(usuario.getTipoDeCuenta());  // cargar valor
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
        cmbTipoDeCuenta.getSelectionModel().select("Usuario");  // reset combo
    }

    private void cambiarEstadoCampos(boolean estado) {
        txtUsername.setDisable(estado);
        txtNombreCompleto.setDisable(estado);
        txtCorreoElectronico.setDisable(estado);
        txtContrasena.setDisable(estado);
        txtNumeroTelefono.setDisable(estado);
        txtFechaNacimiento.setDisable(estado);
        cmbTipoDeCuenta.setDisable(estado);  // habilitar/deshabilitar combo
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
            cs.setInt(1, modeloUsuario.getIdUsuario());
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
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregar_usuario(?, ?, ?, ?, ?, ?, ?);");
                cs.setString(1, modeloUsuario.getUsername());
                cs.setString(2, modeloUsuario.getNombreCompleto());
                cs.setString(3, modeloUsuario.getCorreoElectronico());
                cs.setString(4, modeloUsuario.getPassword());
                cs.setString(5, modeloUsuario.getNumeroTelefono());
                cs.setDate(6, java.sql.Date.valueOf(modeloUsuario.getFechaNacimiento()));
                cs.setString(7, modeloUsuario.getTipoDeCuenta());  // nuevo parámetro
            } else {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_actualizar_usuario(?, ?, ?, ?, ?, ?, ?, ?);");
                cs.setInt(1, modeloUsuario.getIdUsuario());
                cs.setString(2, modeloUsuario.getUsername());
                cs.setString(3, modeloUsuario.getNombreCompleto());
                cs.setString(4, modeloUsuario.getCorreoElectronico());
                cs.setString(5, modeloUsuario.getPassword());
                cs.setString(6, modeloUsuario.getNumeroTelefono());
                cs.setDate(7, java.sql.Date.valueOf(modeloUsuario.getFechaNacimiento()));
                cs.setString(8, modeloUsuario.getTipoDeCuenta());  // nuevo parámetro
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
        if (a.getSource() == btnRegresar) {
            principal.Inicio();
        }
    }
    
    @FXML
    private void buscarUsuario() {
        String filtro = cmbFiltroBusqueda.getValue();
        String texto = txtBuscar.getText().trim().toLowerCase();

        if (filtro == null || texto.isEmpty() || filtro.equals("Buscar por:")) {
            tablaUsuarios.setItems(listaUsuarios); // Muestra todo si está vacío
            return;
        }

        ArrayList<Registro> resultados = new ArrayList<>();
        for (Registro user : listaUsuarios) {
            switch (filtro) {
                case "ID":
                    try {
                        int idBuscado = Integer.parseInt(texto);
                        if (user.getIdUsuario() == idBuscado) {
                            resultados.add(user);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido");
                    }
                    break;
                case "Username":
                    if (user.getUsername().toLowerCase().contains(texto)) {
                        resultados.add(user);
                    }
                    break;
                case "Nombre":
                    if (user.getNombreCompleto().toLowerCase().contains(texto)) {
                        resultados.add(user);
                    }
                    break;
                case "Correo":
                    if (user.getCorreoElectronico().toLowerCase().contains(texto)) {
                        resultados.add(user);
                    }
                    break;
                case "Teléfono":
                    if (user.getNumeroTelefono().toLowerCase().contains(texto)) {
                        resultados.add(user);
                    }
                    break;
                case "Tipo de Cuenta": // NUEVO FILTRO PARA tipoDeCuenta
                    if (user.getTipoDeCuenta().toLowerCase().contains(texto)) {
                        resultados.add(user);
                    }
                    break;
            }
        }

        tablaUsuarios.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaUsuarios.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }

}
