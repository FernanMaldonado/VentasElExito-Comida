package org.fernandomaldonado.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.system.Main;
import org.fernandomaldonado.model.RegistrosProductos; // IMPORTANTE: Importa tu clase de modelo

/**
 * FXML Controller class
 *
 * @author acord
 */
public class RegistrosProductosController implements Initializable {
    private Main principal;

    @FXML private Button btnRegresar;
    // CAMBIO AQUI: La tabla es de tipo RegistrosProductos
    @FXML private TableView<RegistrosProductos> tablaProductos;
    // Las TableColumn deben ser del tipo de la clase de modelo y el tipo de dato de la columna
    @FXML private TableColumn<RegistrosProductos, Integer> colIdProducto;
    @FXML private TableColumn<RegistrosProductos, String> colNombreProducto;
    @FXML private TableColumn<RegistrosProductos, String> colMarca;
    @FXML private TableColumn<RegistrosProductos, Double> colPrecio;
    @FXML private TableColumn<RegistrosProductos, Integer> colStock;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    // CAMBIO AQUI: La lista observable es de tipo RegistrosProductos
    private ObservableList<RegistrosProductos> listaProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTablaProductos();
    }

    public void configurarColumnas() {
        // Los nombres de las propiedades deben coincidir con los métodos property() en la clase RegistrosProductos
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    public void cargarTablaProductos() {
        listaProductos = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) { // Asegúrate de que haya elementos antes de seleccionar
            tablaProductos.getSelectionModel().selectFirst();
        }
    }

    // CAMBIO AQUI: Ahora el ArrayList es de tipo RegistrosProductos
    public ArrayList<RegistrosProductos> listarProductos() {
        ArrayList<RegistrosProductos> rproductos = new ArrayList<>();
        try {
            CallableStatement cs = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_listarProductos();");
            ResultSet resultado = cs.executeQuery();

            while (resultado.next()) {
                // CAMBIO AQUI: Creas una nueva instancia de RegistrosProductos (tu modelo)
                rproductos.add(new RegistrosProductos(
                        resultado.getInt("idProducto"),
                        resultado.getString("nombreProducto"),
                        resultado.getString("marca"),
                        resultado.getDouble("precio"),
                        resultado.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Considera cerrar el CallableStatement y ResultSet aquí.
            // La conexión debería ser gestionada en la clase Conexion.
        }
        return rproductos;
    }

    @FXML
    public void clickRP(ActionEvent evento) {
        if (evento.getSource() == btnRegresar) {
            System.out.println("Regresando...");
            if (principal != null) {
                principal.Inicio();
            } else {
                System.err.println("Error: La referencia a Main es nula en RegistrosProductosController.");
            }
        }
    }
}
