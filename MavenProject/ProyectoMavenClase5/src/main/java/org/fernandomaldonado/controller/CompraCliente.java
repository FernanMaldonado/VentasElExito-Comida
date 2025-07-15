package org.fernandomaldonado.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.Compras;
import org.fernandomaldonado.model.Registro;
import org.fernandomaldonado.model.RegistrosProductos;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class CompraCliente implements Initializable {

    @FXML private TextField txtIdCompra;
    @FXML private DatePicker dpFechaCompra;
    @FXML private ComboBox<Registro> cmbUsuarios;
    @FXML private ComboBox<RegistrosProductos> cmbProductos;
    @FXML private TextField txtCantidad;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegresar;

    private ObservableList<Registro> listaUsuarios;
    private ObservableList<RegistrosProductos> listaProductos;
    private Main principal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUsuarios();
        cargarProductos();
        limpiarCampos();
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
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
                        rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null,
                        rs.getString("tipoDeCuenta")
                );
                listaUsuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbUsuarios.setItems(listaUsuarios);
        cmbUsuarios.setPromptText("Selecciona un usuario");
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listarProductos();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                RegistrosProductos p = new RegistrosProductos(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getDate("fechaDeCaducidad") != null ? rs.getDate("fechaDeCaducidad").toLocalDate() : null
                );
                listaProductos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbProductos.setItems(listaProductos);
        cmbProductos.setPromptText("Selecciona un producto");
    }

    private void limpiarCampos() {
        txtIdCompra.clear();
        dpFechaCompra.setValue(null);
        cmbUsuarios.getSelectionModel().clearSelection();
        cmbProductos.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }

    @FXML
    private void btnGuardarAction() {
        Registro usuarioSeleccionado = cmbUsuarios.getSelectionModel().getSelectedItem();
        RegistrosProductos productoSeleccionado = cmbProductos.getSelectionModel().getSelectedItem();
        LocalDate fecha = dpFechaCompra.getValue();

        if (usuarioSeleccionado == null || fecha == null || productoSeleccionado == null || txtCantidad.getText().isEmpty()) {
            mostrarAlerta("Campos requeridos", "Debes completar todos los campos.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser un número entero positivo.");
            return;
        }

        try {
            Connection conn = Conexion.getInstancia().getConexion();

            // 1. Registrar la compra (usuario + fecha + total 0.0)
            CallableStatement cs = conn.prepareCall("CALL sp_agregar_compra(?, ?, ?);");
            cs.setInt(1, usuarioSeleccionado.getIdUsuario());
            cs.setDate(2, Date.valueOf(fecha));
            cs.setDouble(3, 0.0);
            cs.execute();

            // 2. Obtener el ID de la última compra (opcionalmente podrías retornar desde el SP)
            CallableStatement csId = conn.prepareCall("SELECT MAX(idCompra) AS id FROM compras WHERE idUsuario = ?");
            csId.setInt(1, usuarioSeleccionado.getIdUsuario());
            ResultSet rs = csId.executeQuery();
            int idCompra = 0;
            if (rs.next()) {
                idCompra = rs.getInt("id");
            }

            // 3. Insertar detalle de compra
            CallableStatement csDetalle = conn.prepareCall("CALL sp_agregar_detalle_compra(?, ?, ?);");
            csDetalle.setInt(1, idCompra);
            csDetalle.setInt(2, productoSeleccionado.getIdProducto());
            csDetalle.setInt(3, cantidad);
            csDetalle.execute();

            // 4. Obtener el total actualizado de la compra
            CallableStatement csTotal = conn.prepareCall("SELECT total FROM compras WHERE idCompra = ?");
            csTotal.setInt(1, idCompra);
            ResultSet rsTotal = csTotal.executeQuery();
            double total = 0.0;
            if (rsTotal.next()) {
                total = rsTotal.getDouble("total");
            }

            mostrarAlerta("Compra registrada", "La compra fue registrada exitosamente.\nTotal: Q" + total);
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al registrar la compra.");
        }
    }

    @FXML
    private void btnCancelarAction() {
        limpiarCampos();
    }

    @FXML
    private void btnRegresarAction(ActionEvent e) {
        if (e.getSource() == btnRegresar && principal != null) {
            principal.Inicio();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
