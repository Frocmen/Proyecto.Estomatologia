/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Frocmen
 */
public class Especialidad {
    public static final int DURACION_ENDODONCIA = 60;
    public static final int DURACION_ORTODONCIA = 45;
    public static final int DURACION_LIMPIEZA = 30;
    public static final int DURACION_REVISION = 30;
    
    private int id;
    private String nombre;
    private int duracionMinutos;     
    private boolean activo;

    public Especialidad() {}

    public Especialidad(int id, String nombre, int duracionMinutos, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
    }

    // Factory methods
    public static Especialidad endodoncia() {
        return new Especialidad(1, "Endodoncia", DURACION_ENDODONCIA, true);
    }
    public static Especialidad ortodoncia() {
        return new Especialidad(2, "Ortodoncia", DURACION_ORTODONCIA, true);
    }
    public static Especialidad limpieza() {
        return new Especialidad(3, "Limpieza Dental", DURACION_LIMPIEZA, true);
    }
    public static Especialidad revisionGeneral() {
        return new Especialidad(4, "Revisión General", DURACION_REVISION, true);
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

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

  

    @Override
    public String toString() {
        return String.format("Especialidad[id=%d, %s, %d min]", id, nombre, duracionMinutos);
    }
}
