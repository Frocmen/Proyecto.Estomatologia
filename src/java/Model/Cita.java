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
public class Cita {
   private int id_cita;
    private Paciente paciente;
    private Profesional profesional;
    private LocalDateTime fechaHora;
    private String estado;           // CONFIRMADA, ATENDIDA, CANCELADA, REPROGRAMADA, NO_SHOW
    private String motivo;
    
    public Cita() {
    }

    public Cita(int id_cita, Paciente paciente, Profesional profesional, LocalDateTime fechaHora, String estado, String motivo) {
        this.id_cita = id_cita;
        this.paciente = paciente;
        this.profesional = profesional;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.motivo = motivo;
    }

    public int getId_cita() {
        return id_cita;
    }

    public void setId_cita(int id_cita) {
        this.id_cita = id_cita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    // Regla de Negocio RN-05: Solo se puede cancelar con más de 8 horas
    public boolean puedeCancelar() {
        if (fechaHora == null) return false;
        long horasRestantes = java.time.Duration.between(LocalDateTime.now(), fechaHora).toHours();
        return horasRestantes > 8;
    }
    }
