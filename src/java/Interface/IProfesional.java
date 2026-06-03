/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Profesional;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface IProfesional {
    int registrarProfesional(Profesional profesional);
    List<Profesional> listarProfesionales();
    List<Profesional> buscarPorEspecialidad(String especialidad);
    Profesional buscarPorId(int id);
}
