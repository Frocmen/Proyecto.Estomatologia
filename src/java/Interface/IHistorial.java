/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Historial;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface IHistorial {
     boolean registrarAtencion(Historial historial);
    List<Historial> listarPorCita(int idCita);
    List<Historial> listarPorProfesional(int idProfesional);
}
