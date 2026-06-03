/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Especialidad;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface IEspecialidad {
    
    List<Especialidad> listarEspecialidades();
    boolean existeEspecialidad(String nombre);
}
