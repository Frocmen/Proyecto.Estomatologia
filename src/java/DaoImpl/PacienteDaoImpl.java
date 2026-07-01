/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IPaciente;
import Model.Paciente;
import Util.ConexionSingleton;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class PacienteDaoImpl implements IPaciente {

   // ── SHA-256 (igual que UsuarioDaoImpl) ───────────────────
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
            throw new RuntimeException("Error al hashear clave", e);
        }
    }

    // ── REGISTRAR PACIENTE → tabla PACIENTES Oracle ──────────
    @Override
    public int registrarPaciente(Paciente paciente) {
        String sql = "INSERT INTO PACIENTES(ID, NOMBRE, APELLIDO, DNI, EMAIL, "
                   + "PASSWORD_HASH, TELEFONO, VERIFICADO, FECHA_REGISTRO, ACTIVO) "
                   + "VALUES(SEQ_PACIENTES.NEXTVAL, ?, ?, ?, ?, ?, ?, 'N', SYSDATE, 'S')";

        String[] cols = {"ID"};

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, cols)) {

            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getEmail());

            // Hashear password solo si viene en texto plano
            String pwdHash = paciente.getPasswordHash();
            if (pwdHash != null && pwdHash.length() < 60) {
                // Es texto plano, hashear
                pwdHash = hashClave(pwdHash);
            }
            ps.setString(5, pwdHash != null ? pwdHash : hashClave("default123"));
            ps.setString(6, paciente.getTelefono());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    paciente.setId(idGenerado);
                    System.out.println("✓ Paciente registrado con ID: " + idGenerado);
                    return idGenerado;
                }
            }

        } catch (Exception e) {
    System.out.println("ERROR registrarPaciente: " + e.getMessage());
    e.printStackTrace();
}
return 0;
    }

    // ── LISTAR PACIENTES ACTIVOS ─────────────────────────────
    @Override
    public List<Paciente> listarPacientes() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, EMAIL, TELEFONO, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE ACTIVO = 'S' "
                   + "ORDER BY NOMBRE";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paciente p = mapear(rs);
                lista.add(p);
            }

        } catch (Exception e) {
            System.out.println("ERROR listarPacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // ── EDITAR PACIENTE ──────────────────────────────────────
    @Override
    public boolean editar(Paciente paciente) {
        String sql = "UPDATE PACIENTES SET NOMBRE=?, APELLIDO=?, DNI=?, "
                   + "TELEFONO=?, EMAIL=? WHERE ID=?";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getDni());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getEmail());
            ps.setInt(6, paciente.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR editar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── BAJA LÓGICA ──────────────────────────────────────────
    @Override
    public boolean delete(int id) {
        String sql = "UPDATE PACIENTES SET ACTIVO = 'N' WHERE ID = ?";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR delete paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── BUSCAR POR DNI ───────────────────────────────────────
    @Override
    public Paciente buscarPacientePorDni(String dni) {
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, EMAIL, TELEFONO, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE DNI = ?";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);

        } catch (Exception e) {
            System.out.println("ERROR buscarPorDni: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── BUSCAR POR ID ────────────────────────────────────────
    @Override
    public Paciente buscarPorId(int id) {
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, EMAIL, TELEFONO, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE ID = ?";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);

        } catch (Exception e) {
            System.out.println("ERROR buscarPorId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── BUSCAR POR EMAIL (para validar duplicados en registro) 
    public Paciente buscarPorEmail(String email) {
        String sql = "SELECT ID, NOMBRE, APELLIDO, DNI, EMAIL, TELEFONO, "
                   + "VERIFICADO, ACTIVO FROM PACIENTES WHERE EMAIL = ?";

        try (Connection cn = new ConexionSingleton().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);

        } catch (Exception e) {
            System.out.println("ERROR buscarPorEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── MAPEAR ResultSet → Paciente ──────────────────────────
    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("ID"));
        p.setNombre(rs.getString("NOMBRE"));
        p.setApellido(rs.getString("APELLIDO"));
        p.setDni(rs.getString("DNI"));
        p.setEmail(rs.getString("EMAIL"));
        p.setTelefono(rs.getString("TELEFONO"));
        p.setVerificado("S".equals(rs.getString("VERIFICADO")));
        p.setActivo("S".equals(rs.getString("ACTIVO")));
        return p;
    }
}