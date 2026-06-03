/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Frocmen
 */
public class Paciente extends Persona{
    private String fechaNacimiento;

    public Paciente() {
    }

    public Paciente(String fechaNacimiento, int id_persona, String nombre,String apellido,String dni, String email, String telefono, String direccion) {
        super(id_persona, nombre,apellido,dni, email, telefono, direccion);
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    
}
