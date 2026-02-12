package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.Venta;

public class HistorialObserver implements IVentaObserver {

    @Override
    public void update(Venta venta) {
        // Guardamos la venta en la lista en memoria
        DB.ventas.add(venta);
        
        System.out.println(">>> [HistorialObserver] update() llamado. Venta registrada.");
        System.out.println("    - ID: " + venta.getId() + " | Total: $" + venta.getTotal());
    }
}