/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.List;

/**
 *
 * @author Frocmen
 */
public class Profesional{
   
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String passwordHash;
    private String telefono;
    private boolean activo;
    private List<Especialidad> especialidades;
    private Integer especialidadPrincipalId;

    public Profesional() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public Integer getEspecialidadPrincipalId() {
        return especialidadPrincipalId;
    }

    public void setEspecialidadPrincipalId(Integer especialidadPrincipalId) {
        this.especialidadPrincipalId = especialidadPrincipalId;
    }

    public String getNombreCompleto() { return nombre + " " + apellido; 
    }

    @Override
    public String toString() {
        return String.format("Profesional[id=%d, %s]", id, getNombreCompleto());
    }
    
}