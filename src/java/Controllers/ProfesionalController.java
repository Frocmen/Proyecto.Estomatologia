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

    private final IProfesional profesionalDao = new ProfesionalDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        try (PrintWriter out = response.getWriter()) {

            if ("listar".equals(action)) {
                jsonResponse.add("data", gson.toJsonTree(profesionalDao.listarProfesionales()));
                jsonResponse.addProperty("success", true);

            } else if ("guardar".equals(action)) {
                Profesional p = new Profesional();
                p.setNombre(request.getParameter("nombre"));
                p.setApellido(request.getParameter("apellido"));
                p.setDni(request.getParameter("dni"));
                p.setTelefono(request.getParameter("telefono"));
                p.setEmail(request.getParameter("email"));
                p.setDireccion(request.getParameter("direccion"));
                p.setEspecialidad(request.getParameter("especialidad"));
                p.setColegiatura(request.getParameter("colegiatura"));

                int id = profesionalDao.registrarProfesional(p);
                jsonResponse.addProperty("success", id > 0);
                jsonResponse.addProperty("message", id > 0 ? "Profesional registrado correctamente" : "Error al registrar");

            } else if ("buscarPorEspecialidad".equals(action)) {
                String especialidad = request.getParameter("especialidad");
                jsonResponse.add("data", gson.toJsonTree(profesionalDao.buscarPorEspecialidad(especialidad)));
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
