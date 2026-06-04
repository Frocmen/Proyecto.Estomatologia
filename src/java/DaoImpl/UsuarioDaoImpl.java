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

     private Connection cn;
    
    @Override
    public Usuario validate(String usuario, String clave) {
      
      Usuario u = null;
        Persona p = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            cn = ConexionSingleton.getConnection();
            u = new Usuario();

            // Generar hash SHA-256 de la clave ingresada
            String hashedClave = u.HashClave(clave);

            // ── PASO 1: Buscar en USUARIOS_INTERNOS (recepcionista, admin, odontólogo)
            query = " SELECT ui.ID, ui.EMAIL, ui.PASSWORD_HASH, r.NOMBRE AS ROL "
                  + " FROM USUARIOS_INTERNOS ui "
                  + " INNER JOIN USUARIO_ROL ur ON ui.ID = ur.USUARIO_ID "
                  + " INNER JOIN ROLES r ON ur.ROL_ID = r.ID "
                  + " WHERE ui.EMAIL = ? "
                  + " AND ui.PASSWORD_HASH = ? "
                  + " AND ui.ACTIVO = 'S' ";

            st = cn.prepareStatement(query);
            st.setString(1, usuario);
            st.setString(2, hashedClave);
            rs = st.executeQuery();

            if (rs.next()) {
                // Usuario interno encontrado
                u = new Usuario();
                p = new Persona();

                u.setId_usuario(rs.getInt("ID"));
                u.setUsuario(rs.getString("EMAIL"));
                u.setClave(rs.getString("PASSWORD_HASH"));
                u.setRol(Rol.valueOf(rs.getString("ROL").toUpperCase()));

                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("EMAIL")); // nombre del usuario interno
                u.setPersona(p);

                System.out.println(" USUARIO INTERNO VALIDADO: " + u.getUsuario());
                return u;
            }

            // ── PASO 2: Buscar en PACIENTES (pacientes externos)
            query = " SELECT ID, NOMBRE, APELLIDO, DNI, EMAIL, TELEFONO, PASSWORD_HASH "
                  + " FROM PACIENTES "
                  + " WHERE EMAIL = ? "
                  + " AND PASSWORD_HASH = ? "
                  + " AND ACTIVO = 'S' "
                  + " AND VERIFICADO = 'S' ";

            st = cn.prepareStatement(query);
            st.setString(1, usuario);
            st.setString(2, hashedClave);
            rs = st.executeQuery();

            if (rs.next()) {
                // Paciente encontrado
                u = new Usuario();
                p = new Persona();

                u.setId_usuario(rs.getInt("ID"));
                u.setUsuario(rs.getString("EMAIL"));
                u.setClave(rs.getString("PASSWORD_HASH"));
                u.setRol(Rol.PACIENTE);

                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setEmail(rs.getString("EMAIL"));
                p.setTelefono(rs.getString("TELEFONO"));
                u.setPersona(p);

                System.out.println(" PACIENTE VALIDADO: " + u.getUsuario());
                return u;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL VALIDAR USUARIO: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println(" NO SE LOGRÓ VALIDAR EL USUARIO");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception e) {
                }
            }
        }
        return null; 
    }
    

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        PreparedStatement st;
        ResultSet rs;
        String query;
        int idGenerado = 0;
        String[] cols = {"ID"};

        try {
            cn = ConexionSingleton.getConnection();

            // ORACLE: INSERT con SEQUENCE
            query = " INSERT INTO USUARIOS_INTERNOS(ID, NOMBRE, APELLIDO, EMAIL, PASSWORD_HASH, ACTIVO) "
                  + " VALUES(SEQ_USUARIOS_INT.NEXTVAL, ?, ?, ?, ?, 'S') ";

            st = cn.prepareStatement(query, cols);
            st.setString(1, usuario.getPersona().getNombre());
            st.setString(2, usuario.getPersona().getApellido());
            st.setString(3, usuario.getUsuario());      // Email como username
            st.setString(4, usuario.HashClave(usuario.getClave())); // Hash SHA-256
            int r = st.executeUpdate();

            if (r > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                    System.out.println(" USUARIO_INTERNO creado con ID: " + idGenerado);
                }

                // Asignar el rol en USUARIO_ROL
                if (idGenerado > 0 && usuario.getRol() != null) {
                    query = " INSERT INTO USUARIO_ROL(USUARIO_ID, ROL_ID) "
                          + " SELECT ?, ID FROM ROLES WHERE NOMBRE = ? ";
                    st = cn.prepareStatement(query);
                    st.setInt(1, idGenerado);
                    st.setString(2, usuario.getRol().name());
                    st.executeUpdate();
                    System.out.println(" ROL asignado: " + usuario.getRol().name());
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL REGISTRAR USUARIO: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
        } finally {
            if (cn != null) {
                try {
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    @Override
    public Usuario buscarPorUsername(String username) {
      PreparedStatement st;
        ResultSet rs;
        String query;
        Usuario u = null;
        Persona p = null;

        try {
            cn = ConexionSingleton.getConnection();

            query = " SELECT ui.ID, ui.NOMBRE, ui.APELLIDO, ui.EMAIL, ui.PASSWORD_HASH, r.NOMBRE AS ROL "
                  + " FROM USUARIOS_INTERNOS ui "
                  + " INNER JOIN USUARIO_ROL ur ON ui.ID = ur.USUARIO_ID "
                  + " INNER JOIN ROLES r ON ur.ROL_ID = r.ID "
                  + " WHERE ui.EMAIL = ? AND ui.ACTIVO = 'S' ";

            st = cn.prepareStatement(query);
            st.setString(1, username);
            rs = st.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                p = new Persona();

                u.setId_usuario(rs.getInt("ID"));
                u.setUsuario(rs.getString("EMAIL"));
                u.setClave(rs.getString("PASSWORD_HASH"));
                u.setRol(Rol.valueOf(rs.getString("ROL").toUpperCase()));

                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                u.setPersona(p);
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL BUSCAR USUARIO: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
        } finally {
            if (cn != null) {
                try {
                } catch (Exception e) {
                }
            }
        }
        return u;
    }

    @Override
    public boolean cambiarClave(int idUsuario, String nuevaClave) {
         PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();
            Usuario u = new Usuario();
            String hashedNuevaClave = u.HashClave(nuevaClave);

            // Intentar actualizar en USUARIOS_INTERNOS primero
            query = " UPDATE USUARIOS_INTERNOS SET PASSWORD_HASH = ? WHERE ID = ? ";
            st = cn.prepareStatement(query);
            st.setString(1, hashedNuevaClave);
            st.setInt(2, idUsuario);
            int r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" CLAVE ACTUALIZADA en USUARIOS_INTERNOS");
                return true;
            }

            // Si no encontró, intentar en PACIENTES
            query = " UPDATE PACIENTES SET PASSWORD_HASH = ? WHERE ID = ? ";
            st = cn.prepareStatement(query);
            st.setString(1, hashedNuevaClave);
            st.setInt(2, idUsuario);
            r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" CLAVE ACTUALIZADA en PACIENTES");
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL CAMBIAR CLAVE: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
        } finally {
            if (cn != null) {
                try {
                } catch (Exception e) {
                }
            }
        }
        return false;
    }    
}
