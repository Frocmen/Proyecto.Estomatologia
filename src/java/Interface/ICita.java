/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Cita;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface ICita {
  int registrar(Cita cita);                    // RN-01
    boolean cancelar(int idCita);                // RN-05, RN-10
    boolean marcarComoAtendida(int idCita);      // RN-03
    boolean reprogramar(int idCita, LocalDateTime nuevaFecha); 
    
    Cita buscarPorId(int idCita);
    List<Cita> listarPorPaciente(int idPaciente);
    List<Cita> listarPorProfesional(int idProfesional);
    List<Cita> listarTodas();
    
    // Consultar disponibilidad (HU-02)
    List<Cita> consultarDisponibilidad(String especialidad, LocalDateTime fecha);
    
    // RN-05: Verificar si se puede modificar/cancelar
    boolean puedeModificarOCancelar(int idCita);
}
