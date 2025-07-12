package org.fernandomaldonado.controller;
import org.fernandomaldonado.conexion.Conexion;
import org.fernandomaldonado.model.RegistrosProductos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import org.fernandomaldonado.system.Main;
import javafx.collections.FXCollections;

/**
 * FXML Controller class
 *
 * @author informatica
 */
    public class RegistrosProductosController implements Initializable {
    @FXML private TableView <RegistrosProductos> tablaProductos;
    @FXML private TableColumn colIdProducto,colNombreProducto,colMarca,colPrecio,colStock;
    @FXML private TextField txtIdProducto,txtNombreProducto,txtMarca,txtPrecio,txtBuscar,txtStock;
    @FXML private Button btnAnterior,btnSiguiente, btnNuevo, btnEditar, btnEliminar, 
            btnCancelar, btnGuardar ,btnRegresar; 
    @FXML
    private ComboBox<String> cmbFiltroBusqueda;

    private Main principal;
    private ObservableList<RegistrosProductos> listaProductos;
    private RegistrosProductos modeloProductos ;
    private enum acciones{Agregar,Eliminar,Editar,Ninguna};
    acciones tipoDeAccion = acciones.Ninguna;
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
 
    public Main getPrincipal() {
        return principal;
    }

    @Override

    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar de primero lo que haya aqui
        configurarColumnas();
        cargarTablaCitas();
        // expresion lambda
        tablaProductos.setOnMouseClicked(eh -> cargarCitasTextField());
        cmbFiltroBusqueda.getItems().addAll(" Buscar Por :","ID", "Nombre", "Marca", "Precio", "Stock");
        cmbFiltroBusqueda.getSelectionModel().selectFirst(); // selecciona el primero por defecto
    }    

    public void configurarColumnas(){
        //Formato de columnas
        colIdProducto.setCellValueFactory(new PropertyValueFactory<RegistrosProductos,Integer>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<RegistrosProductos,String>("nombreProducto"));
        colMarca.setCellValueFactory(new PropertyValueFactory<RegistrosProductos, String>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<RegistrosProductos, Integer>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<RegistrosProductos, Integer>("stock"));

    }

    public void cargarTablaCitas(){
        listaProductos = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProductos);
        tablaProductos.getSelectionModel().selectFirst();
        cargarCitasTextField();
    }
    
    public void cargarCitasTextField (){
        // tabla clientes -> modelo = propidades de TEXTFIELD
        RegistrosProductos citaSeleccionada = tablaProductos.getSelectionModel().getSelectedItem();
        if(citaSeleccionada != null ){
        txtIdProducto.setText(String.valueOf(citaSeleccionada.getIdProducto()));
        txtNombreProducto.setText(citaSeleccionada.getNombreProducto());
        txtMarca.setText(citaSeleccionada.getMarca());
        txtPrecio.setText(String.valueOf(citaSeleccionada.getPrecio()));
        txtStock.setText(String.valueOf(citaSeleccionada.getStock()));
        }
        
    }
    
    public ArrayList<RegistrosProductos> listarProductos(){
        ArrayList<RegistrosProductos> productos = new ArrayList<>();
        try {
            ResultSet resultado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_listarProductos();").executeQuery();
            while (resultado.next()) {
                productos.add ( new RegistrosProductos(
                        resultado.getInt(1),
                        resultado.getString(2),
                        resultado.getString(3),
                        resultado.getDouble(4),
                        resultado.getInt(5)));
            }
        } catch (SQLException ex) {
            System.out.println("Error " + ex.getSQLState());   
            ex.printStackTrace();
        }
        return productos;
    }
    
    public RegistrosProductos obtenerModeloProductos(){
        int codigoProducto ;
        if (txtIdProducto.getText().isEmpty()) {
          codigoProducto = 1 ;  
        }else {
            codigoProducto = Integer.parseInt(txtIdProducto.getText());
        }
        String nombreProducto = txtNombreProducto.getText();
        String marca = txtMarca.getText();
        Double precio = Double.parseDouble(txtPrecio.getText());
        int stock = Integer.parseInt(txtStock.getText());
        RegistrosProductos producto = new RegistrosProductos(codigoProducto, nombreProducto, marca, precio, stock);
        return producto ;
    }
    
    public void AgregarProductos(){
        modeloProductos = obtenerModeloProductos();
        try {
            CallableStatement enunciado = Conexion.getInstancia().
                    getConexion().prepareCall("call sp_AgregarProductos(?,?,?,?);");
            enunciado.setString(1,modeloProductos.getNombreProducto());
            enunciado.setString(2,modeloProductos.getMarca());
            enunciado.setDouble(3,modeloProductos.getPrecio());
            enunciado.setInt(4,modeloProductos.getStock());
            enunciado.execute();
            cargarTablaCitas();
        } catch (SQLException ex) {
            System.out.println("Error al agregar");
            ex.printStackTrace();
        }
    }
    
    public void actualizarCitas(){
        modeloProductos = obtenerModeloProductos ();
        try{
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_editarProductos(?,?,?,?,?);");
            enunciado.setInt(1,modeloProductos.getIdProducto());
            enunciado.setString(2,modeloProductos.getNombreProducto());
            enunciado.setString(3,modeloProductos.getMarca());
            enunciado.setDouble(4,modeloProductos.getPrecio());
            enunciado.setInt(5,modeloProductos.getStock());
            enunciado.execute();
            cargarTablaCitas();    
        }catch(SQLException e){
            System.out.println("Error al editar");
            e.printStackTrace();
        }
    }
    
    public void eliminarMascotas(){
        modeloProductos = obtenerModeloProductos();
        try{
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_eliminarProductos(?);");
            enunciado.setInt(1,modeloProductos.getIdProducto());
            enunciado.execute();
            cargarTablaCitas();
        }catch(SQLException ex){
            System.out.println("Error al eliminar");
            ex.printStackTrace();
        }
    }
    
    public void limpiarTexTField () {
        txtIdProducto.clear();
        txtNombreProducto.clear();
        txtMarca.clear();
        txtPrecio.clear();
        txtStock.clear();
}

    
    public void cambiarEstado (boolean estado){
        txtNombreProducto.setDisable(estado);
        txtMarca.setDisable(estado);
        txtPrecio.setDisable(estado);
        txtStock.setDisable(estado);
        
    }
    
    public void habilitarDeshabilitarNodo (){
        boolean desactivado = txtNombreProducto.isDisable();
        cambiarEstado(!desactivado);
        btnSiguiente.setDisable(desactivado);
        btnAnterior.setDisable(desactivado);
        btnNuevo.setDisable(desactivado);
        btnEditar.setDisable(desactivado);
        btnEliminar.setDisable(desactivado);
        btnGuardar.setDisable(!desactivado);
        btnCancelar.setDisable(!desactivado);
      
    }
    
    // Botones 
    @FXML
    private void btnNuevoAction(){
        limpiarTexTField();
        txtNombreProducto.requestFocus();
        tipoDeAccion = acciones.Agregar;
        habilitarDeshabilitarNodo();
    }
    
    @FXML 
    private void btnEditarAction (){
        tipoDeAccion = acciones.Editar;
            habilitarDeshabilitarNodo();
    }
    
    @FXML 
    private void btnEliminarAction (){
        eliminarMascotas();
        tipoDeAccion = acciones.Eliminar;
    }
    
    @FXML
    private void btnCancelarAction (){
        cargarCitasTextField();
            habilitarDeshabilitarNodo();
    }
    @FXML
    private void btnAnteriorAction (){
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice > 0 ) {
            tablaProductos.getSelectionModel().select(indice - 1);
            cargarCitasTextField();
        }
    }
    @FXML
    private void btnSiguienteAction (){
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice < listaProductos.size()-1 ) {
            tablaProductos.getSelectionModel().select(indice + 1);
            cargarCitasTextField();
        }
    }
    
    @FXML 
    private void btnGuardarAction (){
        if (tipoDeAccion == acciones.Agregar) {
            AgregarProductos();
            tipoDeAccion = acciones.Ninguna;
        }else if (tipoDeAccion == acciones.Editar){
            actualizarCitas();
            tipoDeAccion = acciones.Ninguna;
        }
            habilitarDeshabilitarNodo();
    }
    
    @FXML
        private void BuscarID() {
            ArrayList<RegistrosProductos> resultadoBusqueda = new ArrayList<>();
            String textoBusqueda = txtBuscar.getText().trim();
            String filtro = cmbFiltroBusqueda.getValue();

            if (textoBusqueda.isEmpty() || filtro == null) {
                tablaProductos.setItems(FXCollections.observableArrayList(listaProductos));
                return;
            }

            for (RegistrosProductos producto : listaProductos) {
                switch (filtro) {
                    case "ID":
                        try {
                            int idBuscado = Integer.parseInt(textoBusqueda);
                            if (producto.getIdProducto() == idBuscado) {
                                resultadoBusqueda.add(producto);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("ID inválido");
                        }
                        break;

                    case "Nombre":
                        if (producto.getNombreProducto().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                            resultadoBusqueda.add(producto);
                        }
                        break;

                    case "Marca":
                        if (producto.getMarca().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                            resultadoBusqueda.add(producto);
                        }
                        break;

                    case "Precio":
                        try {
                            double precio = Double.parseDouble(textoBusqueda);
                            if (producto.getPrecio() == precio) {
                                resultadoBusqueda.add(producto);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Precio inválido");
                        }
                        break;

                    case "Stock":
                        try {
                            int stock = Integer.parseInt(textoBusqueda);
                            if (producto.getStock() == stock) {
                                resultadoBusqueda.add(producto);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Stock inválido");
                        }
                        break;
                }
            }

            if (resultadoBusqueda.isEmpty()) {
                tablaProductos.setItems(FXCollections.observableArrayList(listaProductos));
            } else {
                tablaProductos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
                tablaProductos.getSelectionModel().selectFirst();
            }
        }



    
    @FXML
        private void Regresar(ActionEvent evento){
        if (evento.getSource()== btnRegresar){
            principal.Inicio();
            }    
        }
}

 
