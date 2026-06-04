/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.ICita;
import Model.Cita;
import Model.Especialidad;
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
       String sql = "INSERT INTO CITAS(ID, PACIENTE_ID, PROFESIONAL_ID, ESPECIALIDAD_ID, "
                   + "FECHA_HORA_INICIO, FECHA_HORA_FIN, ESTADO, ORIGEN, FECHA_CREACION) "
                   + "VALUES(SEQ_CITAS.NEXTVAL, ?, ?, ?, ?, ?, ?, 'PACIENTE', SYSDATE)";
        String[] cols = {"ID"};

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, cols)) {

            ps.setInt(1, cita.getPaciente().getId());
            ps.setInt(2, cita.getProfesional().getId());
            // Si especialidadPrincipalId es null, usar null en BD
            if (cita.getProfesional().getEspecialidadPrincipalId() != null) {
                ps.setInt(3, cita.getProfesional().getEspecialidadPrincipalId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, Timestamp.valueOf(cita.getFechaHora()));
            // FECHA_HORA_FIN se calcula según duración de especialidad (por ahora null)
            ps.setNull(5, Types.TIMESTAMP);
            ps.setString(6, cita.getEstado());

            int affected = ps.executeUpdate();

            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idCita = rs.getInt(1);
                    cita.setId_cita(idCita);
                    System.out.println("Cita registrada con ID: " + idCita);
                    return idCita;
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR al registrar cita: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean cancelar(int idCita) {
       String sql = "UPDATE CITAS SET ESTADO = 'CANCELADA' "
                   + "WHERE ID = ? AND ESTADO = 'CONFIRMADA'";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR al cancelar cita: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean marcarComoAtendida(int idCita) {
      String sql = "UPDATE CITAS SET ESTADO = 'ATENDIDA' WHERE ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR al marcar cita como atendida: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reprogramar(int idCita, LocalDateTime nuevaFecha) {
      String sql = "UPDATE CITAS SET FECHA_HORA_INICIO = ?, ESTADO = 'CONFIRMADA' "
                   + "WHERE ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(nuevaFecha));
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("ERROR al reprogramar cita: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Cita buscarPorId(int idCita) {
      String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
                   + "pa.ID AS PAC_ID, pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE, "
                   + "pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
                   + "FROM CITAS c "
                   + "INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
                   + "INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
                   + "WHERE c.ID = ?";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("PAC_ID"));
                paciente.setNombre(rs.getString("PAC_NOM"));
                paciente.setApellido(rs.getString("PAC_APE"));

                Profesional profesional = new Profesional();
                profesional.setId(rs.getInt("PROF_ID"));
                profesional.setNombre(rs.getString("PROF_NOM"));
                profesional.setApellido(rs.getString("PROF_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setPaciente(paciente);
                cita.setProfesional(profesional);
                cita.setFechaHora(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("NOTAS"));
                return cita;
            }

        } catch (Exception e) {
            System.out.println("ERROR al buscar cita por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cita> listarPorPaciente(int idPaciente) {
       List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
                   + "pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
                   + "FROM CITAS c "
                   + "INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
                   + "WHERE c.PACIENTE_ID = ? ORDER BY c.FECHA_HORA_INICIO DESC";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Profesional profesional = new Profesional();
                profesional.setId(rs.getInt("PROF_ID"));
                profesional.setNombre(rs.getString("PROF_NOM"));
                profesional.setApellido(rs.getString("PROF_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setProfesional(profesional);
                cita.setFechaHora(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("NOTAS"));
                lista.add(cita);
            }

        } catch (Exception e) {
            System.out.println("ERROR al listar citas por paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> listarPorProfesional(int idProfesional) {
         List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
                   + "pa.ID AS PAC_ID, pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE "
                   + "FROM CITAS c "
                   + "INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
                   + "WHERE c.PROFESIONAL_ID = ? ORDER BY c.FECHA_HORA_INICIO DESC";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idProfesional);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("PAC_ID"));
                paciente.setNombre(rs.getString("PAC_NOM"));
                paciente.setApellido(rs.getString("PAC_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setPaciente(paciente);
                cita.setFechaHora(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("NOTAS"));
                lista.add(cita);
            }

        } catch (Exception e) {
            System.out.println("ERROR al listar citas por profesional: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> listarTodas() {
         List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, "
                   + "pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE, "
                   + "pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
                   + "FROM CITAS c "
                   + "INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
                   + "INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
                   + "ORDER BY c.FECHA_HORA_INICIO DESC";

        try (Connection cn = ConexionSingleton.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setNombre(rs.getString("PAC_NOM"));
                paciente.setApellido(rs.getString("PAC_APE"));

                Profesional profesional = new Profesional();
                profesional.setNombre(rs.getString("PROF_NOM"));
                profesional.setApellido(rs.getString("PROF_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setPaciente(paciente);
                cita.setProfesional(profesional);
                cita.setFechaHora(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
                cita.setEstado(rs.getString("ESTADO"));
                lista.add(cita);
            }

        } catch (Exception e) {
            System.out.println("ERROR al listar todas las citas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> consultarDisponibilidad(String especialidad, LocalDateTime fecha) {
       List<Cita> lista = null;
    PreparedStatement st;
    ResultSet rs;
    String query;

    try {
        lista = new ArrayList<>();
        Connection cn = ConexionSingleton.getConnection();

        // ORACLE: TRUNC() para comparar solo la parte de la fecha (sin hora)
        // Trae las citas ya CONFIRMADAS para esa especialidad y fecha
        // con los datos del profesional y la duración de la especialidad
        query = " SELECT c.ID, c.FECHA_HORA_INICIO, c.FECHA_HORA_FIN, c.ESTADO, "
              + "        pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE, "
              + "        e.ID AS ESP_ID, e.NOMBRE AS ESP_NOM, e.DURACION_MINUTOS "
              + " FROM CITAS c "
              + " INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
              + " INNER JOIN ESPECIALIDADES e ON c.ESPECIALIDAD_ID = e.ID "
              + " WHERE UPPER(e.NOMBRE) = UPPER(?) "
              + " AND TRUNC(c.FECHA_HORA_INICIO) = TRUNC(?) "
              + " AND c.ESTADO IN ('CONFIRMADA', 'REPROGRAMADA') "
              + " AND pr.ACTIVO = 'S' "
              + " ORDER BY c.FECHA_HORA_INICIO ";

        st = cn.prepareStatement(query);
        st.setString(1, especialidad);
        // ORACLE: Convertir LocalDateTime a java.sql.Timestamp
        st.setTimestamp(2, Timestamp.valueOf(fecha));
        rs = st.executeQuery();

        while (rs.next()) {
            // Armar objeto Profesional con datos del JOIN
            Profesional profesional = new Profesional();
            profesional.setId(rs.getInt("PROF_ID"));
            profesional.setNombre(rs.getString("PROF_NOM"));
            profesional.setApellido(rs.getString("PROF_APE"));

            // Armar objeto Especialidad con duración (RN-D05)
            Especialidad esp = new Especialidad();
            esp.setId(rs.getInt("ESP_ID"));
            esp.setNombre(rs.getString("ESP_NOM"));
            esp.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));

            // Armar objeto Cita
            Cita cita = new Cita();
            cita.setId_cita(rs.getInt("ID"));
            cita.setProfesional(profesional);
            cita.setFechaHora(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
            cita.setEstado(rs.getString("ESTADO"));

            lista.add(cita);
        }

        System.out.println(" Disponibilidad cargada: " + lista.size() + " citas para " + especialidad);

    } catch (Exception e) {
        System.out.println(" ERROR AL CONSULTAR DISPONIBILIDAD: " + e.getMessage());
        e.printStackTrace();
        System.out.println(" No se pudo consultar la disponibilidad");
    }
    return lista;
    }

    @Override
    public boolean puedeModificarOCancelar(int idCita) {
      // Consulta la fecha y aplica RN-05 (más de 8 horas)
        Cita cita = buscarPorId(idCita);
        if (cita != null) {
            return cita.puedeCancelar();
        }
        return false;
    }
    
}
