/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Persona;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface IPersona {
    int insert(Persona persona);
    boolean update(Persona persona);
    Persona buscarPorId(int id);
    Persona buscarPorDni(String dni);
    List<Persona> listar();
    boolean delete(int id);
}
