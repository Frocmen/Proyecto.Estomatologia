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

        try (PrintWriter out = response.getWriter()) {

            if ("registrar".equals(action)) {
                // Aquí se recibirían los IDs y la fecha
                int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
                int idProfesional = Integer.parseInt(request.getParameter("idProfesional"));
                String fechaStr = request.getParameter("fechaHora");

                // Convertir String a LocalDateTime (ajusta según formato)
                LocalDateTime fechaHora = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                // Crear objetos temporales (en práctica se buscarían por ID)
                Paciente paciente = new Paciente();
                paciente.setId_persona(idPaciente);
                
                Profesional profesional = new Profesional();
                profesional.setId_persona(idProfesional);

                Cita cita = new Cita();
                cita.setPaciente(paciente);
                cita.setProfesional(profesional);
                cita.setFechaHora(fechaHora);
                cita.setEstado("CONFIRMADA");

                int idCita = citaDao.registrar(cita);

                jsonResponse.addProperty("success", idCita > 0);
                jsonResponse.addProperty("message", idCita > 0 ? "Cita registrada correctamente" : "Error al registrar cita");
                jsonResponse.addProperty("idCita", idCita);

            } else if ("cancelar".equals(action)) {
                int idCita = Integer.parseInt(request.getParameter("idCita"));
                boolean resultado = citaDao.cancelar(idCita);

                jsonResponse.addProperty("success", resultado);
                jsonResponse.addProperty("message", resultado ? "Cita cancelada" : "No se pudo cancelar la cita");

            } else if ("listarPorPaciente".equals(action)) {
                int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
                jsonResponse.add("data", gson.toJsonTree(citaDao.listarPorPaciente(idPaciente)));
                jsonResponse.addProperty("success", true);

            } else {
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
