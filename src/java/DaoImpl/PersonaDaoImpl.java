/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IPersona;
import Model.Persona;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class PersonaDaoImpl implements IPersona {

    private Connection cn;
    // El parámetro Usuario se usa para capturar la clave inicial
    // Oracle: SEQ_PACIENTES.NEXTVAL genera el ID automáticamente

    @Override
    public int insert(Persona persona, Usuario usuario) {
        PreparedStatement st;
        ResultSet rs;
        String query;
        String[] cols = {"ID"};
        int idGenerado = 0;

        try {
            cn = ConexionSingleton.getConnection();

            // INSERT en PACIENTES — tabla correcta en Oracle
            query = " INSERT INTO PACIENTES(ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL, "
                    + " PASSWORD_HASH, VERIFICADO, FECHA_REGISTRO, ACTIVO) "
                    + " VALUES(SEQ_PACIENTES.NEXTVAL, ?, ?, ?, ?, ?, ?, 'N', SYSDATE, 'S') ";

            st = cn.prepareStatement(query, cols);
            st.setString(1, persona.getNombre());
            st.setString(2, persona.getApellido());
            st.setString(3, persona.getDni());
            st.setString(4, persona.getTelefono());
            st.setString(5, persona.getEmail());

            // Hash de la clave si viene en el usuario
            String hash = "";
            if (usuario != null && usuario.getClave() != null) {
                hash = usuario.HashClave(usuario.getClave());
            }
            st.setString(6, hash);

            int r = st.executeUpdate();

            if (r > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                    System.out.println(" PACIENTE insertado con ID: " + idGenerado);
                }
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL INSERTAR PERSONA: " + e.getMessage());
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
        return idGenerado;
    }

    // UPDATE — Actualiza datos de un paciente en PACIENTES
    @Override
    public boolean update(Persona persona) {
        PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " UPDATE PACIENTES SET NOMBRE=?, APELLIDO=?, DNI=?, "
                    + " TELEFONO=?, EMAIL=? "
                    + " WHERE ID=? ";

            st = cn.prepareStatement(query);
            st.setString(1, persona.getNombre());
            st.setString(2, persona.getApellido());
            st.setString(3, persona.getDni());
            st.setString(4, persona.getTelefono());
            st.setString(5, persona.getEmail());
            st.setInt(6, persona.getId());

            int r = st.executeUpdate();
            if (r > 0) {
                System.out.println(" PACIENTE actualizado ID: " + persona.getId());
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL ACTUALIZAR PERSONA: " + e.getMessage());
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

    // BUSCAR POR ID — Busca en tabla PACIENTES por ID
    @Override
    public Persona buscarPorId(int id) {
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL "
                    + " FROM PACIENTES WHERE ID = ? AND ACTIVO = 'S' ";

            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Persona p = new Persona();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                return p;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL BUSCAR PERSONA POR ID: " + e.getMessage());
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
        return null;
    }
    
    // BUSCAR POR DNI — Busca en tabla PACIENTES por DNI
    // Usado en alta rápida de recepción (RN-09)
    @Override
    public Persona buscarPorDni(String dni) {
          PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL "
                  + " FROM PACIENTES WHERE DNI = ? AND ACTIVO = 'S' ";

            st = cn.prepareStatement(query);
            st.setString(1, dni);
            rs = st.executeQuery();

            if (rs.next()) {
                Persona p = new Persona();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                return p;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL BUSCAR PERSONA POR DNI: " + e.getMessage());
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
        return null;
    }
    
    // LISTAR — Lista todos los pacientes activos
    @Override
    public List<Persona> listar() {
         List<Persona> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            query = " SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL "
                  + " FROM PACIENTES WHERE ACTIVO = 'S' "
                  + " ORDER BY NOMBRE ";

            st = cn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Persona p = new Persona();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                lista.add(p);
            }

            System.out.println(" Personas listadas: " + lista.size());

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR PERSONAS: " + e.getMessage());
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
        return lista;
    }

    // DELETE — Baja lógica en PACIENTES (ACTIVO = 'N')
    // Oracle: nunca eliminar físicamente registros de salud
    @Override
    public boolean delete(int id) {
         PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " UPDATE PACIENTES SET ACTIVO = 'N' WHERE ID = ? ";

            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" PACIENTE desactivado ID: " + id);
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL ELIMINAR PERSONA: " + e.getMessage());
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
