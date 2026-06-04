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

  
    @Override
    public int registrarPaciente(Paciente paciente) {
        // ORACLE: INSERT con SEQUENCE + RETURNING INTO para recuperar el ID
        String sql = "INSERT INTO PACIENTES(ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL, "
                   + "VERIFICADO, FECHA_REGISTRO, ACTIVO) "
                   + "VALUES(SEQ_PACIENTES.NEXTVAL, ?, ?, ?, ?, ?, 'N', SYSDATE, 'S')";

        String[] cols = {"ID"}; // ← ORACLE: columna a recuperar

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, cols)) {

            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getEmail());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    paciente.setId(idGenerado);
                    System.out.println("Paciente registrado con ID: " + idGenerado);
                    return idGenerado;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR al registrar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
//falta consultar
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
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE ACTIVO = 'S'";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                p.setVerificado("S".equals(rs.getString("VERIFICADO")));
                p.setActivo("S".equals(rs.getString("ACTIVO")));
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
       String sql = "UPDATE PACIENTES SET NOMBRE=?, APELLIDO=?, DNI=?, "
                   + "TELEFONO=?, EMAIL=? WHERE ID=?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getEmail());
            ps.setInt(6, paciente.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR al editar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
      // ORACLE: Baja lógica — nunca eliminar físicamente
        String sql = "UPDATE PACIENTES SET ACTIVO = 'N' WHERE ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR al desactivar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Paciente buscarPacientePorDni(String dni) {
       String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE DNI = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                p.setVerificado("S".equals(rs.getString("VERIFICADO")));
                p.setActivo("S".equals(rs.getString("ACTIVO")));
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
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, TELEFONO, EMAIL, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("ID"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setApellido(rs.getString("APELLIDO"));
                p.setDni(rs.getString("DNI"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("EMAIL"));
                p.setVerificado("S".equals(rs.getString("VERIFICADO")));
                p.setActivo("S".equals(rs.getString("ACTIVO")));
                return p;
            }

        } catch (Exception e) {
            System.out.println("ERROR al buscar paciente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
