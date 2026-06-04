/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IPaciente;
import Model.Paciente;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class PacienteDaoImpl implements IPaciente {

    // falta arregar este pedooooooooooooooo
    @Override
    public int registrarPaciente(Paciente paciente) {
        String sql = "INSERT INTO persona(nombre, apellido, dni, telefono, email, direccion) "
                + "VALUES(?,?,?,?,?,?)";

        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getEmail());
            ps.setString(6, paciente.getDireccion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Guardar datos adicionales del paciente
                    guardarDatosPaciente(idGenerado, paciente.getFechaNacimiento());
                    return idGenerado;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR al registrar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private void guardarDatosPaciente(int idPersona, String fechaNacimiento) {
        String sql = "INSERT INTO paciente(id_persona, fecha_nacimiento) VALUES(?,?)";
        try (Connection cn = ConexionSingleton.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPersona);
            ps.setString(2, fechaNacimiento);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Paciente> listarPacientes() {
List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT p.*, pa.fecha_nacimiento FROM persona p "
                   + "INNER JOIN paciente pa ON p.id_persona = pa.id_persona";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setDireccion(rs.getString("direccion"));
                p.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("ERROR al listar pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean editar(Paciente paciente) {
       String sql = "UPDATE persona SET nombre=?, apellido=?, dni=?, telefono=?, email=?, direccion=? "
                   + "WHERE id_persona=?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getEmail());
            ps.setString(6, paciente.getDireccion());
            ps.setInt(7, paciente.getId_persona());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERROR al editar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
       String sql = "DELETE FROM persona WHERE id_persona = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERROR al eliminar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Paciente buscarPacientePorDni(String dni) {
      String sql = "SELECT p.*, pa.fecha_nacimiento FROM persona p "
                   + "INNER JOIN paciente pa ON p.id_persona = pa.id_persona "
                   + "WHERE p.dni = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Paciente p = new Paciente();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setDireccion(rs.getString("direccion"));
                p.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                return p;
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar paciente por DNI: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Paciente buscarPorId(int id) {
        String sql = "SELECT p.*, pa.fecha_nacimiento FROM persona p "
                   + "INNER JOIN paciente pa ON p.id_persona = pa.id_persona "
                   + "WHERE p.id_persona = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Paciente p = new Paciente();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));
                p.setDireccion(rs.getString("direccion"));
                p.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                return p;
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar paciente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    
    }

}
