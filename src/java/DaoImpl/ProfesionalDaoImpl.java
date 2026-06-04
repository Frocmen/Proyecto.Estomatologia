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
     String sql = "INSERT INTO PROFESIONALES(ID, NOMBRE, APELLIDO, EMAIL, TELEFONO, ACTIVO) "
                   + "VALUES(SEQ_PROFESIONALES.NEXTVAL, ?, ?, ?, ?, 'S')";
        String[] cols = {"ID"};

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, cols)) {

            ps.setString(1, profesional.getNombre());
            ps.setString(2, profesional.getApellido());
            ps.setString(3, profesional.getEmail());
            ps.setString(4, profesional.getTelefono());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    profesional.setId(idGenerado);
                    System.out.println("Profesional registrado con ID: " + idGenerado);
                    return idGenerado;
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
        String sql = "SELECT ID, NOMBRE, APELLIDO, EMAIL, TELEFONO, ACTIVO "
                   + "FROM PROFESIONALES WHERE ACTIVO = 'S'";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId(rs.getInt("ID"));
                prof.setNombre(rs.getString("NOMBRE"));
                prof.setApellido(rs.getString("APELLIDO"));
                prof.setEmail(rs.getString("EMAIL"));
                prof.setTelefono(rs.getString("TELEFONO"));
                prof.setActivo("S".equals(rs.getString("ACTIVO")));
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
        // JOIN con PROFESIONAL_ESPECIALIDAD y ESPECIALIDADES
        String sql = "SELECT p.ID, p.NOMBRE, p.APELLIDO, p.EMAIL, p.TELEFONO, p.ACTIVO "
                   + "FROM PROFESIONALES p "
                   + "INNER JOIN PROFESIONAL_ESPECIALIDAD pe ON p.ID = pe.PROFESIONAL_ID "
                   + "INNER JOIN ESPECIALIDADES e ON pe.ESPECIALIDAD_ID = e.ID "
                   + "WHERE UPPER(e.NOMBRE) = UPPER(?) AND p.ACTIVO = 'S'";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, especialidad);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId(rs.getInt("ID"));
                prof.setNombre(rs.getString("NOMBRE"));
                prof.setApellido(rs.getString("APELLIDO"));
                prof.setEmail(rs.getString("EMAIL"));
                prof.setTelefono(rs.getString("TELEFONO"));
                prof.setActivo("S".equals(rs.getString("ACTIVO")));
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
       String sql = "SELECT ID, NOMBRE, APELLIDO, EMAIL, TELEFONO, ACTIVO "
                   + "FROM PROFESIONALES WHERE ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Profesional prof = new Profesional();
                prof.setId(rs.getInt("ID"));
                prof.setNombre(rs.getString("NOMBRE"));
                prof.setApellido(rs.getString("APELLIDO"));
                prof.setEmail(rs.getString("EMAIL"));
                prof.setTelefono(rs.getString("TELEFONO"));
                prof.setActivo("S".equals(rs.getString("ACTIVO")));
                return prof;
            }

        } catch (Exception e) {
            System.out.println("ERROR al buscar profesional por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
}
    
