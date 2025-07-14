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

    public Registro() {
        this.idUsuario = new SimpleIntegerProperty(0);
        this.username = new SimpleStringProperty("");
        this.nombreCompleto = new SimpleStringProperty("");
        this.correoElectronico = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.numeroTelefono = new SimpleStringProperty("");
        this.fechaNacimiento = new SimpleObjectProperty<>(null);
    }

    public Registro(int idUsuario, String username, String nombreCompleto,
                    String correoElectronico, String password,
                    String numeroTelefono, LocalDate fechaNacimiento) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.username = new SimpleStringProperty(username);
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.correoElectronico = new SimpleStringProperty(correoElectronico);
        this.password = new SimpleStringProperty(password);
        this.numeroTelefono = new SimpleStringProperty(numeroTelefono);
        this.fechaNacimiento = new SimpleObjectProperty<>(fechaNacimiento);
    }

    // Getters
    public int getIdUsuario() { return idUsuario.get(); }
    public String getUsername() { return username.get(); }
    public String getNombreCompleto() { return nombreCompleto.get(); }
    public String getCorreoElectronico() { return correoElectronico.get(); }
    public String getPassword() { return password.get(); }
    public String getNumeroTelefono() { return numeroTelefono.get(); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento.get(); }

    // Setters
    public void setIdUsuario(int idUsuario) { this.idUsuario.set(idUsuario); }
    public void setUsername(String username) { this.username.set(username); }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto.set(nombreCompleto); }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico.set(correoElectronico); }
    public void setPassword(String contrasena) { this.password.set(contrasena); }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono.set(numeroTelefono); }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento.set(fechaNacimiento); }

    // Properties
    public IntegerProperty idRegistroProperty() { return idUsuario; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty nombreCompletoProperty() { return nombreCompleto; }
    public StringProperty correoElectronicoProperty() { return correoElectronico; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty numeroTelefonoProperty() { return numeroTelefono; }
    public ObjectProperty<LocalDate> fechaNacimientoProperty() { return fechaNacimiento; }

    @Override
    public String toString() {
        // Opción recomendada para mostrar en ComboBoxes, etc.
        return getUsername();
    }
}
