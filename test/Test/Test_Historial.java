/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.HistorialDaoImpl;
import Interface.IHistorial;
import Model.Historial;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class Test_Historial {

     IHistorial dao = new HistorialDaoImpl();

    public static void main(String[] args) {
        Test_Historial th = new Test_Historial();

       
        // th.registrarAtencion();
        th.listarPorCita();
        // th.listarPorProfesional();
    }

   
    public void registrarAtencion() {
        Historial h = new Historial();
        h.setIdCita(1);
        h.setIdProfesionalAtendio(1);
        h.setPiezaDental("46");
        h.setTratamientoRealizado("Endodoncia completa");
        h.setObservaciones("Paciente sin complicaciones. Próximo control en 30 días.");
        h.setFechaHoraInicioReal(LocalDateTime.of(2026, 6, 20, 10, 0));
        h.setFechaHoraFinReal(LocalDateTime.of(2026, 6, 20, 11, 0));

        boolean result = dao.registrarAtencion(h);
        if (result) {
            System.out.println(" ATENCIÓN REGISTRADA en historial con ID: " + h.getId());
            System.out.println(" Cita ID:       " + h.getIdCita());
            System.out.println(" Pieza:         " + h.getPiezaDental());
            System.out.println(" Tratamiento:   " + h.getTratamientoRealizado());
            System.out.println(" Observaciones: " + h.getObservaciones());
            System.out.println(" Inicio:        " + h.getFechaHoraInicioReal());
            System.out.println(" Fin:           " + h.getFechaHoraFinReal());
        } else {
            System.out.println(" |ERROR| No se logró registrar la atención");
        }
    }

  
    public void listarPorCita() {
        List<Historial> lista = dao.listarPorCita(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("HISTORIAL DE CITA ID: 1");
            System.out.println("─".repeat(60));
            for (Historial h : lista) {
                System.out.println(" ID Historial:  " + h.getId());
                System.out.println(" Pieza:         " + h.getPiezaDental());
                System.out.println(" Tratamiento:   " + h.getTratamientoRealizado());
                System.out.println(" Observaciones: " + h.getObservaciones());
                System.out.println(" Inicio:        " + h.getFechaHoraInicioReal());
                System.out.println(" Fin:           " + h.getFechaHoraFinReal());
                System.out.println(" Registrado:    " + h.getFechaRegistro());
                System.out.println("─".repeat(60));
            }
        } else {
            System.out.println(" No hay registros de historial para la cita ID: 1");
        }
    }

  
    public void listarPorProfesional() {
        List<Historial> lista = dao.listarPorProfesional(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("HISTORIAL DEL PROFESIONAL ID: 1");
            System.out.println("─".repeat(60));
            for (Historial h : lista) {
                System.out.println(" Cita ID:       " + h.getIdCita());
                System.out.println(" Pieza:         " + h.getPiezaDental());
                System.out.println(" Tratamiento:   " + h.getTratamientoRealizado());
                System.out.println(" Inicio:        " + h.getFechaHoraInicioReal());
                System.out.println(" Registrado:    " + h.getFechaRegistro());
                System.out.println("─".repeat(60));
            }
        } else {
            System.out.println(" No hay registros de historial para el profesional ID: 1");
        }
    }
}