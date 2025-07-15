package org.fernandomaldonado.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Compras {
    private final IntegerProperty idCompra;
    private final IntegerProperty idUsuario;
    private final ObjectProperty<LocalDate> fechaCompra;
    private final DoubleProperty total;

    // Constructor vacío
    public Compras() {
        this.idCompra = new SimpleIntegerProperty(0);
        this.idUsuario = new SimpleIntegerProperty(0);
        this.fechaCompra = new SimpleObjectProperty<>(null);
        this.total = new SimpleDoubleProperty(0.0);
    }

    // Constructor con parámetros
    public Compras(int idCompra, int idUsuario, LocalDate fechaCompra, double total) {
        this.idCompra = new SimpleIntegerProperty(idCompra);
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.fechaCompra = new SimpleObjectProperty<>(fechaCompra);
        this.total = new SimpleDoubleProperty(total);
    }

    // Getters
    public int getIdCompra() {
        return idCompra.get();
    }

    public int getIdUsuario() {
        return idUsuario.get();
    }

    public LocalDate getFechaCompra() {
        return fechaCompra.get();
    }

    public double getTotal() {
        return total.get();
    }

    // Property getters (para TableView y bindings)
    public IntegerProperty idCompraProperty() {
        return idCompra;
    }

    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public ObjectProperty<LocalDate> fechaCompraProperty() {
        return fechaCompra;
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    // Setters
    public void setIdCompra(int idCompra) {
        this.idCompra.set(idCompra);
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario.set(idUsuario);
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra.set(fechaCompra);
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    @Override
        public String toString() {
            return "ID: " + idCompra + "\n" + // Add newline for better readability
                   "USUARIO: " + idUsuario + "\n" +
                   "FECHA COMPRAS: " + fechaCompra + "\n" +
                   "TOTAL: " + total; // Removed the trailing '}'
        }
}
