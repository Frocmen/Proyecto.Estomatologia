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
    
      private Connection cn;
 
    @Override
    public int registrar(Cita cita) {
         PreparedStatement st;
        ResultSet rs;
        String query;
        String[] cols = {"ID"};

        try {
            cn = ConexionSingleton.getConnection();

            query = " INSERT INTO CITAS(ID, PACIENTE_ID, PROFESIONAL_ID, ESPECIALIDAD_ID, "
                  + " FECHA_HORA_INICIO, FECHA_HORA_FIN, ESTADO, ORIGEN, FECHA_CREACION) "
                  + " VALUES(SEQ_CITAS.NEXTVAL, ?, ?, ?, ?, ?, ?, 'PACIENTE', SYSDATE) ";

            st = cn.prepareStatement(query, cols);
            st.setInt(1, cita.getPaciente().getId());
            st.setInt(2, cita.getProfesional().getId());

            
            if (cita.getEspecialidadId() > 0) {
                st.setInt(3, cita.getEspecialidadId());
            } else {
                st.setNull(3, Types.INTEGER);
            }

            st.setTimestamp(4, Timestamp.valueOf(cita.getFechaHora()));
            st.setNull(5, Types.TIMESTAMP); 
            st.setString(6, cita.getEstado());

            int r = st.executeUpdate();

            if (r > 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int idCita = rs.getInt(1);
                    cita.setId_cita(idCita);
                    System.out.println(" CITA registrada con ID: " + idCita);
                    return idCita;
                }
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL REGISTRAR CITA: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println(" No se logró registrar la cita");
        } finally {
            if (cn != null) {
                try {
                } catch (Exception e) {
                }
            }
        }
        return 0;
    }

   
    @Override
    public boolean cancelar(int idCita) {
       PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " UPDATE CITAS SET ESTADO = 'CANCELADA' "
                  + " WHERE ID = ? AND ESTADO = 'CONFIRMADA' ";

            st = cn.prepareStatement(query);
            st.setInt(1, idCita);
            int r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" CITA cancelada ID: " + idCita);
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL CANCELAR CITA: " + e.getMessage());
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
    public boolean marcarComoAtendida(int idCita) {
      PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " UPDATE CITAS SET ESTADO = 'ATENDIDA' WHERE ID = ? ";

            st = cn.prepareStatement(query);
            st.setInt(1, idCita);
            int r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" CITA marcada como ATENDIDA ID: " + idCita);
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL MARCAR CITA COMO ATENDIDA: " + e.getMessage());
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
    public boolean reprogramar(int idCita, LocalDateTime nuevaFecha) {
         PreparedStatement st;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

            query = " UPDATE CITAS SET FECHA_HORA_INICIO = ?, ESTADO = 'CONFIRMADA' "
                  + " WHERE ID = ? ";

            st = cn.prepareStatement(query);
            st.setTimestamp(1, Timestamp.valueOf(nuevaFecha));
            st.setInt(2, idCita);
            int r = st.executeUpdate();

            if (r > 0) {
                System.out.println(" CITA reprogramada ID: " + idCita);
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL REPROGRAMAR CITA: " + e.getMessage());
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
    public Cita buscarPorId(int idCita) {
       String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
               + "pa.ID AS PAC_ID, pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE, "
               + "pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
               + "FROM CITAS c "
               + "INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
               + "INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
               + "WHERE c.ID = ?";

    try (Connection cn = new ConexionSingleton().getConnection();
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
            // ← NULL PROTEGIDO
            Timestamp ts = rs.getTimestamp("FECHA_HORA_INICIO");
            if (ts != null) cita.setFechaHora(ts.toLocalDateTime());
            cita.setEstado(rs.getString("ESTADO"));
            cita.setMotivo(rs.getString("NOTAS"));
            return cita;
        }

    } catch (Exception e) {
        System.out.println("ERROR buscarPorId: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
    }

     
    @Override
    public List<Cita> listarPorPaciente(int idPaciente) {
        List<Cita> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            query = " SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
                  + "        pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
                  + " FROM CITAS c "
                  + " INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
                  + " WHERE c.PACIENTE_ID = ? "
                  + " ORDER BY c.FECHA_HORA_INICIO DESC ";

            st = cn.prepareStatement(query);
            st.setInt(1, idPaciente);
            rs = st.executeQuery();

            while (rs.next()) {
                Profesional profesional = new Profesional();
                profesional.setId(rs.getInt("PROF_ID"));
                profesional.setNombre(rs.getString("PROF_NOM"));
                profesional.setApellido(rs.getString("PROF_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setProfesional(profesional);

                Timestamp ts = rs.getTimestamp("FECHA_HORA_INICIO");
                if (ts != null) cita.setFechaHora(ts.toLocalDateTime());

                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("NOTAS"));
                lista.add(cita);
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR CITAS POR PACIENTE: " + e.getMessage());
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

     
    @Override
    public List<Cita> listarPorProfesional(int idProfesional) {
       List<Cita> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            query = " SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
                  + "        pa.ID AS PAC_ID, pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE "
                  + " FROM CITAS c "
                  + " INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
                  + " WHERE c.PROFESIONAL_ID = ? "
                  + " ORDER BY c.FECHA_HORA_INICIO DESC ";

            st = cn.prepareStatement(query);
            st.setInt(1, idProfesional);
            rs = st.executeQuery();

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("PAC_ID"));
                paciente.setNombre(rs.getString("PAC_NOM"));
                paciente.setApellido(rs.getString("PAC_APE"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setPaciente(paciente);

                Timestamp ts = rs.getTimestamp("FECHA_HORA_INICIO");
                if (ts != null) cita.setFechaHora(ts.toLocalDateTime());

                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("NOTAS"));
                lista.add(cita);
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR CITAS POR PROFESIONAL: " + e.getMessage());
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

     
    @Override
    public List<Cita> listarTodas() {
      List<Cita> lista = new ArrayList<>();
    String sql = "SELECT c.ID, c.FECHA_HORA_INICIO, c.ESTADO, c.NOTAS, "
               + "pa.ID AS PAC_ID, pa.NOMBRE AS PAC_NOM, pa.APELLIDO AS PAC_APE, "
               + "pr.ID AS PROF_ID, pr.NOMBRE AS PROF_NOM, pr.APELLIDO AS PROF_APE "
               + "FROM CITAS c "
               + "INNER JOIN PACIENTES pa ON c.PACIENTE_ID = pa.ID "
               + "INNER JOIN PROFESIONALES pr ON c.PROFESIONAL_ID = pr.ID "
               + "ORDER BY c.FECHA_HORA_INICIO DESC";

    try (Connection cn = new ConexionSingleton().getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
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
            Timestamp ts = rs.getTimestamp("FECHA_HORA_INICIO");
            if (ts != null) cita.setFechaHora(ts.toLocalDateTime());
            cita.setEstado(rs.getString("ESTADO"));
            cita.setMotivo(rs.getString("NOTAS"));
            lista.add(cita);
        }

    } catch (Exception e) {
        System.out.println("ERROR listarTodas: " + e.getMessage());
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
            cn = ConexionSingleton.getConnection();

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
            st.setTimestamp(2, Timestamp.valueOf(fecha));
            rs = st.executeQuery();

            while (rs.next()) {
                Profesional profesional = new Profesional();
                profesional.setId(rs.getInt("PROF_ID"));
                profesional.setNombre(rs.getString("PROF_NOM"));
                profesional.setApellido(rs.getString("PROF_APE"));

                Especialidad esp = new Especialidad();
                esp.setId(rs.getInt("ESP_ID"));
                esp.setNombre(rs.getString("ESP_NOM"));
                esp.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));

                Cita cita = new Cita();
                cita.setId_cita(rs.getInt("ID"));
                cita.setProfesional(profesional);

                Timestamp ts = rs.getTimestamp("FECHA_HORA_INICIO");
                if (ts != null) cita.setFechaHora(ts.toLocalDateTime());

                cita.setEstado(rs.getString("ESTADO"));
                lista.add(cita);
            }

            System.out.println(" Disponibilidad: " + lista.size() + " citas para " + especialidad);

        } catch (Exception e) {
            System.out.println(" ERROR AL CONSULTAR DISPONIBILIDAD: " + e.getMessage());
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

     
    @Override
    public boolean puedeModificarOCancelar(int idCita) {
      Cita cita = buscarPorId(idCita);
        if (cita != null && cita.getFechaHora() != null) {
            return cita.puedeCancelar(); 
        }
        System.out.println(" |AVISO| No se pudo verificar RN-05 para cita ID: " + idCita);
        return false;
    }
    
}