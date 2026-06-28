/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.PacienteDaoImpl;
import DaoImpl.PersonaDaoImpl;
import DaoImpl.UsuarioDaoImpl;
import Interface.IPaciente;
import Interface.IPersona;
import Interface.IUsuario;
import Model.Paciente;
import Model.Persona;
import Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Frocmen
 */
@WebServlet(name = "AuthController", urlPatterns = {"/AuthController"})
public class AuthController extends HttpServlet {

    // LLAMADA GLOBAL — arquitectura del profesor
    private final IUsuario uDao = new UsuarioDaoImpl();
    private final IPaciente pDao = new PacienteDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("{\"message\":\"AuthController activo\"}");
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        // Protección contra action null
        if (action == null) {
            action = "";
        }

        try (PrintWriter out = response.getWriter()) {

            // ── VALIDAR LOGIN ──────────────────────────────────────────
            if ("validar".equals(action)) {
                String usuario = request.getParameter("usuario");
                String clave = request.getParameter("password");

                Usuario us = uDao.validate(usuario, clave);

                if (us != null && us.getUsuario() != null) {
                    // ABRIENDO SESIÓN
                    HttpSession sesion = request.getSession(true);
                    sesion.setAttribute("usuario", us);

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Inicio de sesión exitoso");
                    jsonResponse.add("userData", gson.toJsonTree(us));
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Credenciales incorrectas");
                }

            // ── REGISTER — Registro de paciente nuevo ─────────────────
            // Usa PacienteDaoImpl — tabla PACIENTES en Oracle
            } else if ("register".equals(action)) {
                Paciente p = new Paciente();
                p.setNombre(request.getParameter("nombre"));
                p.setApellido(request.getParameter("apellido"));
                p.setDni(request.getParameter("dni"));
                p.setTelefono(request.getParameter("telefono"));
                p.setEmail(request.getParameter("email"));

                // La contraseña se hashea en PacienteDaoImpl al guardar
                // Si viene password del form, se guarda hasheada via UsuarioDaoImpl
                // En este flujo básico el paciente queda VERIFICADO = 'N'
                int resultado = pDao.registrarPaciente(p);

                jsonResponse.addProperty("success", resultado > 0);
                jsonResponse.addProperty("message", resultado > 0
                        ? "Registro exitoso. Verifique su correo para activar su cuenta."
                        : "Error al registrar. Verifique los datos ingresados.");

            // ── SALIR — Cierre de sesión ───────────────────────────────
            } else if ("salir".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sesión cerrada correctamente");

            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Acción no válida");
            }

            out.print(jsonResponse.toString());

        } catch (Exception e) {
            // ERROR 500 — Falla en lógica del servidor
            response.setStatus(500);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
        }
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
