package org.fernandomaldonado.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fernandomaldonado.model.HistorialDeCompras;
import org.fernandomaldonado.system.Main;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.fernandomaldonado.conexion.Conexion;

public class HistorialDeComprasController {
    @FXML private Button btnRegresar;
    @FXML
    private TableView<HistorialDeCompras> tableCompras;

    @FXML private TableColumn<HistorialDeCompras, Integer> colIdCompra;
    @FXML private TableColumn<HistorialDeCompras, LocalDate> colFechaCompra;
    @FXML private TableColumn<HistorialDeCompras, String> colProducto;
    @FXML private TableColumn<HistorialDeCompras, Integer> colCantidad;
    @FXML private TableColumn<HistorialDeCompras, BigDecimal> colPrecioUnitario;
    @FXML private TableColumn<HistorialDeCompras, BigDecimal> colSubtotal;
    @FXML private TableColumn<HistorialDeCompras, BigDecimal> colTotal;
    @FXML private TableColumn<HistorialDeCompras, String> colUsername;
    @FXML private TableColumn<HistorialDeCompras, String> colCorreo;
    @FXML
    private Label lblTotalGeneral;

    private ObservableList<HistorialDeCompras> comprasList = FXCollections.observableArrayList();

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private int usuarioId;

    public void setUsuarioLogueado(int idUsuario) {
        this.usuarioId = idUsuario;
        cargarComprasUsuario(idUsuario);
    }

    private void cargarComprasUsuario(int idUsuario) {
        comprasList.clear();

        String sql = "SELECT c.idCompra, c.fechaCompra, u.username, u.correoElectronico, " +
                     "p.nombreProducto, d.cantidad, d.precioUnitario, " +
                     "(d.cantidad * d.precioUnitario) AS subtotal, c.total " +
                     "FROM compras c " +
                     "JOIN usuarios u ON c.idUsuario = u.idUsuario " +
                     "JOIN detalle_compras d ON c.idCompra = d.idCompra " +
                     "JOIN productos p ON d.idProducto = p.idProducto " +
                     "WHERE c.idUsuario = ? " +
                     "ORDER BY c.fechaCompra DESC";

        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();
            BigDecimal totalGeneral = BigDecimal.ZERO;

            while (rs.next()) {
                int idCompra = rs.getInt("idCompra");
                LocalDate fechaCompra = rs.getDate("fechaCompra").toLocalDate();
                String username = rs.getString("username");
                String correo = rs.getString("correoElectronico");
                String nombreProducto = rs.getString("nombreProducto");
                int cantidad = rs.getInt("cantidad");
                BigDecimal precioUnitario = rs.getBigDecimal("precioUnitario");
                BigDecimal subtotal = rs.getBigDecimal("subtotal");
                BigDecimal total = rs.getBigDecimal("total");

                totalGeneral = totalGeneral.add(subtotal);

                HistorialDeCompras detalle = new HistorialDeCompras(
                        idCompra, fechaCompra, username, correo,
                        nombreProducto, cantidad, precioUnitario, subtotal, total
                );

                comprasList.add(detalle);
            }

            tableCompras.setItems(comprasList);
            lblTotalGeneral.setText("Total General: Q " + totalGeneral);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCompra"));
    }
    
    
    @FXML
    private void Regresar(ActionEvent a) {
        if (a.getSource() == btnRegresar) {
            principal.PantallaInicio();
            
        }
    }
}
