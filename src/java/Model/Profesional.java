/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Frocmen
 */
public class Profesional extends Persona{
    private String especialidad;
    private String colegiatura;   // Número de registro profesional

    public Profesional() {}

    public Profesional(int id_persona, String nombre, String apellido, String dni, 
                      String telefono, String email, String direccion, 
                      String especialidad, String colegiatura) {
        
        super(id_persona, nombre, apellido, dni, telefono, email, direccion);
        this.especialidad = especialidad;
        this.colegiatura = colegiatura;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public String getColegiatura() { return colegiatura; }
    public void setColegiatura(String colegiatura) { this.colegiatura = colegiatura; }
}