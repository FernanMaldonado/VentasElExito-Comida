package org.fernandomaldonado.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.RegistrosProductos;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RegistrosProductosController implements Initializable {

    @FXML private TableView<RegistrosProductos> tablaProductos;
    @FXML private TableColumn<RegistrosProductos, Integer> colIdProducto;
    @FXML private TableColumn<RegistrosProductos, String> colNombreProducto;
    @FXML private TableColumn<RegistrosProductos, String> colMarca;
    @FXML private TableColumn<RegistrosProductos, Double> colPrecio;
    @FXML private TableColumn<RegistrosProductos, Integer> colStock;
    @FXML private TableColumn<RegistrosProductos, LocalDate> colFechaCaducidad;

    @FXML private TextField txtIdProducto;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private DatePicker dpFechaCaducidad;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRegresar;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroBusqueda;

    private ObservableList<RegistrosProductos> listaProductos;
    private RegistrosProductos modeloProducto;
    private Main principal;

    private enum Accion { AGREGAR, EDITAR, NINGUNA }
    private Accion tipoAccion = Accion.NINGUNA;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarProductos();
        tablaProductos.setOnMouseClicked(e -> cargarTextFields());

        cmbFiltroBusqueda.getItems().addAll("Buscar por:", "ID", "Nombre", "Marca", "Precio", "Stock");
        cmbFiltroBusqueda.getSelectionModel().selectFirst();

        cambiarEstadoCampos(true);
        habilitarBotones(true);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnas() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colFechaCaducidad.setCellValueFactory(new PropertyValueFactory<>("fechaDeCaducidad"));
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProductos);
        tablaProductos.getSelectionModel().selectFirst();
        cargarTextFields();
    }

    private ArrayList<RegistrosProductos> listarProductos() {
        ArrayList<RegistrosProductos> productos = new ArrayList<>();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listarProductos();");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                productos.add(new RegistrosProductos(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getDate("fechaDeCaducidad") != null ? rs.getDate("fechadeCaducidad").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    private RegistrosProductos obtenerModelo() {
        int id = txtIdProducto.getText().isEmpty() ? 0 : Integer.parseInt(txtIdProducto.getText());
        LocalDate fechaCaducidad = dpFechaCaducidad.getValue();
        return new RegistrosProductos(
                id,
                txtNombreProducto.getText(),
                txtMarca.getText(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtStock.getText()),
                fechaCaducidad
        );
    }

    private void cargarTextFields() {
        RegistrosProductos producto = tablaProductos.getSelectionModel().getSelectedItem();
        if (producto != null) {
            txtIdProducto.setText(String.valueOf(producto.getIdProducto()));
            txtNombreProducto.setText(producto.getNombreProducto());
            txtMarca.setText(producto.getMarca());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            dpFechaCaducidad.setValue(producto.getFechaDeCaducidad());
        }
    }

    private void limpiarCampos() {
        txtIdProducto.clear();
        txtNombreProducto.clear();
        txtMarca.clear();
        txtPrecio.clear();
        txtStock.clear();
        dpFechaCaducidad.setValue(null);
    }

    private void cambiarEstadoCampos(boolean estado) {
        txtNombreProducto.setDisable(estado);
        txtMarca.setDisable(estado);
        txtPrecio.setDisable(estado);
        txtStock.setDisable(estado);
        dpFechaCaducidad.setDisable(estado);
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
        modeloProducto = obtenerModelo();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_eliminarProductos(?);");
            cs.setInt(1, modeloProducto.getIdProducto());
            cs.execute();
            cargarProductos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnGuardarAction() {
        modeloProducto = obtenerModelo();
        try {
            CallableStatement cs;
            if (tipoAccion == Accion.AGREGAR) {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_agregarProductos(?, ?, ?, ?, ?);");
                cs.setString(1, modeloProducto.getNombreProducto());
                cs.setString(2, modeloProducto.getMarca());
                cs.setDouble(3, modeloProducto.getPrecio());
                cs.setInt(4, modeloProducto.getStock());
                if (modeloProducto.getFechaDeCaducidad() != null) {
                    cs.setDate(5, Date.valueOf(modeloProducto.getFechaDeCaducidad()));
                } else {
                    cs.setNull(5, Types.DATE);
                }
            } else {
                cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_editarProductos(?, ?, ?, ?, ?, ?);");
                cs.setInt(1, modeloProducto.getIdProducto());
                cs.setString(2, modeloProducto.getNombreProducto());
                cs.setString(3, modeloProducto.getMarca());
                cs.setDouble(4, modeloProducto.getPrecio());
                cs.setInt(5, modeloProducto.getStock());
                if (modeloProducto.getFechaDeCaducidad() != null) {
                    cs.setDate(6, Date.valueOf(modeloProducto.getFechaDeCaducidad()));
                } else {
                    cs.setNull(6, Types.DATE);
                }
            }
            cs.execute();
            cargarProductos();
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
    private void buscarProducto() {
        String filtro = cmbFiltroBusqueda.getValue();
        String texto = txtBuscar.getText().trim().toLowerCase();

        if (filtro == null || texto.isEmpty() || filtro.equals("Buscar por:")) {
            tablaProductos.setItems(listaProductos);
            return;
        }

        ArrayList<RegistrosProductos> resultados = new ArrayList<>();
        for (RegistrosProductos prod : listaProductos) {
            switch (filtro) {
                case "ID":
                    try {
                        int idBuscado = Integer.parseInt(texto);
                        if (prod.getIdProducto() == idBuscado) resultados.add(prod);
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido");
                    }
                    break;
                case "Nombre":
                    if (prod.getNombreProducto().toLowerCase().contains(texto)) resultados.add(prod);
                    break;
                case "Marca":
                    if (prod.getMarca().toLowerCase().contains(texto)) resultados.add(prod);
                    break;
                case "Precio":
                    try {
                        double precio = Double.parseDouble(texto);
                        if (prod.getPrecio() == precio) resultados.add(prod);
                    } catch (NumberFormatException e) {
                        System.out.println("Precio inválido");
                    }
                    break;
                case "Stock":
                    try {
                        int stock = Integer.parseInt(texto);
                        if (prod.getStock() == stock) resultados.add(prod);
                    } catch (NumberFormatException e) {
                        System.out.println("Stock inválido");
                    }
                    break;
            }
        }

        tablaProductos.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            cargarTextFields();
        }
    }

    @FXML
    private void btnAnteriorAction() {
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaProductos.getSelectionModel().select(indice - 1);
            cargarTextFields();
        }
    }

    @FXML
    private void btnSiguienteAction() {
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice < listaProductos.size() - 1) {
            tablaProductos.getSelectionModel().select(indice + 1);
            cargarTextFields();
        }
    }
}
