/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DaoImpl.PersonaDaoImpl;
import DaoImpl.UsuarioDaoImpl;
import Interface.IPersona;
import Interface.IUsuario;
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

    private final IUsuario uDao = new UsuarioDaoImpl();
    private final IPersona pDao = new PersonaDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AuthController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AuthController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
         String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        try (PrintWriter out = response.getWriter()) {

            if ("validar".equals(action)) {
                String usuario = request.getParameter("usuario");
                String clave = request.getParameter("password");

                Usuario us = uDao.validate(usuario, clave);

                if (us != null && us.getUsuario() != null) {
                    HttpSession sesion = request.getSession(true);
                    sesion.setAttribute("usuario", us);

                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Inicio de sesión exitoso");
                    jsonResponse.add("userData", gson.toJsonTree(us));
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Credenciales incorrectas");
                }

            } else if ("register".equals(action)) {
                Persona p = new Persona();
                Usuario us = new Usuario();

                p.setNombre(request.getParameter("nombre"));
                p.setEmail(request.getParameter("email"));
                p.setDireccion(request.getParameter("direccion"));
                p.setTelefono(request.getParameter("telefono"));
                us.setClave(request.getParameter("password"));

                int resultado = pDao.insert(p, us);

                jsonResponse.addProperty("success", resultado > 0);
                jsonResponse.addProperty("message", resultado > 0 ? "Registro exitoso" : "Error al registrar");

            } else if ("salir".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session != null) session.invalidate();

                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sesión cerrada correctamente");
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
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
