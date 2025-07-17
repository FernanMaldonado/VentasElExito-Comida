package org.fernandomaldonado.model;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HistorialDeCompras {
    private IntegerProperty idCompra;
    private ObjectProperty<LocalDate> fechaCompra;
    private StringProperty nombreProducto;
    private IntegerProperty cantidad;
    private ObjectProperty<BigDecimal> precioUnitario;
    private ObjectProperty<BigDecimal> subtotal;
    private ObjectProperty<BigDecimal> totalCompra;
    private StringProperty username;
    private StringProperty correoElectronico;


    public HistorialDeCompras(int idCompra, LocalDate fechaCompra, String username, String correoElectronico,
                          String nombreProducto, int cantidad, BigDecimal precioUnitario,
                          BigDecimal subtotal, BigDecimal totalCompra) {
    this.idCompra = new SimpleIntegerProperty(idCompra);
    this.fechaCompra = new SimpleObjectProperty<>(fechaCompra);
    this.username = new SimpleStringProperty(username);
    this.correoElectronico = new SimpleStringProperty(correoElectronico);
    this.nombreProducto = new SimpleStringProperty(nombreProducto);
    this.cantidad = new SimpleIntegerProperty(cantidad);
    this.precioUnitario = new SimpleObjectProperty<>(precioUnitario);
    this.subtotal = new SimpleObjectProperty<>(subtotal);
    this.totalCompra = new SimpleObjectProperty<>(totalCompra);
}


    // Getters y setters para propiedades (solo ejemplos, puedes añadir los demás igual)
    public String getUsername() { return username.get(); }
    public StringProperty usernameProperty() { return username; }
    public void setUsername(String username) { this.username.set(username); }

    public String getCorreoElectronico() { return correoElectronico.get(); }
    public StringProperty correoElectronicoProperty() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico.set(correoElectronico); }

    public int getIdCompra() { return idCompra.get(); }
    public IntegerProperty idCompraProperty() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra.set(idCompra); }

    public LocalDate getFechaCompra() { return fechaCompra.get(); }
    public ObjectProperty<LocalDate> fechaCompraProperty() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra.set(fechaCompra); }

    public String getNombreProducto() { return nombreProducto.get(); }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto.set(nombreProducto); }

    public int getCantidad() { return cantidad.get(); }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }

    public BigDecimal getPrecioUnitario() { return precioUnitario.get(); }
    public ObjectProperty<BigDecimal> precioUnitarioProperty() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario.set(precioUnitario); }

    public BigDecimal getSubtotal() { return subtotal.get(); }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal.set(subtotal); }

    public BigDecimal getTotalCompra() { return totalCompra.get(); }
    public ObjectProperty<BigDecimal> totalCompraProperty() { return totalCompra; }
    public void setTotalCompra(BigDecimal totalCompra) { this.totalCompra.set(totalCompra); }
}
