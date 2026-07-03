/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.PacienteDaoImpl;
import DaoImpl.UsuarioDaoImpl;
import Interface.IPaciente;
import Interface.IUsuario;
import Model.Paciente;
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
   
    private final IUsuario uDao  = new UsuarioDaoImpl();
    private final IPaciente pDao = new PacienteDaoImpl();
    private final Gson gson = Util.GsonProvider.getGson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                  throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
 
        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();
 
        if (action == null) action = "";
 
        try (PrintWriter out = response.getWriter()) {
 
            switch (action) {
 
                
                case "validar":
                    String usuario = request.getParameter("usuario");
                    String password = request.getParameter("password");
 
                    Usuario u = uDao.validate(usuario, password);
 
                    if (u != null && u.getUsuario() != null) {
                        // Guardar sesión en el servidor
                        HttpSession session = request.getSession();
                        session.setAttribute("usuario", u);
                        session.setMaxInactiveInterval(1800); // 30 min
 
                        
                        JsonObject userData = new JsonObject();
                        userData.addProperty("id", u.getPersona() != null
                                ? u.getPersona().getId() : 0);
                        userData.addProperty("usuario", u.getUsuario());
                        userData.addProperty("rol", u.getRol().name());
 
                       
                        if (u.getPersona() != null) {
                            JsonObject persona = new JsonObject();
                            persona.addProperty("id",       u.getPersona().getId());
                            persona.addProperty("nombre",   u.getPersona().getNombre());
                            persona.addProperty("apellido", u.getPersona().getApellido() != null
                                    ? u.getPersona().getApellido() : "");
                            userData.add("persona", persona);
                        }
 
                        jsonResponse.addProperty("success", true);
                        jsonResponse.add("userData", userData);
                        jsonResponse.addProperty("message", "Login exitoso");
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Credenciales incorrectas");
                    }
                    break;
 
                
                case "register":
                    String nombre   = request.getParameter("nombre");
    String apellido = request.getParameter("apellido");
    String dni      = request.getParameter("dni");
    String telefono = request.getParameter("telefono");
    String email    = request.getParameter("email");
    String pass     = request.getParameter("password");

    System.out.println("=== REGISTRO ===");
    System.out.println("Nombre: " + nombre);
    System.out.println("Email:  " + email);

    if (nombre == null || email == null || pass == null ||
        nombre.trim().isEmpty() || email.trim().isEmpty() || pass.trim().isEmpty()) {
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "Faltan campos obligatorios");
        break;
    }

    Paciente nuevo = new Paciente();
    nuevo.setNombre(nombre.trim());
    nuevo.setApellido(apellido != null ? apellido.trim() : "");
    nuevo.setDni(dni         != null ? dni.trim()      : "");
    nuevo.setTelefono(telefono != null ? telefono.trim() : "");
    nuevo.setEmail(email.trim());
    nuevo.setPasswordHash(pass.trim());

    int idNuevo = pDao.registrarPaciente(nuevo);
    System.out.println("ID generado: " + idNuevo);

    if (idNuevo > 0) {
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "Paciente registrado correctamente");
    } else {
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "Error al guardar en la base de datos");
    }
    break;
 
               
                case "salir":
                    HttpSession sesionActual = request.getSession(false);
                    if (sesionActual != null) {
                        sesionActual.invalidate();
                    }
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Sesión cerrada");
                    break;
 
              
                case "verificar":
                    HttpSession sesionVerif = request.getSession(false);
                    if (sesionVerif != null && sesionVerif.getAttribute("usuario") != null) {
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("loggedIn", true);
                    } else {
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("loggedIn", false);
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