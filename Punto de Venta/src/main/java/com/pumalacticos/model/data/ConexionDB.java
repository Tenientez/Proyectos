package com.pumalacticos.model.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static Connection connection = null;
    private static final String URL = "jdbc:sqlite:punto_venta.db";

    private ConexionDB() { }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                System.out.println("Conexión a SQLite establecida.");
                inicializarTablas(); // Aseguramos que las tablas existan
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error conexión: " + e.getMessage());
        }
        return connection;
    }

    private static void inicializarTablas() throws SQLException {
        String sqlVentas = "CREATE TABLE IF NOT EXISTS ventas (" +
                "id TEXT PRIMARY KEY, " +
                "fecha TEXT, " + // Formato ISO para ordenamiento
                "total REAL, " +
                "monto_recibido REAL, " +
                "cambio REAL, " +
                "metodo_pago TEXT, " +
                "rfc TEXT, " +
                "correo TEXT)";

        String sqlDetalle = "CREATE TABLE IF NOT EXISTS ventas_detalle (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_venta TEXT, " +
                "codigo_producto TEXT, " +
                "nombre_producto TEXT, " +
                "cantidad INTEGER, " +
                "precio_unitario REAL, " +
                "subtotal REAL, " +
                "FOREIGN KEY(id_venta) REFERENCES ventas(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlVentas);
            stmt.execute(sqlDetalle);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}