package org.fernandomaldonado.model;

import javafx.beans.property.*;

public class DetalleCompras {
    private final IntegerProperty idDetalle;
    private final IntegerProperty idCompra;
    private final IntegerProperty idProducto;
    private final IntegerProperty cantidad;
    private final DoubleProperty precioUnitario;

    // Constructor vacío
    public DetalleCompras() {
        this.idDetalle = new SimpleIntegerProperty(0);
        this.idCompra = new SimpleIntegerProperty(0);
        this.idProducto = new SimpleIntegerProperty(0);
        this.cantidad = new SimpleIntegerProperty(0);
        this.precioUnitario = new SimpleDoubleProperty(0.0);
    }

    // Constructor con parámetros
    public DetalleCompras(int idDetalle, int idCompra, int idProducto, int cantidad, double precioUnitario) {
        this.idDetalle = new SimpleIntegerProperty(idDetalle);
        this.idCompra = new SimpleIntegerProperty(idCompra);
        this.idProducto = new SimpleIntegerProperty(idProducto);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
    }

    // Getters
    public int getIdDetalle() {
        return idDetalle.get();
    }

    public int getIdCompra() {
        return idCompra.get();
    }

    public int getIdProducto() {
        return idProducto.get();
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public double getPrecioUnitario() {
        return precioUnitario.get();
    }

    // Property getters (para TableView y bindings)
    public IntegerProperty idDetalleProperty() {
        return idDetalle;
    }

    public IntegerProperty idCompraProperty() {
        return idCompra;
    }

    public IntegerProperty idProductoProperty() {
        return idProducto;
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    public DoubleProperty precioUnitarioProperty() {
        return precioUnitario;
    }

    // Setters
    public void setIdDetalle(int idDetalle) {
        this.idDetalle.set(idDetalle);
    }

    public void setIdCompra(int idCompra) {
        this.idCompra.set(idCompra);
    }

    public void setIdProducto(int idProducto) {
        this.idProducto.set(idProducto);
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario.set(precioUnitario);
    }

   @Override
        public String toString() {
            return "ID Detalle: " + idDetalle.get() + "\n" +
                   "ID Compra: " + idCompra.get() + "\n" +
                   "ID Producto: " + idProducto.get() + "\n" +
                   "Cantidad: " + cantidad.get() + "\n" +
                   "Precio Unitario: Q" + precioUnitario.get();
        }

}
