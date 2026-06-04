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
public class Historial {
    
    private int id;
    private int idCita;
    private LocalDateTime fechaHoraInicioReal;
    private LocalDateTime fechaHoraFinReal;
    private String piezaDental;
    private String tratamientoRealizado;
    private String observaciones;
    private int idProfesionalAtendio;
    private LocalDateTime fechaRegistro;

    public Historial() {
    }

    public Historial(int idCita, LocalDateTime fechaHoraInicioReal,LocalDateTime fechaHoraFinReal, String piezaDental,
            String tratamientoRealizado, String observaciones,int idProfesionalAtendio) {
        this.idCita = idCita;
        this.fechaHoraInicioReal = fechaHoraInicioReal;
        this.fechaHoraFinReal = fechaHoraFinReal;
        this.piezaDental = piezaDental;
        this.tratamientoRealizado = tratamientoRealizado;
        this.observaciones = observaciones;
        this.idProfesionalAtendio = idProfesionalAtendio;
        this.fechaRegistro = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public LocalDateTime getFechaHoraInicioReal() { return fechaHoraInicioReal; }
    public void setFechaHoraInicioReal(LocalDateTime f) { this.fechaHoraInicioReal = f; }

    public LocalDateTime getFechaHoraFinReal() { return fechaHoraFinReal; }
    public void setFechaHoraFinReal(LocalDateTime f) { this.fechaHoraFinReal = f; }

    public String getPiezaDental() { return piezaDental; }
    public void setPiezaDental(String piezaDental) { this.piezaDental = piezaDental; }

    public String getTratamientoRealizado() { return tratamientoRealizado; }
    public void setTratamientoRealizado(String t) { this.tratamientoRealizado = t; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdProfesionalAtendio() { return idProfesionalAtendio; }
    public void setIdProfesionalAtendio(int id) { this.idProfesionalAtendio = id; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime f) { this.fechaRegistro = f; }
}
