/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.CitaDaoImpl;
import DaoImpl.HistorialDaoImpl;
import Interface.ICita;
import Interface.IHistorial;
import Model.Historial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 *
 * @author Frocmen
 */
@WebServlet(name = "HistorialController", urlPatterns = {"/HistorialController"})
public class HistorialController extends HttpServlet {

    // LLAMADA GLOBAL
    private final IHistorial historialDao = new HistorialDaoImpl();
    private final ICita citaDao = new CitaDaoImpl();
    private final Gson gson = Util.GsonProvider.getGson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        // Protección contra action == null
        if (action == null) {
            action = "";
        }

        try (PrintWriter out = response.getWriter()) {

            switch (action) {

                // ── REGISTRAR ATENCIÓN — RF-08, RN-03 ──────────────────
                // El odontólogo marca la cita como realizada y registra
                // la atención en el historial clínico del paciente
                case "registrar":
                    int idCita = Integer.parseInt(request.getParameter("idCita"));
                    int idProfesional = Integer.parseInt(
                            request.getParameter("idProfesional"));
                    String piezaDental = request.getParameter("piezaDental");
                    String tratamiento = request.getParameter("tratamiento");
                    String observaciones = request.getParameter("observaciones");

                    // RN-03: Primero registrar en historial
                    Historial h = new Historial();
                    h.setIdCita(idCita);
                    h.setIdProfesionalAtendio(idProfesional);
                    h.setPiezaDental(piezaDental);
                    h.setTratamientoRealizado(tratamiento);
                    h.setObservaciones(observaciones);
                    h.setFechaHoraInicioReal(LocalDateTime.now());
                    h.setFechaHoraFinReal(LocalDateTime.now());

                    boolean registrado = historialDao.registrarAtencion(h);

                    // RN-03: Solo marcar como ATENDIDA si el historial se guardó
                    if (registrado) {
                        citaDao.marcarComoAtendida(idCita);
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("message",
                                "Atención registrada en historial correctamente");
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message",
                                "Error al registrar la atención");
                    }
                    break;

                // ── LISTAR POR CITA ─────────────────────────────────────
                case "listarPorCita":
                    int idC = Integer.parseInt(request.getParameter("idCita"));
                    jsonResponse.add("data",
                            gson.toJsonTree(historialDao.listarPorCita(idC)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── LISTAR POR PROFESIONAL ──────────────────────────────
                case "listarPorProfesional":
                    int idP = Integer.parseInt(request.getParameter("idProfesional"));
                    jsonResponse.add("data",
                            gson.toJsonTree(historialDao.listarPorProfesional(idP)));
                    jsonResponse.addProperty("success", true);
                    break;

                default:
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Acción no válida");
            }

            out.print(jsonResponse.toString());

        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
