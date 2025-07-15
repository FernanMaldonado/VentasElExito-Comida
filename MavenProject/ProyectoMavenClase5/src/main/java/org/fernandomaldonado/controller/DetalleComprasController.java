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
import org.fernandomaldonado.model.DetalleCompras;
import org.fernandomaldonado.model.RegistrosProductos;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DetalleComprasController implements Initializable {

    @FXML private TableView<DetalleCompras> tablaDetalleCompras;
    @FXML private TableColumn<DetalleCompras, Integer> colIdDetalle;
    @FXML private TableColumn<DetalleCompras, Integer> colIdCompra;
    @FXML private TableColumn<DetalleCompras, Integer> colIdProducto;
    @FXML private TableColumn<DetalleCompras, Integer> colCantidad;
    @FXML private TableColumn<DetalleCompras, Double> colPrecioUnitario;

    @FXML private TextField txtIdDetalle;
    @FXML private ComboBox<Compras> cmbCompras;
    @FXML private ComboBox<RegistrosProductos> cmbProductos;
    @FXML private TextField txtCantidad;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;

    private ObservableList<DetalleCompras> listaDetalleCompras;
    private ObservableList<Compras> listaCompras;
    private ObservableList<RegistrosProductos> listaProductos;
    private DetalleCompras modeloDetalleCompra;
    private Main principal;

    private enum Accion { AGREGAR, EDITAR, NINGUNA }
    private Accion tipoAccion = Accion.NINGUNA;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarCompras();
        cargarProductos();
        cargarDetalleCompras();

        tablaDetalleCompras.setOnMouseClicked(e -> cargarTextFields());

        cambiarEstadoCampos(true);
        habilitarBotones(true);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnas() {
        colIdDetalle.setCellValueFactory(new PropertyValueFactory<>("idDetalle"));
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }

    private void cargarCompras() {
        listaCompras = FXCollections.observableArrayList();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listar_compras();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Compras c = new Compras(
                        rs.getInt("idCompra"),
                        rs.getInt("idUsuario"),
                        rs.getDate("fechaCompra") != null ? rs.getDate("fechaCompra").toLocalDate() : null,
                        rs.getDouble("total")
                );
                listaCompras.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbCompras.setItems(listaCompras);
        cmbCompras.setPromptText("Selecciona una compra");
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

    private void cargarDetalleCompras() {
        listaDetalleCompras = FXCollections.observableArrayList(listarDetalleCompras());
        tablaDetalleCompras.setItems(listaDetalleCompras);
        if (!listaDetalleCompras.isEmpty()) {
            tablaDetalleCompras.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }

    private ArrayList<DetalleCompras> listarDetalleCompras() {
        ArrayList<DetalleCompras> detalles = new ArrayList<>();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listar_detalles();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                detalles.add(new DetalleCompras(
                        rs.getInt("idDetalle"),
                        rs.getInt("idCompra"),
                        rs.getInt("idProducto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precioUnitario")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    private DetalleCompras obtenerModelo() {
        int idDetalle = txtIdDetalle.getText().isEmpty() ? 0 : Integer.parseInt(txtIdDetalle.getText());

        Compras compraSeleccionada = cmbCompras.getSelectionModel().getSelectedItem();
        RegistrosProductos productoSeleccionado = cmbProductos.getSelectionModel().getSelectedItem();

        if (compraSeleccionada == null || productoSeleccionado == null) {
            System.out.println("Debe seleccionar una compra y un producto.");
            return null;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException e) {
            System.out.println("Cantidad no válida.");
            return null;
        }

        return new DetalleCompras(
                idDetalle,
                compraSeleccionada.getIdCompra(),
                productoSeleccionado.getIdProducto(),
                cantidad,
                0.0 // Precio será calculado automáticamente en la base de datos
        );
    }

    private void cargarTextFields() {
        DetalleCompras detalle = tablaDetalleCompras.getSelectionModel().getSelectedItem();
        if (detalle != null) {
            txtIdDetalle.setText(String.valueOf(detalle.getIdDetalle()));

            for (Compras c : listaCompras) {
                if (c.getIdCompra() == detalle.getIdCompra()) {
                    cmbCompras.getSelectionModel().select(c);
                    break;
                }
            }

            for (RegistrosProductos p : listaProductos) {
                if (p.getIdProducto() == detalle.getIdProducto()) {
                    cmbProductos.getSelectionModel().select(p);
                    break;
                }
            }

            txtCantidad.setText(String.valueOf(detalle.getCantidad()));
        }
    }

    private void limpiarCampos() {
        txtIdDetalle.clear();
        cmbCompras.getSelectionModel().clearSelection();
        cmbProductos.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }

    private void cambiarEstadoCampos(boolean estado) {
        cmbCompras.setDisable(estado);
        cmbProductos.setDisable(estado);
        txtCantidad.setDisable(estado);
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
        modeloDetalleCompra = obtenerModelo();
        if (modeloDetalleCompra == null) return;

        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_eliminar_detalle_compra(?);");
            cs.setInt(1, modeloDetalleCompra.getIdDetalle());
            cs.execute();
            cargarDetalleCompras();
            limpiarCampos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnGuardarAction() {
        modeloDetalleCompra = obtenerModelo();
        if (modeloDetalleCompra == null) return;

        try {
            CallableStatement cs;
            if (tipoAccion == Accion.AGREGAR) {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregar_detalle_compra(?, ?, ?);");
                cs.setInt(1, modeloDetalleCompra.getIdCompra());
                cs.setInt(2, modeloDetalleCompra.getIdProducto());
                cs.setInt(3, modeloDetalleCompra.getCantidad());
            } else if (tipoAccion == Accion.EDITAR) {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_actualizar_detalle_compra_total(?, ?, ?, ?);");
                cs.setInt(1, modeloDetalleCompra.getIdDetalle());
                cs.setInt(2, modeloDetalleCompra.getIdProducto());
                cs.setInt(3, modeloDetalleCompra.getCantidad());
                cs.setDouble(4, modeloDetalleCompra.getPrecioUnitario());
            } else {
                return;
            }

            cs.execute();
            cargarDetalleCompras();
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

    @FXML
    private void buscarDetalle(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            tablaDetalleCompras.setItems(listaDetalleCompras);
            return;
        }
        String txtLower = texto.toLowerCase();

        ObservableList<DetalleCompras> resultados = FXCollections.observableArrayList();

        for (DetalleCompras d : listaDetalleCompras) {
            if (String.valueOf(d.getIdDetalle()).contains(txtLower)
                    || String.valueOf(d.getPrecioUnitario()).contains(txtLower)) {
                resultados.add(d);
            }
        }

        tablaDetalleCompras.setItems(resultados);
        if (!resultados.isEmpty()) {
            tablaDetalleCompras.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }
}
