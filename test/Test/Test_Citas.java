/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.CitaDaoImpl;
import Interface.ICita;
import Model.Cita;
import Model.Paciente;
import Model.Profesional;
import java.time.LocalDateTime;
import java.util.List;
/**
 *
 * @author Frocmen
 */
public class Test_Citas {

    ICita dao = new CitaDaoImpl();
    
    public static void main(String[] args) {
        
          Test_Citas tc = new Test_Citas();
        
       // tc.registrar();
        //tc.cancelar();
        //tc.reprogramar();
        //tc.marcarComoAtendida();
        //tc.buscarPorId();
        //tc.listarPorPaciente();
        //tc.listarPorProfesional();
        tc.listarTodas();
        //tc.consultarDisponibilidad();
        //tc.puedeModificarOCancelar();
    }
 
    public void registrar() {
       Paciente paciente = new Paciente();
        paciente.setId(1);

        Profesional profesional = new Profesional();
        profesional.setId(1);

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setProfesional(profesional);
        cita.setEspecialidadId(1); // ← ID de la especialidad en tabla ESPECIALIDADES
        cita.setFechaHora(LocalDateTime.of(2026, 8, 15, 10, 0));
        cita.setEstado("CONFIRMADA");
        cita.setMotivo("Revisión general");

        int result = dao.registrar(cita);
        if (result > 0) {
            System.out.println(" CITA REGISTRADA con ID: " + result);
        } else {
            System.out.println(" |ERROR| No se logró registrar la cita");
        }
    }
 
    public void cancelar() {
        boolean result = dao.cancelar(1);
        if (result) {
            System.out.println(" CITA CANCELADA correctamente");
        } else {
            System.out.println(" |ERROR| No se logró cancelar");
        }
    }
 
    public void reprogramar() {
        LocalDateTime nuevaFecha = LocalDateTime.of(2026, 8, 20, 14, 30);
        boolean result = dao.reprogramar(1, nuevaFecha);
        if (result) {
            System.out.println(" CITA REPROGRAMADA para: " + nuevaFecha);
        } else {
            System.out.println(" |ERROR| No se logró reprogramar");
        }
    }
 
    public void marcarComoAtendida() {
         boolean result = dao.marcarComoAtendida(1);
        if (result) {
            System.out.println(" CITA marcada como ATENDIDA");
        } else {
            System.out.println(" |ERROR| No se logró marcar como atendida");
        }
    }
 
    public void buscarPorId() {
        Cita cita = dao.buscarPorId(1);
        if (cita != null) {
            System.out.println(" CITA ENCONTRADA");
            System.out.println(" ID:          " + cita.getId_cita());
            System.out.println(" Estado:      " + cita.getEstado());
            System.out.println(" Fecha/Hora:  " + cita.getFechaHora());
            System.out.println(" Paciente:    " + cita.getPaciente().getNombre()
                    + " " + cita.getPaciente().getApellido());
            System.out.println(" Profesional: " + cita.getProfesional().getNombre()
                    + " " + cita.getProfesional().getApellido());
            System.out.println(" Motivo:      " + cita.getMotivo());
        } else {
            System.out.println(" |ERROR| No se encontró la cita");
        }
    }
 
    public void listarPorPaciente() {
         List<Cita> lista = dao.listarPorPaciente(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("CITAS DEL PACIENTE ID: 1");
            System.out.println("ID\tFecha/Hora\t\tEstado\t\tProfesional");
            System.out.println("─".repeat(70));
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getProfesional().getNombre()
                        + " " + c.getProfesional().getApellido());
            }
        } else {
            System.out.println(" No hay citas para el paciente ID: 1");
        }
    }
 
    public void listarPorProfesional() {
        List<Cita> lista = dao.listarPorProfesional(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("CITAS DEL PROFESIONAL ID: 1");
            System.out.println("ID\tFecha/Hora\t\tEstado\t\tPaciente");
            System.out.println("─".repeat(70));
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getPaciente().getNombre()
                        + " " + c.getPaciente().getApellido());
            }
        } else {
            System.out.println(" No hay citas para el profesional ID: 1");
        }
    }
 
    public void listarTodas() {
        List<Cita> lista = dao.listarTodas();
        if (lista != null && !lista.isEmpty()) {
            System.out.println("TODAS LAS CITAS DEL SISTEMA");
            System.out.println("ID\tFecha/Hora\t\tEstado\t\tPaciente\t\tProfesional");
            System.out.println("─".repeat(90));
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getPaciente().getNombre()
                        + " " + c.getPaciente().getApellido()
                        + "\t\t" + c.getProfesional().getNombre()
                        + " " + c.getProfesional().getApellido());
            }
        } else {
            System.out.println(" No hay citas registradas en el sistema");
        }
    }
 
    public void consultarDisponibilidad() {
         LocalDateTime fecha = LocalDateTime.of(2026, 6, 20, 8, 0);
        List<Cita> lista = dao.consultarDisponibilidad("Endodoncia", fecha);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("CITAS CONFIRMADAS — Endodoncia — " + fecha.toLocalDate());
            System.out.println("ID\tFecha/Hora\t\tEstado\t\tProfesional");
            System.out.println("─".repeat(70));
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getProfesional().getNombre()
                        + " " + c.getProfesional().getApellido());
            }
        } else {
            System.out.println(" No hay citas confirmadas para Endodoncia en esa fecha");
        }
    }
 
    public void puedeModificarOCancelar() {
         boolean result = dao.puedeModificarOCancelar(1);
        if (result) {
            System.out.println(" La cita SI puede modificarse o cancelarse");
            System.out.println(" RN-05: Faltan más de 8 horas");
        } else {
            System.out.println(" La cita NO puede modificarse o cancelarse");
            System.out.println(" RN-05: Faltan menos de 8 horas o cita no encontrada");
        }
    }
}
