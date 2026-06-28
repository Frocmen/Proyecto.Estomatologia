/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.EspecialidadDaoImpl;
import Interface.IEspecialidad;
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
@WebServlet(name = "EspecialidadController", urlPatterns = {"/EspecialidadController"})
public class EspecialidadController extends HttpServlet {
    
    // LLAMADA GLOBAL
    private final IEspecialidad especialidadDao = new EspecialidadDaoImpl();
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

                // ── LISTAR ESPECIALIDADES ACTIVAS ───────────────────────
                // RN-01: Solo especialidades con al menos 1 médico activo
                case "listar":
                    jsonResponse.add("data",
                            gson.toJsonTree(especialidadDao.listarEspecialidades()));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── VERIFICAR SI EXISTE UNA ESPECIALIDAD ────────────────
                // RN-D09 del CU-03: validar que la especialidad exista
                case "existe":
                    String nombre = request.getParameter("nombre");
                    boolean existe = especialidadDao.existeEspecialidad(nombre);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("existe", existe);
                    jsonResponse.addProperty("message", existe
                            ? "Especialidad encontrada"
                            : "Especialidad no encontrada");
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
