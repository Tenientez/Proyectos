package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.dao.ProductoDAO; // Importamos DAO
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;

public class InventarioObserver implements IVentaObserver {

    @Override
    public void update(Venta venta) {
        System.out.println(">>> [InventarioObserver] Actualizando stock en SQLite...");
        
        // Creamos una instancia del DAO para actualizar la BD
        ProductoDAO productoDAO = new ProductoDAO();

        for (LineaVenta linea : venta.getLineas()) {
            String codigo = linea.getProducto().getCodigoBarras();
            int cantidadVendida = linea.getCantidad();

            // 1. Obtener el producto actual de la BD
            Producto productoEnBD = productoDAO.buscarPorCodigo(codigo);
            
            if (productoEnBD != null) {
                // 2. Calcular nuevo stock
                int nuevoStock = productoEnBD.getStock() - cantidadVendida;
                if (nuevoStock < 0) nuevoStock = 0;
                
                // 3. Actualizar el objeto y guardar en BD
                productoEnBD.setStock(nuevoStock);
                productoDAO.actualizar(productoEnBD);
                
                System.out.println("    - Actualizado: " + productoEnBD.getNombre() + " | Stock: " + nuevoStock);
            }
        }
    }
}