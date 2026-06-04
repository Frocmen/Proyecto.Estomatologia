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

    @Override
    public int insert(Persona persona, Usuario usuario) {
        String sqlPersona = "INSERT INTO persona(nombre, apellido, dni, telefono, email, direccion) "
                + "VALUES(?,?,?,?,?,?)";

        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());
            ps.setString(3, persona.getDni());
            ps.setString(4, persona.getTelefono());
            ps.setString(5, persona.getEmail());
            ps.setString(6, persona.getDireccion());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idPersona = rs.getInt(1);

                    // Insertar el usuario asociado
                    String sqlUsuario = "INSERT INTO usuario(usuario, clave, rol, id_persona) VALUES(?,?,?,?)";
                    try (PreparedStatement ps2 = cn.prepareStatement(sqlUsuario)) {
                        ps2.setString(1, usuario.getUsuario());
                        ps2.setString(2, usuario.getClave());
                        ps2.setString(3, usuario.getRol().name());
                        ps2.setInt(4, idPersona);
                        ps2.executeUpdate();
                    }

                    return idPersona;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR al insertar persona: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean update(Persona persona) {
        String sql = "UPDATE persona SET nombre=?, apellido=?, dni=?, telefono=?, email=?, direccion=? "
                + "WHERE id_persona=?";

        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());
            ps.setString(3, persona.getDni());
            ps.setString(4, persona.getTelefono());
            ps.setString(5, persona.getEmail());
            ps.setString(6, persona.getDireccion());
            ps.setInt(7, persona.getId_persona());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERROR al actualizar persona: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Persona buscarPorId(int id) {
        String sql = "SELECT * FROM persona WHERE id_persona = ?";
        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona p = new Persona();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setDireccion(rs.getString("direccion"));
                return p;
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar persona por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Persona buscarPorDni(String dni) {
        String sql = "SELECT * FROM persona WHERE dni = ?";
        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona p = new Persona();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setDireccion(rs.getString("direccion"));
                return p;
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar persona por DNI: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Persona> listar() {
        // Implementación pendiente si la necesitas
        return null;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM persona WHERE id_persona = ?";
        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERROR al eliminar persona: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
