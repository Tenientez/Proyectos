package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.dao.VentaDAO;
import com.pumalacticos.model.domain.Venta;

public class HistorialObserver implements IVentaObserver {

    @Override
    public void update(Venta venta) {
        // Instanciamos el DAO localmente o podríamos tenerlo estático, 
        // pero instanciar es seguro y barato en este contexto.
        VentaDAO dao = new VentaDAO();
        dao.guardar(venta);
        
        System.out.println(">>> [HistorialObserver] Venta guardada en SQLite.");
        System.out.println("    - Folio: " + venta.getId() + " | Total: $" + venta.getTotal());
    }
}