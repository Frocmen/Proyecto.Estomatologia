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

   // LLAMADA GLOBAL
    private final IPaciente pacienteDao = new PacienteDaoImpl();
    private final Gson gson = Util.GsonProvider.getGson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
          response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String action = request.getParameter("action");
    JsonObject jsonResponse = new JsonObject();

    if (action == null) {
        action = "listar";
    }

    PrintWriter out = response.getWriter();

    try {
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

                int id = pacienteDao.registrarPaciente(p);
                jsonResponse.addProperty("success", id > 0);
                jsonResponse.addProperty("message", id > 0
                        ? "Paciente registrado correctamente"
                        : "Error al registrar");
                if (id > 0) {
                    jsonResponse.addProperty("id", id);
                }
                break;

            case "buscar":
                String dni = request.getParameter("dni");
                Paciente paciente = pacienteDao.buscarPacientePorDni(dni);
                jsonResponse.addProperty("success", paciente != null);
                if (paciente != null) {
                    jsonResponse.add("data", gson.toJsonTree(paciente));
                } else {
                    jsonResponse.addProperty("message", "Paciente no encontrado");
                }
                break;

            case "editar":
                Paciente pe = new Paciente();
                pe.setId(Integer.parseInt(request.getParameter("id")));
                pe.setNombre(request.getParameter("nombre"));
                pe.setApellido(request.getParameter("apellido"));
                pe.setDni(request.getParameter("dni"));
                pe.setTelefono(request.getParameter("telefono"));
                pe.setEmail(request.getParameter("email"));

                boolean editado = pacienteDao.editar(pe);
                jsonResponse.addProperty("success", editado);
                jsonResponse.addProperty("message", editado
                        ? "Paciente actualizado correctamente"
                        : "Error al actualizar");
                break;

            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                boolean eliminado = pacienteDao.delete(idEliminar);
                jsonResponse.addProperty("success", eliminado);
                jsonResponse.addProperty("message", eliminado
                        ? "Paciente desactivado correctamente"
                        : "Error al desactivar");
                break;

            default:
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Acción no válida");
        }

    } catch (Exception e) {
        response.setStatus(500);
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "Error: " + e.getMessage());
        e.printStackTrace();
    }

    out.print(jsonResponse.toString());
    out.flush();
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