package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Venta;

public class InventarioObserver implements IVentaObserver {

    @Override
    public void update(Venta venta) {
        System.out.println(">>> [InventarioObserver] update() llamado. Actualizando stock...");

        for (LineaVenta linea : venta.getLineas()) {
            String codigoBuscado = linea.getProducto().getCodigoBarras();
            int cantidadVendida = linea.getCantidad();

            DB.productos.stream()
                .filter(p -> p.getCodigoBarras().equals(codigoBuscado))
                .findFirst()
                .ifPresent(productoEnDB -> {
                    int nuevoStock = productoEnDB.getStock() - cantidadVendida;
                    if (nuevoStock < 0) nuevoStock = 0;
                    
                    productoEnDB.setStock(nuevoStock);
                    System.out.println("    - Producto: " + productoEnDB.getNombre() + 
                                       " | Nuevo Stock: " + nuevoStock);
                });
        }
    }
}