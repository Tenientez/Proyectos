package com.pumalacticos.model.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class VentaTest {

    @Test 
    void asignarValores(){
        Producto p = new Producto("003", "Oreos", 18, 13, "Galletas", false);
        LineaVenta l = new LineaVenta(p, 2);
        DatosFacturacion d = new DatosFacturacion("PELR820910H78", "contacto.felipe.lopez@gmail.com");
        LocalDateTime hoy = LocalDateTime.now();

        Venta v = new Venta("V001", hoy, List.of(l), 36, 50, 14, "Efectivo", d);

        assertEquals("V001", v.getId());
        assertEquals(hoy, v.getFecha());
        assertEquals(36, v.getTotal());
        assertEquals(50, v.getMontoRecibido());
        assertEquals(14, v.getCambio());
        assertEquals("Efectivo", v.getMetodoPago());
        assertEquals(d, v.getDatosFacturacion());
        assertEquals(1, v.getLineas().size());
    }

    @Test 
    void mostrarFormatoToString(){
        Venta v = new Venta("V002", LocalDateTime.now(), List.of(), 100, 200, 100, "Tarjeta", null);
        assertEquals("Venta #V002 ($100.0)", v.toString());
    }
    
}
