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
public class Profesional extends Persona{
   
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String passwordHash;
    private String telefono;
    private boolean activo;
    private List<Especialidad> especialidades;
    private Integer especialidadPrincipalId;

    public Profesional() {}

    // ← AGREGADO: alias getId_persona() para compatibilidad con DAOs y Controllers
    public int getId_persona() { return id; }
    public void setId_persona(int id) { this.id = id; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String p) { this.passwordHash = p; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Especialidad> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<Especialidad> e) { this.especialidades = e; }

    public Integer getEspecialidadPrincipalId() { return especialidadPrincipalId; }
    public void setEspecialidadPrincipalId(Integer id) { this.especialidadPrincipalId = id; }

    public String getNombreCompleto() { return nombre + " " + apellido; }

    @Override
    public String toString() {
        return String.format("Profesional[id=%d, %s]", id, getNombreCompleto());
    }
}