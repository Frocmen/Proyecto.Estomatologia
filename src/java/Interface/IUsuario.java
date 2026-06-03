/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Model.Usuario;

/**
 *
 * @author Frocmen
 */
public interface IUsuario {
    
    Usuario validate(String usuario, String clave);
    boolean registrarUsuario(Usuario usuario);
    Usuario buscarPorUsername(String username);
    boolean cambiarClave(int idUsuario, String nuevaClave);
}
