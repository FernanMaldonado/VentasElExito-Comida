package org.fernandomaldonado.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

/**
 *
 * @author acord
 */
public class RegistrosProductos {
    private final IntegerProperty idProducto;
    private final StringProperty nombreProducto;
    private final StringProperty marca;
    private final DoubleProperty precio;
    private final IntegerProperty stock;
    private final ObjectProperty<LocalDate> fechaDeCaducidad;

    public RegistrosProductos() {
        this.idProducto = new SimpleIntegerProperty(0);
        this.nombreProducto = new SimpleStringProperty("");
        this.marca = new SimpleStringProperty("");
        this.precio = new SimpleDoubleProperty(0.0);
        this.stock = new SimpleIntegerProperty(0);
        this.fechaDeCaducidad = new SimpleObjectProperty<>(null);
    }

    public RegistrosProductos(int idProducto, String nombreProducto, String marca, double precio, int stock, LocalDate fechaDeCaducidad) {
        this.idProducto = new SimpleIntegerProperty(idProducto);
        this.nombreProducto = new SimpleStringProperty(nombreProducto);
        this.marca = new SimpleStringProperty(marca);
        this.precio = new SimpleDoubleProperty(precio);
        this.stock = new SimpleIntegerProperty(stock);
        this.fechaDeCaducidad = new SimpleObjectProperty<>(fechaDeCaducidad);
    }

    // Getters
    public int getIdProducto() {
        return idProducto.get();
    }

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public String getMarca() {
        return marca.get();
    }

    public double getPrecio() {
        return precio.get();
    }

    public int getStock() {
        return stock.get();
    }

    public LocalDate getFechaDeCaducidad() {
        return fechaDeCaducidad.get();
    }

    // Property getters
    public IntegerProperty idProductoProperty() {
        return idProducto;
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public StringProperty marcaProperty() {
        return marca;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public ObjectProperty<LocalDate> fechaDeCaducidadProperty() {
        return fechaDeCaducidad;
    }

    // Setters
    public void setIdProducto(int idProducto) {
        this.idProducto.set(idProducto);
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto.set(nombreProducto);
    }

    public void setMarca(String marca) {
        this.marca.set(marca);
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public void setFechaDeCaducidad(LocalDate fechaDeCaducidad) {
        this.fechaDeCaducidad.set(fechaDeCaducidad);
    }
}
