/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DaoImpl;

import Interface.IEspecialidad;
import Model.Especialidad;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frocmen
 */
public class EspecialidadDaoImpl implements IEspecialidad{
    
     private Connection cn;

    @Override
    public List<Especialidad> listarEspecialidades() {
      List<Especialidad> lista = null;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            lista = new ArrayList<>();
            cn = ConexionSingleton.getConnection();

            
            query = " SELECT DISTINCT e.ID, e.NOMBRE, e.DURACION_MINUTOS, e.ACTIVO "
                  + " FROM ESPECIALIDADES e "
                  + " INNER JOIN PROFESIONAL_ESPECIALIDAD pe ON e.ID = pe.ESPECIALIDAD_ID "
                  + " INNER JOIN PROFESIONALES p ON pe.PROFESIONAL_ID = p.ID "
                  + " WHERE e.ACTIVO = 'S' "
                  + " AND p.ACTIVO = 'S' "
                  + " ORDER BY e.NOMBRE ";

            st = cn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Especialidad esp = new Especialidad();
                esp.setId(rs.getInt("ID"));
                esp.setNombre(rs.getString("NOMBRE"));
                esp.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));
                esp.setActivo("S".equals(rs.getString("ACTIVO")));
                lista.add(esp);
            }

            System.out.println(" Especialidades cargadas: " + lista.size());

        } catch (Exception e) {
            System.out.println(" ERROR AL LISTAR ESPECIALIDADES: " + e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println(" No se pudo listar las especialidades");
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
    public boolean existeEspecialidad(String nombre) {
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            cn = ConexionSingleton.getConnection();

          
            query = " SELECT COUNT(*) AS TOTAL "
                  + " FROM ESPECIALIDADES "
                  + " WHERE UPPER(NOMBRE) = UPPER(?) "
                  + " AND ACTIVO = 'S' ";

            st = cn.prepareStatement(query);
            st.setString(1, nombre);
            rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt("TOTAL") > 0;
            }

        } catch (Exception e) {
            System.out.println(" ERROR AL VERIFICAR ESPECIALIDAD: " + e.getMessage());
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
    
}