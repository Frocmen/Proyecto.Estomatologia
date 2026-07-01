/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.ProfesionalDaoImpl;
import Interface.IProfesional;
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

/**
 *
 * @author Frocmen
 */
@WebServlet(name = "ProfesionalController", urlPatterns = {"/ProfesionalController"})
public class ProfesionalController extends HttpServlet {

    // LLAMADA GLOBAL
    private final IProfesional profesionalDao = new ProfesionalDaoImpl();
private final Gson gson = Util.GsonProvider.getGson();
    
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

                // ── LISTAR PROFESIONALES ────────────────────────────────
                case "listar":
                    jsonResponse.add("data",
                            gson.toJsonTree(profesionalDao.listarProfesionales()));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── GUARDAR PROFESIONAL ─────────────────────────────────
                case "guardar":
                    Profesional p = new Profesional();
                    p.setNombre(request.getParameter("nombre"));
                    p.setApellido(request.getParameter("apellido"));
                    p.setTelefono(request.getParameter("telefono"));
                    p.setEmail(request.getParameter("email"));

                    int id = profesionalDao.registrarProfesional(p);
                    jsonResponse.addProperty("success", id > 0);
                    jsonResponse.addProperty("message", id > 0
                            ? "Profesional registrado correctamente"
                            : "Error al registrar");
                    break;

                // ── BUSCAR POR ESPECIALIDAD ─────────────────────────────
                case "buscarPorEspecialidad":
                    String especialidad = request.getParameter("especialidad");
                    jsonResponse.add("data",
                            gson.toJsonTree(profesionalDao.buscarPorEspecialidad(especialidad)));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── BUSCAR POR ID ───────────────────────────────────────
                case "buscar":
                    int idBuscar = Integer.parseInt(request.getParameter("id"));
                    Profesional prof = profesionalDao.buscarPorId(idBuscar);
                    jsonResponse.addProperty("success", prof != null);
                    if (prof != null) {
                        jsonResponse.add("data", gson.toJsonTree(prof));
                    } else {
                        jsonResponse.addProperty("message", "Profesional no encontrado");
                    }
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
