package org.fernandomaldonado.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.Registro;
import org.fernandomaldonado.model.RegistrosProductos;
import org.fernandomaldonado.system.Main;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CompraCliente implements Initializable {

    @FXML private TextField txtIdCompra;

    @FXML private DatePicker dpFechaCompra;

    // Ya no hay ComboBox ni TextField de usuario

    @FXML private ComboBox<RegistrosProductos> cmbProductos;
    @FXML private TextField txtCantidad;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegresar;
    private Registro usuarioLogueado;

    public void setUsuarioLogueado(Registro usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    private ObservableList<RegistrosProductos> listaProductos;
    private Main principal;

    // Usuario logueado (debe ser seteado desde Main o clase que maneja sesión)

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProductos();
        limpiarCampos();

        // Fecha fija a la local y bloqueada
        dpFechaCompra.setValue(LocalDate.now());
        dpFechaCompra.setDisable(true);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }


    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList();
        try (
            CallableStatement cs = Conexion.getInstancia().getConexion().prepareCall("CALL sp_listarProductos();");
            ResultSet rs = cs.executeQuery();
        ) {
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
        cmbProductos.getSelectionModel().clearSelection();
        txtCantidad.clear();
    }

    @FXML
    private void btnGuardarAction() {
        if (usuarioLogueado == null) {
            mostrarAlerta("Error", "No hay usuario logueado.");
            return;
        }

        RegistrosProductos productoSeleccionado = cmbProductos.getSelectionModel().getSelectedItem();
        LocalDate fecha = dpFechaCompra.getValue();

        if (productoSeleccionado == null || txtCantidad.getText().isEmpty()) {
            mostrarAlerta("Campos requeridos", "Debes seleccionar un producto y escribir cantidad.");
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

        int stockDisponible = productoSeleccionado.getStock();

        if (stockDisponible == 0) {
            mostrarAlerta("Producto agotado", "Este producto no está disponible actualmente.");
            return;
        }

        if (cantidad > stockDisponible) {
            mostrarAlerta("Stock insuficiente", "Solo hay disponibles " + stockDisponible + " unidades del producto.");
            return;
        }

        Connection conn = null;
        CallableStatement csCompra = null;
        CallableStatement csDetalle = null;
        ResultSet rsGeneratedKeys = null;
        CallableStatement csTotal = null;
        ResultSet rsTotal = null;

        try {
            conn = Conexion.getInstancia().getConexion();

            // Registrar la compra con total 0
            csCompra = conn.prepareCall("CALL sp_agregar_compra(?, ?, ?);", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            csCompra.setInt(1, usuarioLogueado.getIdUsuario());
            csCompra.setDate(2, Date.valueOf(fecha));
            csCompra.setDouble(3, 0.0);
            csCompra.execute();

            // Obtener idCompra recién insertado
            String queryIdCompra = "SELECT MAX(idCompra) AS id FROM compras WHERE idUsuario = ?";
            try (PreparedStatement psIdCompra = conn.prepareStatement(queryIdCompra)) {
                psIdCompra.setInt(1, usuarioLogueado.getIdUsuario());
                rsGeneratedKeys = psIdCompra.executeQuery();
                if (rsGeneratedKeys.next()) {
                    int idCompra = rsGeneratedKeys.getInt("id");

                    // Insertar detalle de compra
                    csDetalle = conn.prepareCall("CALL sp_agregar_detalle_compra(?, ?, ?);");
                    csDetalle.setInt(1, idCompra);
                    csDetalle.setInt(2, productoSeleccionado.getIdProducto());
                    csDetalle.setInt(3, cantidad);
                    csDetalle.execute();

                    // Obtener total actualizado
                    csTotal = conn.prepareCall("SELECT total FROM compras WHERE idCompra = ?");
                    csTotal.setInt(1, idCompra);
                    rsTotal = csTotal.executeQuery();
                    double total = 0.0;
                    if (rsTotal.next()) {
                        total = rsTotal.getDouble("total");
                    }

                    mostrarAlertaConImagen("Compra registrada",
                            "La compra fue registrada exitosamente.\nTotal: Q" + total,
                            "/img/CompraHecha.gif");

                    limpiarCampos();
                } else {
                    mostrarAlerta("Error", "No se pudo obtener el ID de la compra recién insertada.");
                }
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg.contains("Producto agotado") || msg.contains("Stock insuficiente")) {
                mostrarAlerta("Error de stock", msg);
            } else {
                e.printStackTrace();
                mostrarAlerta("Error", "Ocurrió un error al registrar la compra.");
            }
        } finally {
            try { if (rsGeneratedKeys != null) rsGeneratedKeys.close(); } catch (Exception ignored) {}
            try { if (csCompra != null) csCompra.close(); } catch (Exception ignored) {}
            try { if (csDetalle != null) csDetalle.close(); } catch (Exception ignored) {}
            try { if (rsTotal != null) rsTotal.close(); } catch (Exception ignored) {}
            try { if (csTotal != null) csTotal.close(); } catch (Exception ignored) {}
        }
    }

    @FXML
    private void mostrarAlertaConImagen(String titulo, String mensaje, String rutaImagen) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        Label lblMensaje = new Label(mensaje);

        javafx.scene.image.Image imagen = new javafx.scene.image.Image(getClass().getResourceAsStream(rutaImagen));
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(imagen);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        VBox contenido = new VBox(10);
        contenido.getChildren().addAll(lblMensaje, imageView);

        alerta.getDialogPane().setContent(contenido);
        alerta.showAndWait();
    }

    @FXML
    private void btnCancelarAction() {
        limpiarCampos();
    }

    @FXML
    private void btnRegresarAction(ActionEvent e) {
    if (e.getSource() == btnRegresar && principal != null) {
        // Cerrar ventana actual
        Stage stageActual = (Stage) btnRegresar.getScene().getWindow();

        // Abrir ventana nueva (por ejemplo, Inicio)
        principal.HistorialConUsuario(usuarioLogueado);
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
