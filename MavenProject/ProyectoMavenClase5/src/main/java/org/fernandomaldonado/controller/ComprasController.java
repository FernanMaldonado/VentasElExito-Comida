package org.fernandomaldonado.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.Compras;
import org.fernandomaldonado.model.Registro;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ComprasController implements Initializable {

    @FXML private TableView<Compras> tablaCompras;
    @FXML private TableColumn<Compras, Integer> colIdCompra;
    @FXML private TableColumn<Compras, Integer> colIdUsuario;
    @FXML private TableColumn<Compras, LocalDate> colFechaCompra;
    @FXML private TableColumn<Compras, Double> colTotal;

    @FXML private TextField txtIdCompra;
    @FXML private DatePicker dpFechaCompra;
    @FXML private TextField txtTotal;

    @FXML private ComboBox<Registro> cmbUsuarios;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;

    private ObservableList<Compras> listaCompras;
    private ObservableList<Registro> listaUsuarios;
    private Compras modeloCompra;
    private Main principal;

    private enum Accion { AGREGAR, EDITAR, NINGUNA }
    private Accion tipoAccion = Accion.NINGUNA;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarUsuarios();
        cargarCompras();

        tablaCompras.setOnMouseClicked(e -> cargarTextFields());

        cambiarEstadoCampos(true);
        habilitarBotones(true);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnas() {
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listar_usuarios();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Registro u = new Registro(
                        rs.getInt("idUsuario"),
                        rs.getString("username"),
                        rs.getString("nombreCompleto"),
                        rs.getString("correoElectronico"),
                        rs.getString("password"),
                        rs.getString("numeroTelefono"),
                        rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null
                );
                listaUsuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbUsuarios.setItems(listaUsuarios);
        cmbUsuarios.setPromptText("Selecciona un usuario");
    }

    private void cargarCompras() {
        listaCompras = FXCollections.observableArrayList(listarCompras());
        tablaCompras.setItems(listaCompras);
        if (!listaCompras.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }

    private ArrayList<Compras> listarCompras() {
        ArrayList<Compras> compras = new ArrayList<>();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listar_compras();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                compras.add(new Compras(
                        rs.getInt("idCompra"),
                        rs.getInt("idUsuario"),
                        rs.getDate("fechaCompra") != null ? rs.getDate("fechaCompra").toLocalDate() : null,
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }

    private Compras obtenerModelo() {
        int id = txtIdCompra.getText().isEmpty() ? 0 : Integer.parseInt(txtIdCompra.getText());
        LocalDate fechaCompra = dpFechaCompra.getValue();

        Registro usuarioSeleccionado = cmbUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            System.out.println("Debe seleccionar un usuario.");
            return null;
        }

        return new Compras(
                id,
                usuarioSeleccionado.getIdUsuario(),
                fechaCompra,
                Double.parseDouble(txtTotal.getText())
        );
    }

    private void cargarTextFields() {
        Compras compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra != null) {
            txtIdCompra.setText(String.valueOf(compra.getIdCompra()));
            dpFechaCompra.setValue(compra.getFechaCompra());
            txtTotal.setText(String.valueOf(compra.getTotal()));

            for (Registro u : listaUsuarios) {
                if (u.getIdUsuario()== compra.getIdUsuario()) {
                    cmbUsuarios.getSelectionModel().select(u);
                    break;
                }
            }
        }
    }

    private void limpiarCampos() {
        txtIdCompra.clear();
        dpFechaCompra.setValue(null);
        txtTotal.clear();
        cmbUsuarios.getSelectionModel().clearSelection();
    }

    private void cambiarEstadoCampos(boolean estado) {
        dpFechaCompra.setDisable(estado);
        txtTotal.setDisable(estado);
        cmbUsuarios.setDisable(estado);
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
        modeloCompra = obtenerModelo();
        if (modeloCompra == null) return;
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_eliminar_compra(?);");
            cs.setInt(1, modeloCompra.getIdCompra());
            cs.execute();
            cargarCompras();
            limpiarCampos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnGuardarAction() {
        modeloCompra = obtenerModelo();
        if (modeloCompra == null) return;

        try {
            CallableStatement cs;
            if (tipoAccion == Accion.AGREGAR) {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregar_compra(?, ?, ?);");
                cs.setInt(1, modeloCompra.getIdUsuario());
                if (modeloCompra.getFechaCompra() != null) {
                    cs.setDate(2, Date.valueOf(modeloCompra.getFechaCompra()));
                } else {
                    cs.setNull(2, Types.DATE);
                }
                cs.setDouble(3, modeloCompra.getTotal());
            } else if (tipoAccion == Accion.EDITAR) {
                System.out.println("Edición no implementada en el procedimiento almacenado.");
                return;
            } else {
                return;
            }
            cs.execute();
            cargarCompras();
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
    private void btnRegresarAction(ActionEvent e) {
        if (e.getSource() == btnRegresar) {
            principal.Inicio();
        }
    }

    // Método para búsqueda simple por ID o total (puedes ampliar a más campos)
    @FXML
    private void buscarCompra(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            tablaCompras.setItems(listaCompras);
            return;
        }
        String txtLower = texto.toLowerCase();

        ObservableList<Compras> resultados = FXCollections.observableArrayList();

        for (Compras c : listaCompras) {
            if (String.valueOf(c.getIdCompra()).contains(txtLower)
                    || String.valueOf(c.getTotal()).contains(txtLower)) {
                resultados.add(c);
            }
        }

        tablaCompras.setItems(resultados);
        if (!resultados.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }
}
