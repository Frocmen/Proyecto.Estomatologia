/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDateTime;

/**
 *
 * @author Frocmen
 */
public class Paciente{
    
    private int id;                    // ← Era id_persona
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String passwordHash;       // ← NUEVO (era clave en Usuario)
    private String googleId;           // ← NUEVO (para OAuth)
    private String telefono;
    private boolean verificado;        // ← NUEVO
    private String tokenVerificacion;  // ← NUEVO
    private LocalDateTime fechaRegistro; // ← NUEVO
    private boolean activo; 

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, String dni, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.activo = true;
        this.verificado = false;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { 
        // RN-D06: Validar 9 dígitos
        if (telefono != null && !telefono.matches("^[0-9]{9}$")) {
            throw new IllegalArgumentException("Teléfono debe tener 9 dígitos numéricos");
        }
        this.telefono = telefono; 
    }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }

    public String getTokenVerificacion() { return tokenVerificacion; }
    public void setTokenVerificacion(String tokenVerificacion) { this.tokenVerificacion = tokenVerificacion; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return String.format("Paciente[id=%d, %s, DNI=%s]", id, getNombreCompleto(), dni);
    }
    
    
}
