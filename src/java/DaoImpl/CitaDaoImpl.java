/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.ICita;
import Model.Cita;
import Model.Paciente;
import Model.Profesional;
import Util.ConexionSingleton;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class CitaDaoImpl implements ICita{

    @Override
    public int registrar(Cita cita) {
        String sql = "INSERT INTO cita(id_paciente, id_profesional, fecha_hora, estado) VALUES(?,?,?,?)";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, cita.getPaciente().getId_persona());
            ps.setInt(2, cita.getProfesional().getId_persona());
            ps.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(4, cita.getEstado());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean cancelar(int idCita) {
       String sql = "UPDATE cita SET estado = 'CANCELADA' WHERE id_cita = ? AND estado = 'CONFIRMADA'";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean marcarComoAtendida(int idCita) {
       String sql = "UPDATE cita SET estado = 'ATENDIDA' WHERE id_cita = ?";
        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reprogramar(int idCita, LocalDateTime nuevaFecha) {
        return false; // Implementar
    }

    @Override
    public Cita buscarPorId(int idCita) {
       return null; // Implementar
    }

    @Override
    public List<Cita> listarPorPaciente(int idPaciente) {
       List<Cita> lista = new ArrayList<>();
        // Implementar JOIN con paciente y profesional
        return lista;
    }

    @Override
    public List<Cita> listarPorProfesional(int idProfesional) {
        List<Cita> lista = new ArrayList<>();
        // Implementar consulta
        return lista;
    }

    @Override
    public List<Cita> listarTodas() {
        return new ArrayList<>();
    }

    @Override
    public List<Cita> consultarDisponibilidad(String especialidad, LocalDateTime fecha) {
       // Implementación pendiente según tu tabla
        return new ArrayList<>(); 
    }

    @Override
    public boolean puedeModificarOCancelar(int idCita) {
        // Consultar y validar RN-05
        return true;
    }
    
}
