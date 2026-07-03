/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import java.sql.*;

public class ConexionSingleton {
 // VARIABLE ESTÁTICA DE CONEXIÓN
    public static Connection connection;
 
   
    private static boolean hookRegistered = false;
 
    // MÉTODO getConnection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
 
           
                Class.forName("oracle.jdbc.OracleDriver");
 
         
                connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521/XE",
                        "BDPI",
                        "123"
                );
                System.out.println("Conectado a Oracle XE");
 
               
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