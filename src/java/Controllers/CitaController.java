/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.CitaDaoImpl;
import Interface.ICita;
import Model.Cita;
import Model.Paciente;
import Model.Profesional;
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
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Frocmen
 */
@WebServlet(name = "CitaController", urlPatterns = {"/CitaController"})
public class CitaController extends HttpServlet {
    
    private final ICita citaDao = new CitaDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        // Protección contra action == null
        if (action == null) {
            action = "listar";
        }

        try (PrintWriter out = response.getWriter()) {

            switch (action) {

                // ── REGISTRAR CITA ──────────────────────────────────────
                case "registrar":
                    int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
                    int idProfesional = Integer.parseInt(request.getParameter("idProfesional"));
                    String fechaStr = request.getParameter("fechaHora");

                    LocalDateTime fechaHora = LocalDateTime.parse(
                            fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    Paciente paciente = new Paciente();
                    paciente.setId(idPaciente);

                    Profesional profesional = new Profesional();
                    profesional.setId(idProfesional);

                    Cita cita = new Cita();
                    cita.setPaciente(paciente);
                    cita.setProfesional(profesional);
                    cita.setFechaHora(fechaHora);
                    cita.setEstado("CONFIRMADA");

                    // Recibir motivo si viene del formulario
                    String motivo = request.getParameter("motivo");
                    if (motivo != null) {
                        cita.setMotivo(motivo);
                    }

                    int idCitaCreada = citaDao.registrar(cita);
                    jsonResponse.addProperty("success", idCitaCreada > 0);
                    jsonResponse.addProperty("message", idCitaCreada > 0
                            ? "Cita registrada correctamente"
                            : "Error al registrar la cita");
                    jsonResponse.addProperty("idCita", idCitaCreada);
                    break;

                // ── CANCELAR CITA — RN-05 APLICADA ─────────────────────
                case "cancelar":
                   int idCita = Integer.parseInt(request.getParameter("idCita"));

                    //  RN-05: Verificar si faltan más de 8 horas para la cita
                    // puedeModificarOCancelar() consulta la BD y aplica la regla
                    if (!citaDao.puedeModificarOCancelar(idCita)) {
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", 
            "No es posible cancelar tu cita con menos de 8 horas de anticipación. " +
            "Comunícate con recepción.");
        break;
                    }

                    // Si pasó la validación, proceder con la cancelación
                    boolean cancelado = citaDao.cancelar(idCita);
                    jsonResponse.addProperty("success", idCita);
                    jsonResponse.addProperty("message", cancelado
                            ? "Cita cancelada correctamente"
                            : "No se pudo cancelar la cita. Verifique el estado actual.");
                    break;

                // ── REPROGRAMAR CITA — RN-05 APLICADA ──────────────────
                case "reprogramar":
                    int idReprogramar = Integer.parseInt(request.getParameter("idCita"));
                    String nuevaFechaStr = request.getParameter("nuevaFecha");

                    //  RN-05: Misma validación para modificación
                    if (!citaDao.puedeModificarOCancelar(idReprogramar)) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message",
                                "No es posible modificar tu cita con menos de 8 horas "
                                + "de anticipación. Por favor, comunícate directamente "
                                + "con la recepción de Dental Health.");
                        break;
                    }

                    LocalDateTime nuevaFecha = LocalDateTime.parse(
                            nuevaFechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    boolean reprogramado = citaDao.reprogramar(idReprogramar, nuevaFecha);
                    jsonResponse.addProperty("success", reprogramado);
                    jsonResponse.addProperty("message", reprogramado
                            ? "Cita reprogramada correctamente"
                            : "No se pudo reprogramar la cita.");
                    break;

                // ── MARCAR COMO ATENDIDA ────────────────────────────────
                case "atender":
                    int idAtender = Integer.parseInt(request.getParameter("idCita"));
                    boolean atendida = citaDao.marcarComoAtendida(idAtender);
                    jsonResponse.addProperty("success", atendida);
                    jsonResponse.addProperty("message", atendida
                            ? "Cita marcada como atendida"
                            : "No se pudo actualizar el estado de la cita.");
                    break;

                // ── LISTAR POR PACIENTE ─────────────────────────────────
                case "listarPorPaciente":
                    int idPac = Integer.parseInt(request.getParameter("idPaciente"));
                    jsonResponse.add("data", gson.toJsonTree(citaDao.listarPorPaciente(idPac)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── LISTAR POR PROFESIONAL ──────────────────────────────
                case "listarPorProfesional":
                    int idProf = Integer.parseInt(request.getParameter("idProfesional"));
                    jsonResponse.add("data", gson.toJsonTree(citaDao.listarPorProfesional(idProf)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── LISTAR TODAS ────────────────────────────────────────
                case "listarTodas":
                    jsonResponse.add("data", gson.toJsonTree(citaDao.listarTodas()));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── CONSULTAR DISPONIBILIDAD — RF-02 ───────────────────
                case "disponibilidad":
                    String especialidad = request.getParameter("especialidad");
                    String fechaDisponibilidad = request.getParameter("fecha");

                    LocalDateTime fechaBusqueda = LocalDateTime.parse(
                            fechaDisponibilidad, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    jsonResponse.add("data", gson.toJsonTree(
                            citaDao.consultarDisponibilidad(especialidad, fechaBusqueda)));
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
