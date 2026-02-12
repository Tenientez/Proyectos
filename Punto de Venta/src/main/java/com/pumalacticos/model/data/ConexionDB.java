package com.pumalacticos.model.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a SQLite usando el Patrón Singleton.
 * * PATRÓN DE DISEÑO: SINGLETON
 * Objetivo: Garantizar que solo exista una instancia de la conexión a la base de datos
 * durante el ciclo de vida de la aplicación.
 */
public class ConexionDB {

    // 1. Variable estática que almacenará la única instancia de la conexión.
    private static Connection connection = null;

    // Definimos la ruta donde se creará el archivo de base de datos.
    // "jdbc:sqlite:" es el protocolo, "punto_venta.db" es el nombre del archivo.
    private static final String URL = "jdbc:sqlite:punto_venta.db";

    // Constructor privado: Evita que alguien haga "new ConexionDB()" desde fuera.
    private ConexionDB() { }

    /**
     * Método estático para obtener la conexión.
     * Si no existe, la crea. Si existe, devuelve la que ya está abierta.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Cargamos dinámicamente el driver de SQLite (opcional en versiones modernas, pero buena práctica)
                // Esto asegura que Java sepa cómo hablar con el archivo .db
                Class.forName("org.sqlite.JDBC");
                
                // Establecemos la conexión
                connection = DriverManager.getConnection(URL);
                System.out.println("Conexión a SQLite establecida exitosamente.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Método para cerrar la conexión cuando cerremos la app.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}