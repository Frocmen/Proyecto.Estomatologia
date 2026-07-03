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
  int registrar(Cita cita);                  
    boolean cancelar(int idCita);                
    boolean marcarComoAtendida(int idCita);      
    boolean reprogramar(int idCita, LocalDateTime nuevaFecha); 
    
    Cita buscarPorId(int idCita);
    List<Cita> listarPorPaciente(int idPaciente);
    List<Cita> listarPorProfesional(int idProfesional);
    List<Cita> listarTodas();
    
    
    List<Cita> consultarDisponibilidad(String especialidad, LocalDateTime fecha);
    
    
    boolean puedeModificarOCancelar(int idCita);
}
