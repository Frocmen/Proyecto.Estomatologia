/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Test;

import DaoImpl.UsuarioDaoImpl;
import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;

/**
 *
 * @author Frocmen
 */
public class Test_Usuario {

  // falta ver este pedo :
    
    
    IUsuario dao = new UsuarioDaoImpl();

    public static void main(String[] args) {
        Test_Usuario tu = new Test_Usuario();

        // Descomentar el método a probar
        // tu.registrarUsuario();
        //  tu.validarUsuario();
        // tu.buscarPorUsername();
       tu.cambiarClave();
    }

    // REGISTRAR USUARIO INTERNO 
    public void registrarUsuario() {
        Persona p = new Persona();
        p.setNombre("Carlos");
        p.setApellido("Mendez");

        Usuario u = new Usuario();
        u.setUsuario("carlos@dental.com ");
        u.setClave("admin123");
        u.setRol(Rol.ADMIN);
        u.setPersona(p);

        boolean result = dao.registrarUsuario(u);
        if (result) {
            System.out.println(" USUARIO REGISTRADO: " + u.getUsuario());
            System.out.println(" ROL: " + u.getRol());
        } else {
            System.out.println(" |ERROR| No se logró registrar el usuario");
        }
    }

    // VALIDAR LOGIN
    public void validarUsuario() {
        // Probar con usuario interno
        System.out.println("=== PRUEBA USUARIO INTERNO ===");
        Usuario u = dao.validate("admin@dental.com", "admin123");
        if (u != null && u.getUsuario() != null) {
            System.out.println(" BIENVENIDO: " + u.getUsuario());
            System.out.println(" ROL: " + u.getRol());
            if (u.getPersona() != null) {
                System.out.println(" NOMBRE: " + u.getPersona().getNombre());
            }
        } else {
            System.out.println(" CREDENCIALES INCORRECTAS");
        }

        // Probar con paciente
        System.out.println("=== PRUEBA PACIENTE ===");
        Usuario up = dao.validate("juan@email.com", "admin123");
        if (up != null && up.getUsuario() != null) {
            System.out.println(" BIENVENIDO PACIENTE: " + up.getUsuario());
            System.out.println(" ROL: " + up.getRol());
            if (up.getPersona() != null) {
                System.out.println(" NOMBRE: " + up.getPersona().getNombre()
                        + " " + up.getPersona().getApellido());
            }
        } else {
            System.out.println(" CREDENCIALES INCORRECTAS");
        }
    }

    // BUSCAR POR USERNAME 
    public void buscarPorUsername() {
        Usuario u = dao.buscarPorUsername("admin@dental.com");
        if (u != null) {
            System.out.println(" USUARIO ENCONTRADO: " + u.getUsuario());
            System.out.println(" ROL: " + u.getRol());
        } else {
            System.out.println(" |ERROR| Usuario no encontrado");
        }
    }

    // CAMBIAR CLAVE
    public void cambiarClave() {
        boolean result = dao.cambiarClave(21, "nuevaClave123");
        if (result) {
            System.out.println(" CLAVE ACTUALIZADA CORRECTAMENTE");
        } else {
            System.out.println(" |ERROR| No se logró cambiar la clave");
        }
    }
}
