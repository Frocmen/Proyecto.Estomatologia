/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.PacienteDaoImpl;
import Interface.IPaciente;
import Model.Paciente;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class Test_Paciente {
    IPaciente dao = new PacienteDaoImpl();

    public static void main(String[] args) {
        Test_Paciente tp = new Test_Paciente();


        // tp.registrar();
        tp.listar();
        // tp.buscarPorDni();
        // tp.buscarPorId();
        // tp.editar();
        // tp.eliminar();
    }


    public void registrar() {
        Paciente p = new Paciente();
        p.setNombre("Ana");
        p.setApellido("Torres");
        p.setDni("45678901");
        p.setTelefono("987654321");
        p.setEmail("ana.torres@gmail.com");

        int result = dao.registrarPaciente(p);
        if (result > 0) {
            System.out.println(" PACIENTE REGISTRADO con ID: " + result);
        } else {
            System.out.println(" |ERROR| No se logró registrar el paciente");
        }
    }


    public void listar() {
        List<Paciente> lista = dao.listarPacientes();
        if (lista != null && !lista.isEmpty()) {
            System.out.println("ID\tNombre\t\tApellido\tDNI\t\tEmail");
            System.out.println("─".repeat(70));
            for (Paciente p : lista) {
                System.out.println(p.getId()
                        + "\t" + p.getNombre()
                        + "\t\t" + p.getApellido()
                        + "\t\t" + p.getDni()
                        + "\t" + p.getEmail());
            }
        } else {
            System.out.println(" NO HAY PACIENTES REGISTRADOS");
        }
    }


    public void buscarPorDni() {
        Paciente p = dao.buscarPacientePorDni("12345678");
        if (p != null) {
            System.out.println(" PACIENTE ENCONTRADO");
            System.out.println(" ID:       " + p.getId());
            System.out.println(" Nombre:   " + p.getNombre() + " " + p.getApellido());
            System.out.println(" DNI:      " + p.getDni());
            System.out.println(" Teléfono: " + p.getTelefono());
            System.out.println(" Email:    " + p.getEmail());
        } else {
            System.out.println(" |ERROR| Paciente no encontrado");
        }
    }


    public void buscarPorId() {
        Paciente p = dao.buscarPorId(1);
        if (p != null) {
            System.out.println(" PACIENTE ENCONTRADO");
            System.out.println(" ID:       " + p.getId());
            System.out.println(" Nombre:   " + p.getNombre() + " " + p.getApellido());
            System.out.println(" DNI:      " + p.getDni());
            System.out.println(" Email:    " + p.getEmail());
        } else {
            System.out.println(" |ERROR| Paciente no encontrado con ID: 1");
        }
    }


    public void editar() {
        Paciente p = new Paciente();
        p.setId(1);
        p.setNombre("Juan Editado");
        p.setApellido("Perales Sosa");
        p.setDni("12345678");
        p.setTelefono("999111222");
        p.setEmail("juan.editado@email.com");

        boolean result = dao.editar(p);
        if (result) {
            System.out.println(" PACIENTE ACTUALIZADO CORRECTAMENTE");
        } else {
            System.out.println(" |ERROR| No se logró actualizar el paciente");
        }
    }


    public void eliminar() {
        boolean result = dao.delete(1);
        if (result) {
            System.out.println(" PACIENTE DESACTIVADO CORRECTAMENTE");
        } else {
            System.out.println(" |ERROR| No se logró desactivar el paciente");
        }
    }
}