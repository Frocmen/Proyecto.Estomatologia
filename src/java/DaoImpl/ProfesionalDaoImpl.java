/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IProfesional;
import Model.Profesional;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Frocmen
 */
public class ProfesionalDaoImpl implements IProfesional{

    @Override
    public int registrarProfesional(Profesional profesional) {
      String sql = "INSERT INTO profesional(id_persona, especialidad, colegiatura) VALUES (?, ?, ?)";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, profesional.getId_persona());
            ps.setString(2, profesional.getEspecialidad());
            ps.setString(3, profesional.getColegiatura() != null ? profesional.getColegiatura() : "");
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);   // Retorna el ID generado
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR al registrar profesional: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Profesional> listarProfesionales() {
       List<Profesional> lista = new ArrayList<>();
        String sql = "SELECT p.*, pr.especialidad, pr.colegiatura FROM persona p "
                   + "INNER JOIN profesional pr ON p.id_persona = pr.id_persona";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId_persona(rs.getInt("id_persona"));
                prof.setNombre(rs.getString("nombre"));
                prof.setApellido(rs.getString("apellido"));
                prof.setTelefono(rs.getString("telefono"));
                prof.setEmail(rs.getString("email"));
                prof.setDireccion(rs.getString("direccion"));
                prof.setEspecialidad(rs.getString("especialidad"));
                prof.setColegiatura(rs.getString("colegiatura"));
                
                lista.add(prof);
            }
        } catch (Exception e) {
            System.out.println("ERROR al listar profesionales: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Profesional> buscarPorEspecialidad(String especialidad) {
        List<Profesional> lista = new ArrayList<>();
        String sql = "SELECT p.*, pr.especialidad, pr.colegiatura FROM persona p "
                   + "INNER JOIN profesional pr ON p.id_persona = pr.id_persona "
                   + "WHERE pr.especialidad = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, especialidad);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId_persona(rs.getInt("id_persona"));
                prof.setNombre(rs.getString("nombre"));
                prof.setApellido(rs.getString("apellido"));
                prof.setTelefono(rs.getString("telefono"));
                prof.setEmail(rs.getString("email"));
                prof.setDireccion(rs.getString("direccion"));
                prof.setEspecialidad(rs.getString("especialidad"));
                prof.setColegiatura(rs.getString("colegiatura"));
                
                lista.add(prof);
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar por especialidad: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Profesional buscarPorId(int id) {
      String sql = "SELECT p.*, pr.especialidad, pr.colegiatura FROM persona p "
                   + "INNER JOIN profesional pr ON p.id_persona = pr.id_persona "
                   + "WHERE p.id_persona = ?";
        
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId_persona(rs.getInt("id_persona"));
                prof.setNombre(rs.getString("nombre"));
                prof.setApellido(rs.getString("apellido"));
                prof.setTelefono(rs.getString("telefono"));
                prof.setEmail(rs.getString("email"));
                prof.setDireccion(rs.getString("direccion"));
                prof.setEspecialidad(rs.getString("especialidad"));
                prof.setColegiatura(rs.getString("colegiatura"));
                return prof;
            }
        } catch (Exception e) {
            System.out.println("ERROR al buscar profesional por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    } 
    
}
    
