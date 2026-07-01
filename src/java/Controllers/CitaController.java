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
    private final Gson gson = Util.GsonProvider.getGson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();
        if (action == null) action = "listarTodas";

        try (PrintWriter out = response.getWriter()) {

            switch (action) {

                // ── REGISTRAR CITA ───────────────────────────────────
                case "registrar":
                    int pacienteId    = Integer.parseInt(request.getParameter("pacienteId"));
                    int profesionalId = Integer.parseInt(request.getParameter("profesionalId"));
                    int especialidadId = Integer.parseInt(
                            request.getParameter("especialidadId") != null
                            ? request.getParameter("especialidadId") : "1");
                    String fechaStr   = request.getParameter("fechaHora");
                    String motivo     = request.getParameter("motivo");

                    LocalDateTime fechaHora = LocalDateTime.parse(fechaStr,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    Paciente pac = new Paciente();
                    pac.setId(pacienteId);

                    Profesional prof = new Profesional();
                    prof.setId(profesionalId);

                    Cita cita = new Cita();
                    cita.setPaciente(pac);
                    cita.setProfesional(prof);
                    cita.setEspecialidadId(especialidadId);
                    cita.setFechaHora(fechaHora);
                    cita.setEstado("CONFIRMADA");
                    cita.setMotivo(motivo != null ? motivo : "");

                    int idCita = citaDao.registrar(cita);
                    jsonResponse.addProperty("success", idCita > 0);
                    jsonResponse.addProperty("message", idCita > 0
                            ? "Cita registrada correctamente"
                            : "Error al registrar la cita");
                    if (idCita > 0) jsonResponse.addProperty("id", idCita);
                    break;

                // ── CANCELAR CITA — RN-05 ────────────────────────────
                case "cancelar":
                    int idCancelar = Integer.parseInt(request.getParameter("idCita"));

                    boolean puedeCancelar = citaDao.puedeModificarOCancelar(idCancelar);
                    if (!puedeCancelar) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message",
                                "No se puede cancelar: faltan menos de 8 horas (RN-05)");
                        break;
                    }
                    boolean cancelado = citaDao.cancelar(idCancelar);
                    jsonResponse.addProperty("success", cancelado);
                    jsonResponse.addProperty("message", cancelado
                            ? "Cita cancelada correctamente"
                            : "Error al cancelar la cita");
                    break;

                // ── REPROGRAMAR CITA — RN-05 ─────────────────────────
                case "reprogramar":
                    int idReprog = Integer.parseInt(request.getParameter("idCita"));
                    String nuevaFechaStr = request.getParameter("nuevaFecha");

                    if (!citaDao.puedeModificarOCancelar(idReprog)) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message",
                                "No se puede reprogramar: faltan menos de 8 horas (RN-05)");
                        break;
                    }
                    LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    boolean reprog = citaDao.reprogramar(idReprog, nuevaFecha);
                    jsonResponse.addProperty("success", reprog);
                    jsonResponse.addProperty("message", reprog
                            ? "Cita reprogramada correctamente"
                            : "Error al reprogramar");
                    break;

                // ── MARCAR COMO ATENDIDA ─────────────────────────────
                case "atender":
                    int idAtender = Integer.parseInt(request.getParameter("idCita"));
                    boolean atendida = citaDao.marcarComoAtendida(idAtender);
                    jsonResponse.addProperty("success", atendida);
                    jsonResponse.addProperty("message", atendida
                            ? "Cita marcada como atendida"
                            : "Error al marcar");
                    break;

                // ── LISTAR POR PACIENTE ──────────────────────────────
                case "listarPorPaciente":
                    int idPac = Integer.parseInt(request.getParameter("pacienteId"));
                    jsonResponse.add("data",
                            gson.toJsonTree(citaDao.listarPorPaciente(idPac)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── LISTAR POR PROFESIONAL ───────────────────────────
                case "listarPorProfesional":
                    int idProf = Integer.parseInt(request.getParameter("profesionalId"));
                    jsonResponse.add("data",
                            gson.toJsonTree(citaDao.listarPorProfesional(idProf)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── LISTAR TODAS ─────────────────────────────────────
                case "listarTodas":
                    jsonResponse.add("data", gson.toJsonTree(citaDao.listarTodas()));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── DISPONIBILIDAD ───────────────────────────────────
                case "disponibilidad":
                    String esp  = request.getParameter("especialidad");
                    String fStr = request.getParameter("fecha");
                    LocalDateTime fechaDispo = LocalDateTime.parse(fStr,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    jsonResponse.add("data",
                            gson.toJsonTree(citaDao.consultarDisponibilidad(esp, fechaDispo)));
                    jsonResponse.addProperty("success", true);
                    break;

                default:
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Acción no válida");
            }

            out.print(jsonResponse.toString());

        } catch (NumberFormatException e) {
            response.setStatus(400);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Parámetro numérico inválido: " + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error del servidor: " + e.getMessage());
            e.printStackTrace();
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
