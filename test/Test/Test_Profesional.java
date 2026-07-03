/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.ProfesionalDaoImpl;
import Interface.IProfesional;
import Model.Profesional;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class Test_Profesional {
    IProfesional dao = new ProfesionalDaoImpl();

    public static void main(String[] args) {
        Test_Profesional tpr = new Test_Profesional();

        // tpr.registrar();
        tpr.listar();
        // tpr.buscarPorEspecialidad();
        // tpr.buscarPorId();
    }

   
    public void registrar() {
        Profesional p = new Profesional();
        p.setNombre("Luis");
        p.setApellido("Ramirez");
        p.setEmail("luis.ramirez@dental.com");
        p.setTelefono("912345678");

        int result = dao.registrarProfesional(p);
        if (result > 0) {
            System.out.println(" PROFESIONAL REGISTRADO con ID: " + result);
        } else {
            System.out.println(" |ERROR| No se logró registrar el profesional");
        }
    }

 
    public void listar() {
        List<Profesional> lista = dao.listarProfesionales();
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tNombre\t\tApellido\tEmail");
            System.out.println("─".repeat(60));
            for (Profesional p : lista) {
                System.out.println(p.getId()
                        + "\t" + p.getNombre()
                        + "\t\t" + p.getApellido()
                        + "\t\t" + p.getEmail());
            }
        } else {
            System.out.println(" NO HAY PROFESIONALES REGISTRADOS");
        }
    }


    public void buscarPorEspecialidad() {
        List<Profesional> lista = dao.buscarPorEspecialidad("Endodoncia");
        if (lista != null && !lista.isEmpty()) {
            System.out.println(" PROFESIONALES CON ESPECIALIDAD ENDODONCIA:");
            System.out.println("─".repeat(40));
            for (Profesional p : lista) {
                System.out.println(" - " + p.getNombre()
                        + " " + p.getApellido()
                        + " | Email: " + p.getEmail());
            }
        } else {
            System.out.println(" No hay profesionales para esa especialidad");
        }
    }


    public void buscarPorId() {
        Profesional p = dao.buscarPorId(1);
        if (p != null) {
            System.out.println(" PROFESIONAL ENCONTRADO");
            System.out.println(" ID:       " + p.getId());
            System.out.println(" Nombre:   " + p.getNombreCompleto());
            System.out.println(" Email:    " + p.getEmail());
            System.out.println(" Teléfono: " + p.getTelefono());
            System.out.println(" Activo:   " + p.isActivo());
        } else {
            System.out.println(" |ERROR| Profesional no encontrado con ID: 1");
        }
    }
}