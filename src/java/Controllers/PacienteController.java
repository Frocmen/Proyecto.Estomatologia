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

                // ── LISTAR PACIENTES ────────────────────────────────────
                case "listar":
                    jsonResponse.add("data", gson.toJsonTree(pacienteDao.listarPacientes()));
                    jsonResponse.addProperty("success", true);
                    break;

                // ── GUARDAR PACIENTE ────────────────────────────────────
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
                    break;

                // ── BUSCAR POR DNI ──────────────────────────────────────
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

                // ── EDITAR PACIENTE ─────────────────────────────────────
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

                // ── ELIMINAR PACIENTE (baja lógica) ────────────────────
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
