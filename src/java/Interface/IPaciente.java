/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Paciente;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public interface IPaciente {
   int registrarPaciente(Paciente paciente);
    List<Paciente> listarPacientes();
    boolean editar(Paciente paciente);
    boolean delete(int id);
    Paciente buscarPacientePorDni(String dni);
    Paciente buscarPorId(int id);
    Paciente buscarPorEmail(String email); // ← NUEVO
}
