package com.pumalacticos.model.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pumalacticos.model.data.ConexionDB;
import com.pumalacticos.model.domain.DatosFacturacion;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;

public class VentaDAO {

    public void guardar(Venta venta) {
        String sqlVenta = "INSERT INTO ventas(id, fecha, total, monto_recibido, cambio, metodo_pago, rfc, correo) VALUES(?,?,?,?,?,?,?,?)";
        String sqlDetalle = "INSERT INTO ventas_detalle(id_venta, codigo_producto, nombre_producto, cantidad, precio_unitario, subtotal) VALUES(?,?,?,?,?,?)";

        Connection conn = ConexionDB.getConnection();
        
        try {
            conn.setAutoCommit(false); 

            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta)) {
                pstmt.setString(1, venta.getId());
                pstmt.setString(2, venta.getFecha().toString());
                pstmt.setDouble(3, venta.getTotal());
                pstmt.setDouble(4, venta.getMontoRecibido());
                pstmt.setDouble(5, venta.getCambio());
                pstmt.setString(6, venta.getMetodoPago());
                
                if (venta.getDatosFacturacion() != null) {
                    pstmt.setString(7, venta.getDatosFacturacion().getRfc());
                    pstmt.setString(8, venta.getDatosFacturacion().getCorreo());
                } else {
                    pstmt.setNull(7, Types.VARCHAR);
                    pstmt.setNull(8, Types.VARCHAR);
                }
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                for (LineaVenta linea : venta.getLineas()) {
                    pstmtDetalle.setString(1, venta.getId());
                    pstmtDetalle.setString(2, linea.getProducto().getCodigoBarras());
                    pstmtDetalle.setString(3, linea.getProducto().getNombre());
                    pstmtDetalle.setInt(4, linea.getCantidad());
                    pstmtDetalle.setDouble(5, linea.getProducto().getPrecio());
                    pstmtDetalle.setDouble(6, linea.getSubtotal());
                    pstmtDetalle.addBatch();
                }
                pstmtDetalle.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Venta> obtenerHistorial() {
        List<Venta> historial = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha DESC";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String fechaStr = rs.getString("fecha");
                LocalDateTime fecha = LocalDateTime.parse(fechaStr);
                double total = rs.getDouble("total");
                double monto = rs.getDouble("monto_recibido");
                double cambio = rs.getDouble("cambio");
                String metodo = rs.getString("metodo_pago");
                String rfc = rs.getString("rfc");
                String correo = rs.getString("correo");

                DatosFacturacion datosFac = (rfc != null) ? new DatosFacturacion(rfc, correo) : null;
                List<LineaVenta> lineas = obtenerLineasPorVenta(id, conn);

                historial.add(new Venta(id, fecha, lineas, total, monto, cambio, metodo, datosFac));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historial;
    }

    private List<LineaVenta> obtenerLineasPorVenta(String idVenta, Connection conn) throws SQLException {
        List<LineaVenta> lineas = new ArrayList<>();
        String sql = "SELECT * FROM ventas_detalle WHERE id_venta = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idVenta);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    Producto p = new Producto(
                        rs.getString("codigo_producto"),
                        rs.getString("nombre_producto"),
                        rs.getDouble("precio_unitario"),
                        0, "Historial", false
                    );
                    lineas.add(new LineaVenta(p, rs.getInt("cantidad")));
                }
            }
        }
        return lineas;
    }

    // CORREGIDO: Recibe la fecha exacta desde Java para evitar errores de Zona Horaria
    public void limpiarHistorialAnteriorA(LocalDateTime fechaLimite) {
        String sql = "DELETE FROM ventas WHERE fecha <= ?";
        // Limpieza de huérfanos manual
        String sqlDetalle = "DELETE FROM ventas_detalle WHERE id_venta NOT IN (SELECT id FROM ventas)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Convertimos la fecha de Java a String ISO-8601 que es como lo guardamos
            pstmt.setString(1, fechaLimite.toString());
            
            int borrados = pstmt.executeUpdate();
            
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sqlDetalle);
            }
            
            if(borrados > 0) {
                System.out.println("Se eliminaron " + borrados + " ventas anteriores a: " + fechaLimite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}