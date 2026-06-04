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
        
        //tc.registrar();
        //tc.cancelar();
        //tc.reprogramar();
        //tc.marcarComoAtendida();
        //tc.buscarPorId();
        tc.listarPorPaciente();
        //tc.listarPorProfesional();
        //tc.listarTodas();
        //tc.consultarDisponibilidad();
        //tc.puedeModificarOCancelar();
    }
 
    public void registrar() {
        Paciente paciente = new Paciente();
        paciente.setId(1);
 
        Profesional profesional = new Profesional();
        profesional.setId(1);
        profesional.setEspecialidadPrincipalId(null);
 
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setProfesional(profesional);
        cita.setFechaHora(LocalDateTime.of(2025, 7, 20, 10, 0));
        cita.setEstado("CONFIRMADA");
        cita.setMotivo("Revisión general");
 
        int result = dao.registrar(cita);
        if (result > 0) {
            System.out.println("Cita registrada con ID: " + result);
        } else {
            System.out.println("|ERROR| No se logró registrar la cita");
        }
    }
 
    public void cancelar() {
        boolean result = dao.cancelar(1);
        if (result) {
            System.out.println("Cita CANCELADA correctamente");
        } else {
            System.out.println("|ERROR| No se logró cancelar la cita");
        }
    }
 
    public void reprogramar() {
        LocalDateTime nuevaFecha = LocalDateTime.of(2025, 8, 5, 14, 30);
        boolean result = dao.reprogramar(1, nuevaFecha);
        if (result) {
            System.out.println("Cita REPROGRAMADA para: " + nuevaFecha);
        } else {
            System.out.println("|ERROR| No se logró reprogramar la cita");
        }
    }
 
    public void marcarComoAtendida() {
        boolean result = dao.marcarComoAtendida(1);
        if (result) {
            System.out.println("Cita marcada como ATENDIDA");
        } else {
            System.out.println("|ERROR| No se logró marcar como atendida");
        }
    }
 
    public void buscarPorId() {
        Cita cita = dao.buscarPorId(1);
        if (cita != null) {
            System.out.println("Cita encontrada");
            System.out.println("ID: "         + cita.getId_cita());
            System.out.println("Estado: "     + cita.getEstado());
            System.out.println("Fecha/Hora: " + cita.getFechaHora());
            System.out.println("Paciente: "   + cita.getPaciente().getNombre()
                                              + " " + cita.getPaciente().getApellido());
            System.out.println("Profesional: "+ cita.getProfesional().getNombre()
                                              + " " + cita.getProfesional().getApellido());
            System.out.println("Motivo: "     + cita.getMotivo());
        } else {
            System.out.println("|ERROR| No se encontró la cita");
        }
    }
 
    public void listarPorPaciente() {
        List<Cita> lista = dao.listarPorPaciente(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tFecha/Hora\tEstado\tProfesional");
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getProfesional().getNombre()
                        + " "  + c.getProfesional().getApellido());
            }
        } else {
            System.out.println("No hay citas para este paciente");
        }
    }
 
    public void listarPorProfesional() {
        List<Cita> lista = dao.listarPorProfesional(1);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tFecha/Hora\tEstado\tPaciente");
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getPaciente().getNombre()
                        + " "  + c.getPaciente().getApellido());
            }
        } else {
            System.out.println("No hay citas para este profesional");
        }
    }
 
    public void listarTodas() {
        List<Cita> lista = dao.listarTodas();
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tFecha/Hora\tEstado\tPaciente\tProfesional");
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getPaciente().getNombre()
                        + " "  + c.getPaciente().getApellido()
                        + "\t" + c.getProfesional().getNombre()
                        + " "  + c.getProfesional().getApellido());
            }
        } else {
            System.out.println("No hay citas registradas");
        }
    }
 
    public void consultarDisponibilidad() {
        LocalDateTime fecha = LocalDateTime.of(2025, 7, 20, 8, 0);
        List<Cita> lista = dao.consultarDisponibilidad("ORTODONCIA", fecha);
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tFecha/Hora\tEstado\tProfesional");
            for (Cita c : lista) {
                System.out.println(c.getId_cita()
                        + "\t" + c.getFechaHora()
                        + "\t" + c.getEstado()
                        + "\t" + c.getProfesional().getNombre()
                        + " "  + c.getProfesional().getApellido());
            }
        } else {
            System.out.println("No hay disponibilidad para esa especialidad y fecha");
        }
    }
 
    public void puedeModificarOCancelar() {
        boolean result = dao.puedeModificarOCancelar(1);
        if (result) {
            System.out.println("La cita SI puede modificarse o cancelarse (RN-05: mas de 8 horas)");
        } else {
            System.out.println("La cita NO puede modificarse o cancelarse (RN-05: menos de 8 horas)");
        }
    }
}
