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
import java.security.MessageDigest;
import java.sql.*;

/**
 *
 * @author Frocmen
 */
public class UsuarioDaoImpl implements IUsuario{


    private String hashClave(String clave) {
        try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(clave.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (Exception e) {
        e.printStackTrace();
        return clave;
    }
}

    

    @Override
    public Usuario validate(String correo, String clave) {
        String claveHash = hashClave(clave);

    
        String sqlInterno = "SELECT u.ID, u.NOMBRE, u.APELLIDO, u.EMAIL, r.NOMBRE AS ROL "
                          + "FROM USUARIOS_INTERNOS u "
                          + "INNER JOIN USUARIO_ROL ur ON u.ID = ur.USUARIO_ID "
                          + "INNER JOIN ROLES r ON ur.ROL_ID = r.ID "
                          + "WHERE u.EMAIL = ? AND u.PASSWORD_HASH = ? AND u.ACTIVO = 'S'";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlInterno)) {

            ps.setString(1, correo);
            ps.setString(2, claveHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona persona = new Persona();
                persona.setId(rs.getInt("ID"));
                persona.setNombre(rs.getString("NOMBRE"));
                persona.setApellido(rs.getString("APELLIDO"));
                persona.setEmail(rs.getString("EMAIL"));

                String rolStr = rs.getString("ROL");
                Rol rol;
                try {
                    rol = Rol.valueOf(rolStr);
                } catch (Exception ex) {
                    rol = Rol.RECEPCIONISTA;
                }

                Usuario u = new Usuario();
                u.setUsuario(correo);
                u.setRol(rol);
                u.setPersona(persona);
                System.out.println("✓ Login interno: " + correo + " | ROL: " + rol);
                return u;
            }

        } catch (Exception e) {
            System.out.println("ERROR validate interno: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Buscar en PACIENTES
        String sqlPaciente = "SELECT ID, NOMBRE, APELLIDO, EMAIL "
                           + "FROM PACIENTES "
                           + "WHERE EMAIL = ? AND PASSWORD_HASH = ? AND ACTIVO = 'S'";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlPaciente)) {

            ps.setString(1, correo);
            ps.setString(2, claveHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona persona = new Persona();
                persona.setId(rs.getInt("ID"));      // ← ID del paciente
                persona.setNombre(rs.getString("NOMBRE"));
                persona.setApellido(rs.getString("APELLIDO"));
                persona.setEmail(rs.getString("EMAIL"));

                Usuario u = new Usuario();
                u.setUsuario(correo);
                u.setRol(Rol.PACIENTE);
                u.setPersona(persona);
                System.out.println("✓ Login paciente: " + correo);
                return u;
            }

        } catch (Exception e) {
            System.out.println("ERROR validate paciente: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✗ Login fallido para: " + correo);
        return null;
    }

  
    @Override
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO USUARIOS_INTERNOS(ID, NOMBRE, APELLIDO, EMAIL, "
                   + "PASSWORD_HASH, ACTIVO) "
                   + "VALUES(SEQ_USUARIOS_INT.NEXTVAL, ?, ?, ?, ?, 'S')";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario.getPersona().getNombre());
            ps.setString(2, usuario.getPersona().getApellido());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, hashClave(usuario.getClave()));

            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                // Insertar ROL
                String idQuery = "SELECT SEQ_USUARIOS_INT.CURRVAL FROM DUAL";
                PreparedStatement psId = cn.prepareStatement(idQuery);
                ResultSet rsId = psId.executeQuery();
                if (rsId.next()) {
                    int idUsuario = rsId.getInt(1);
                    int idRol = obtenerIdRol(cn, usuario.getRol().name());
                    if (idRol > 0) {
                        String sqlRol = "INSERT INTO USUARIO_ROL(USUARIO_ID, ROL_ID) VALUES(?,?)";
                        PreparedStatement psRol = cn.prepareStatement(sqlRol);
                        psRol.setInt(1, idUsuario);
                        psRol.setInt(2, idRol);
                        psRol.executeUpdate();
                    }
                }
            }
            return ok;

        } catch (Exception e) {
            System.out.println("ERROR registrarUsuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private int obtenerIdRol(Connection cn, String nombreRol) {
        try {
            PreparedStatement ps = cn.prepareStatement(
                    "SELECT ID FROM ROLES WHERE NOMBRE = ?");
            ps.setString(1, nombreRol);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("ID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

  
    @Override
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT u.ID, u.NOMBRE, u.APELLIDO, u.EMAIL, r.NOMBRE AS ROL "
                   + "FROM USUARIOS_INTERNOS u "
                   + "INNER JOIN USUARIO_ROL ur ON u.ID = ur.USUARIO_ID "
                   + "INNER JOIN ROLES r ON ur.ROL_ID = r.ID "
                   + "WHERE u.EMAIL = ? AND u.ACTIVO = 'S'";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona persona = new Persona();
                persona.setId(rs.getInt("ID"));
                persona.setNombre(rs.getString("NOMBRE"));
                persona.setApellido(rs.getString("APELLIDO"));

                Usuario u = new Usuario();
                u.setUsuario(rs.getString("EMAIL"));
                try {
                    u.setRol(Rol.valueOf(rs.getString("ROL")));
                } catch (Exception ex) {
                    u.setRol(Rol.RECEPCIONISTA);
                }
                u.setPersona(persona);
                return u;
            }

        } catch (Exception e) {
            System.out.println("ERROR buscarPorUsername: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean cambiarClave(int idUsuario, String nuevaClave) {
        String claveHash = hashClave(nuevaClave);

        // Intentar en USUARIOS_INTERNOS
        String sql1 = "UPDATE USUARIOS_INTERNOS SET PASSWORD_HASH = ? WHERE ID = ?";
        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setString(1, claveHash);
            ps.setInt(2, idUsuario);
            if (ps.executeUpdate() > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Intentar en PACIENTES
        String sql2 = "UPDATE PACIENTES SET PASSWORD_HASH = ? WHERE ID = ?";
        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setString(1, claveHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERROR cambiarClave: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}