/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import java.sql.*;

public class ConexionSingleton {
 // VARIABLE ESTÁTICA DE CONEXIÓN
    public static Connection connection;
 
    // BANDERA: evita registrar el ShutdownHook más de una vez
    private static boolean hookRegistered = false;
 
    // MÉTODO getConnection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
 
                // Driver Oracle JDBC
                Class.forName("oracle.jdbc.OracleDriver");
 
                // Conexión Oracle XE
                connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521/XE",
                        "BDPI",
                        "123"
                );
                System.out.println("Conectado a Oracle XE");
 
                // ✅ Registrar el ShutdownHook UNA SOLA VEZ
                if (!hookRegistered) {
                    hookRegistered = true;
                    Runtime.getRuntime().addShutdownHook(new getClose());
                }
            }
            return connection;
 
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("CONEXIÓN FALLIDA: ", e);
        }
    }
 
    // CIERRE DE CONEXIÓN AL TERMINAR LA JVM
    static class getClose extends Thread {
        @Override
        public void run() {
            try {
                // ✅ Cerrar directamente la variable, sin llamar getConnection()
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexión cerrada correctamente.");
                }
            } catch (Exception ex) {
                System.out.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
}