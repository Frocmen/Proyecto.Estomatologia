/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.*;

/**
 *
 * @author Frocmen
 */
public class UsuarioDaoImpl implements IUsuario{

    @Override
    public Usuario validate(String usuario, String clave) {
      
        String sql = "SELECT u.*, p.* FROM usuario u INNER JOIN persona p ON u.id_persona = p.id_persona "
                   + "WHERE u.usuario = ? AND u.clave = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, clave);  // En producción usar hash
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setUsuario(rs.getString("usuario"));
                u.setClave(rs.getString("clave"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                
                // Crear persona asociada
                Persona p = new Persona();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                u.setPersona(p);
                
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario(usuario, clave, rol, id_persona) VALUES(?,?,?,?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getClave());
            ps.setString(3, usuario.getRol().name());
            ps.setInt(4, usuario.getPersona().getId_persona());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Usuario buscarPorUsername(String username) {
      // Implementación similar a validate
        return null;
    }

    @Override
    public boolean cambiarClave(int idUsuario, String nuevaClave) {
        // Implementar según necesidad
        return false;
    }
    
}
