/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.EspecialidadDaoImpl;
import Interface.IEspecialidad;
import Model.Especialidad;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class Test_Especialidad {
    IEspecialidad dao = new EspecialidadDaoImpl();

    public static void main(String[] args) {
        Test_Especialidad te = new Test_Especialidad();

        // Descomentar el método a probar
        te.listar();
        // te.existeEspecialidad();
    }

    // LISTAR ESPECIALIDADES ACTIVAS
    // RN-01: Solo con al menos 1 médico activo asignado
    public void listar() {
        List<Especialidad> lista = dao.listarEspecialidades();
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tNombre\t\t\tDuración (min)\tActivo");
            System.out.println("─".repeat(60));
            for (Especialidad e : lista) {
                System.out.println(e.getId()
                        + "\t" + e.getNombre()
                        + "\t\t" + e.getDuracionMinutos() + " min"
                        + "\t\t" + (e.isActivo() ? "SI" : "NO"));
            }
        } else {
            System.out.println(" NO HAY ESPECIALIDADES ACTIVAS CON MÉDICOS ASIGNADOS");
        }
    }

    // VERIFICAR SI EXISTE ESPECIALIDAD
    // RN-D09: Valida antes de mostrar al paciente
    public void existeEspecialidad() {
        String[] nombres = {"Endodoncia", "Ortodoncia", "Cirugía Laser", "Limpieza Dental"};

        System.out.println("VERIFICACIÓN DE ESPECIALIDADES:");
        System.out.println("─".repeat(40));
        for (String nombre : nombres) {
            boolean existe = dao.existeEspecialidad(nombre);
            System.out.println(" " + nombre + ": "
                    + (existe ? "EXISTE " : "NO EXISTE "));
        }
    }
    
}
