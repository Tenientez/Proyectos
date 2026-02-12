package com.pumalacticos.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LineaVentaTest {
    
    @Test 
    void calculcarSubtotal(){
        Producto p = new Producto("002", "Gansito", 23, 20, "Dulces", false);
        LineaVenta l = new LineaVenta(p, 3);

        assertEquals(69, l.getSubtotal());
    }

    @Test 
    void recalculaSubtotal(){
        Producto p  = new Producto("002", "Gansito", 23, 20, "Dulces", false);
        LineaVenta l = new LineaVenta(p, 2);

        l.agregarCantidad(2);

        assertEquals(4, l.getCantidad());
        assertEquals(92, l.getSubtotal());
    }

    @Test 
    void cantidadActualizaSubtotal(){
        Producto p = new Producto("002", "Gansito", 23, 20, "Dulces", false);
        LineaVenta l = new LineaVenta(p, 1);

        l.setCantidad(8);

        assertEquals(8, l.getCantidad());
        assertEquals(184, l.getSubtotal());
    }
    
}
