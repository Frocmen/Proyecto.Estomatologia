/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.PacienteDaoImpl;
import Interface.IPaciente;
import Model.Paciente;
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
@WebServlet(name = "PacienteController", urlPatterns = {"/PacienteController"})
public class PacienteController extends HttpServlet {

  private final IPaciente pacienteDao = new PacienteDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        try (PrintWriter out = response.getWriter()) {

            switch (action) {
                case "listar":
                    jsonResponse.add("data", gson.toJsonTree(pacienteDao.listarPacientes()));
                    jsonResponse.addProperty("success", true);
                    break;

                case "guardar":
                    Paciente p = new Paciente();
                    p.setNombre(request.getParameter("nombre"));
                    p.setApellido(request.getParameter("apellido"));
                    p.setDni(request.getParameter("dni"));
                    p.setTelefono(request.getParameter("telefono"));
                    p.setEmail(request.getParameter("email"));
                    p.setDireccion(request.getParameter("direccion"));
                    p.setFechaNacimiento(request.getParameter("fechaNacimiento"));

                    int id = pacienteDao.registrarPaciente(p);
                    jsonResponse.addProperty("success", id > 0);
                    jsonResponse.addProperty("message", id > 0 ? "Paciente registrado correctamente" : "Error al registrar");
                    break;

                case "buscar":
                    String dni = request.getParameter("dni");
                    Paciente paciente = pacienteDao.buscarPacientePorDni(dni);
                    jsonResponse.addProperty("success", paciente != null);
                    if (paciente != null) {
                        jsonResponse.add("data", gson.toJsonTree(paciente));
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
