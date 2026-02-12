package com.pumalacticos.model.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.pumalacticos.model.data.ConexionDB;
import com.pumalacticos.model.domain.Producto;

/**
 * Implementación concreta del DAO para Productos usando SQLite.
 * VERSIÓN FINAL: Ajustada para manejar los 6 atributos de tu clase Producto.
 */
public class ProductoDAO implements IProductoDAO {

    public ProductoDAO() {
        inicializarTabla();
    }

    private void inicializarTabla() {
        // Agregamos 'categoria' y 'es_pesable' a la tabla
        String sql = "CREATE TABLE IF NOT EXISTS productos (" +
                     "codigo_barras TEXT PRIMARY KEY, " +
                     "nombre TEXT NOT NULL, " +
                     "precio REAL, " +
                     "stock INTEGER, " +
                     "categoria TEXT, " +
                     "es_pesable INTEGER)"; // SQLite usa INTEGER (0 o 1) para booleanos
        
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al inicializar la tabla: " + e.getMessage());
        }
    }

    @Override
    public void guardar(Producto producto) {
        // Guardamos los 6 valores
        String sql = "INSERT INTO productos(codigo_barras, nombre, precio, stock, categoria, es_pesable) VALUES(?,?,?,?,?,?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getCodigoBarras());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getStock());
            pstmt.setString(5, producto.getCategoria());    // Asumiendo que tienes este getter
            pstmt.setBoolean(6, producto.isEsRestringido());    // Asumiendo que tienes este getter
            
            pstmt.executeUpdate();
            System.out.println("Producto guardado en BD: " + producto.getNombre());
            
        } catch (SQLException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    @Override
    public Producto buscarPorCodigo(String codigoBarras) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ?";
        Producto p = null;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigoBarras);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Construimos el objeto con los 6 argumentos requeridos
                p = new Producto(
                    rs.getString("codigo_barras"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria"),
                    rs.getBoolean("es_pesable")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar: " + e.getMessage());
        }
        return p;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo_barras"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria"),
                    rs.getBoolean("es_pesable")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lista: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Producto producto) {
        // Actualizamos todos los campos editables
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, categoria = ?, es_pesable = ? WHERE codigo_barras = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());
            pstmt.setString(4, producto.getCategoria());
            pstmt.setBoolean(5, producto.isEsRestringido());
            pstmt.setString(6, producto.getCodigoBarras()); // El WHERE va al final

            pstmt.executeUpdate();
            System.out.println("Producto actualizado: " + producto.getCodigoBarras());

        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(String codigoBarras) {
        String sql = "DELETE FROM productos WHERE codigo_barras = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoBarras);
            pstmt.executeUpdate();
            System.out.println("Eliminado: " + codigoBarras);

        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
        }
    }
}