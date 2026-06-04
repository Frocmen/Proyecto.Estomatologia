/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IHistorial;
import Model.Historial;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class HistorialDaoImpl implements IHistorial{

private Connection  cn;

    @Override
    public boolean registrarAtencion(Historial historial) {

         PreparedStatement st;
        String query;
        String[] cols = {"ID"};

        try {
            cn = ConexionSingleton.getConnection();

            query = " INSERT INTO HISTORIAL_ATENCIONES( "
                  + "     ID, CITA_ID, FECHA_HORA_INICIO_REAL, FECHA_HORA_FIN_REAL, "
                  + "     PIEZA_DENTAL, TRATAMIENTO_REALIZADO, OBSERVACIONES, "
                  + "     PROFESIONAL_ATENDIO_ID, FECHA_REGISTRO "
                  + " ) VALUES ( "
                  + "     SEQ_HISTORIAL.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, SYSDATE "
                  + " ) ";

            st = cn.prepareStatement(query, cols);

            st.setInt(1, historial.getIdCita());

            // ORACLE: LocalDateTime → Timestamp
            st.setTimestamp(2, historial.getFechaHoraInicioReal() != null
                    ? Timestamp.valueOf(historial.getFechaHoraInicioReal())
                    : new Timestamp(System.currentTimeMillis()));

            st.setTimestamp(3, historial.getFechaHoraFinReal() != null
                    ? Timestamp.valueOf(historial.getFechaHoraFinReal())
                    : new Timestamp(System.currentTimeMillis()));

            st.setString(4, historial.getPiezaDental());
            st.setString(5, historial.getTratamientoRealizado());
            st.setString(6, historial.getObservaciones());
            st.setInt(7, historial.getIdProfesionalAtendio());

            int r = st.executeUpdate();

            if (r > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    historial.setId(rs.getInt(1));
                    System.out.println(" HISTORIAL registrado con ID: " + historial.getId());
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL REGISTRAR HISTORIAL: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println(" No se pudo registrar la atención en el historial");
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
    public List<Historial> listarPorCita(int idCita) {
          List<Historial> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            query = " SELECT ID, CITA_ID, FECHA_HORA_INICIO_REAL, FECHA_HORA_FIN_REAL, "
                  + "        PIEZA_DENTAL, TRATAMIENTO_REALIZADO, OBSERVACIONES, "
                  + "        PROFESIONAL_ATENDIO_ID, FECHA_REGISTRO "
                  + " FROM HISTORIAL_ATENCIONES "
                  + " WHERE CITA_ID = ? "
                  + " ORDER BY FECHA_REGISTRO DESC ";

            st = cn.prepareStatement(query);
            st.setInt(1, idCita);
            rs = st.executeQuery();

            while (rs.next()) {
                Historial h = new Historial();
                h.setId(rs.getInt("ID"));
                h.setIdCita(rs.getInt("CITA_ID"));

                // ORACLE: Timestamp → LocalDateTime
                Timestamp tsInicio = rs.getTimestamp("FECHA_HORA_INICIO_REAL");
                if (tsInicio != null) {
                    h.setFechaHoraInicioReal(tsInicio.toLocalDateTime());
                }

                Timestamp tsFin = rs.getTimestamp("FECHA_HORA_FIN_REAL");
                if (tsFin != null) {
                    h.setFechaHoraFinReal(tsFin.toLocalDateTime());
                }

                h.setPiezaDental(rs.getString("PIEZA_DENTAL"));
                h.setTratamientoRealizado(rs.getString("TRATAMIENTO_REALIZADO"));
                h.setObservaciones(rs.getString("OBSERVACIONES"));
                h.setIdProfesionalAtendio(rs.getInt("PROFESIONAL_ATENDIO_ID"));

                Timestamp tsRegistro = rs.getTimestamp("FECHA_REGISTRO");
                if (tsRegistro != null) {
                    h.setFechaRegistro(tsRegistro.toLocalDateTime());
                }

                lista.add(h);
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR HISTORIAL POR CITA: " + e.getMessage());
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
    public List<Historial> listarPorProfesional(int idProfesional) {
    List<Historial> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            query = " SELECT ID, CITA_ID, FECHA_HORA_INICIO_REAL, FECHA_HORA_FIN_REAL, "
                  + "        PIEZA_DENTAL, TRATAMIENTO_REALIZADO, OBSERVACIONES, "
                  + "        PROFESIONAL_ATENDIO_ID, FECHA_REGISTRO "
                  + " FROM HISTORIAL_ATENCIONES "
                  + " WHERE PROFESIONAL_ATENDIO_ID = ? "
                  + " ORDER BY FECHA_REGISTRO DESC ";

            st = cn.prepareStatement(query);
            st.setInt(1, idProfesional);
            rs = st.executeQuery();

            while (rs.next()) {
                Historial h = new Historial();
                h.setId(rs.getInt("ID"));
                h.setIdCita(rs.getInt("CITA_ID"));

                Timestamp tsInicio = rs.getTimestamp("FECHA_HORA_INICIO_REAL");
                if (tsInicio != null) {
                    h.setFechaHoraInicioReal(tsInicio.toLocalDateTime());
                }

                Timestamp tsFin = rs.getTimestamp("FECHA_HORA_FIN_REAL");
                if (tsFin != null) {
                    h.setFechaHoraFinReal(tsFin.toLocalDateTime());
                }

                h.setPiezaDental(rs.getString("PIEZA_DENTAL"));
                h.setTratamientoRealizado(rs.getString("TRATAMIENTO_REALIZADO"));
                h.setObservaciones(rs.getString("OBSERVACIONES"));
                h.setIdProfesionalAtendio(rs.getInt("PROFESIONAL_ATENDIO_ID"));

                Timestamp tsRegistro = rs.getTimestamp("FECHA_REGISTRO");
                if (tsRegistro != null) {
                    h.setFechaRegistro(tsRegistro.toLocalDateTime());
                }

                lista.add(h);
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR HISTORIAL POR PROFESIONAL: " + e.getMessage());
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
}
