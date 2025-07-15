package org.fernandomaldonado.model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Registro {

    private final IntegerProperty idUsuario;
    private final StringProperty username;
    private final StringProperty nombreCompleto;
    private final StringProperty correoElectronico;
    private final StringProperty password;
    private final StringProperty numeroTelefono;
    private final ObjectProperty<LocalDate> fechaNacimiento;
    private final StringProperty tipoDeCuenta;  // <-- nuevo campo

    public Registro() {
        this.idUsuario = new SimpleIntegerProperty(0);
        this.username = new SimpleStringProperty("");
        this.nombreCompleto = new SimpleStringProperty("");
        this.correoElectronico = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.numeroTelefono = new SimpleStringProperty("");
        this.fechaNacimiento = new SimpleObjectProperty<>(null);
        this.tipoDeCuenta = new SimpleStringProperty("Usuario");  // valor por defecto
    }

    public Registro(int idUsuario, String username, String nombreCompleto,
                    String correoElectronico, String password,
                    String numeroTelefono, LocalDate fechaNacimiento,
                    String tipoDeCuenta) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.username = new SimpleStringProperty(username);
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.correoElectronico = new SimpleStringProperty(correoElectronico);
        this.password = new SimpleStringProperty(password);
        this.numeroTelefono = new SimpleStringProperty(numeroTelefono);
        this.fechaNacimiento = new SimpleObjectProperty<>(fechaNacimiento);
        this.tipoDeCuenta = new SimpleStringProperty(tipoDeCuenta);
    }

    // Getters
    public int getIdUsuario() { return idUsuario.get(); }
    public String getUsername() { return username.get(); }
    public String getNombreCompleto() { return nombreCompleto.get(); }
    public String getCorreoElectronico() { return correoElectronico.get(); }
    public String getPassword() { return password.get(); }
    public String getNumeroTelefono() { return numeroTelefono.get(); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento.get(); }
    public String getTipoDeCuenta() { return tipoDeCuenta.get(); }  // getter nuevo

    // Setters
    public void setIdUsuario(int idUsuario) { this.idUsuario.set(idUsuario); }
    public void setUsername(String username) { this.username.set(username); }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto.set(nombreCompleto); }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico.set(correoElectronico); }
    public void setPassword(String password) { this.password.set(password); }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono.set(numeroTelefono); }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento.set(fechaNacimiento); }
    public void setTipoDeCuenta(String tipoDeCuenta) { this.tipoDeCuenta.set(tipoDeCuenta); }  // setter nuevo

    // Properties para bindings en JavaFX
    public IntegerProperty idUsuarioProperty() { return idUsuario; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty nombreCompletoProperty() { return nombreCompleto; }
    public StringProperty correoElectronicoProperty() { return correoElectronico; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty numeroTelefonoProperty() { return numeroTelefono; }
    public ObjectProperty<LocalDate> fechaNacimientoProperty() { return fechaNacimiento; }
    public StringProperty tipoDeCuentaProperty() { return tipoDeCuenta; }  // property nuevo

    @Override
    public String toString() {
        // útil para mostrar en ComboBoxes u otros controles
        return getUsername();
    }
}
