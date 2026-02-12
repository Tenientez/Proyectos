package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistorialObserverTest {
    private HistorialObserver observer;

    @BeforeEach
    void setUp(){
        observer = new HistorialObserver();
        DB.ventas.clear();
    }

    @Test
    void updatePruebaAgregarVentaHistorial(){
        Venta venta = new Venta("T001", LocalDateTime.now(), List.of(), 100, 120, 20, "Efectivo", null);
        observer.update(venta);

        assertEquals(1, DB.ventas.size());
        assertEquals(venta, DB.ventas.get(0));
    }

    @Test
    void updatePruebaAgregarVariasVentas(){
        Venta v1 = new Venta("A01", LocalDateTime.now(), List.of(), 50, 100, 50, "Efectivo", null);
        Venta v2 = new Venta("A02", LocalDateTime.now(), List.of(), 20, 50, 30, "Tarjeta", null);

        observer.update(v1);
        observer.update(v2);

        assertEquals(2, DB.ventas.size());
        assertEquals("A01", DB.ventas.get(0).getId());
        assertEquals("A02", DB.ventas.get(1).getId());
    }
    
}
