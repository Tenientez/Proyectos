package com.pumalacticos.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoTest {

    @Test
    void asignarValores(){
        Producto p = new Producto("001", "Coca-Cola", 18.5, 10, "Bebidas", false);

        assertEquals("001", p.getCodigoBarras());
        assertEquals("Coca-Cola", p.getNombre());
        assertEquals(18.5, p.getPrecio());
        assertEquals(10, p.getStock());
        assertEquals("Bebidas", p.getCategoria());
        assertFalse(p.isEsRestringido());
    }

    @Test 
    void actualizarValores(){
        Producto p = new Producto("001", "Coca", 10, 5, "Bebidas", false);
        p.setPrecio(20);
        p.setStock(50);

        assertEquals(20, p.getPrecio());
        assertEquals(50, p.getStock());
    }

    @Test 
    void darFormatoCorrecto(){
        Producto p = new Producto("001", "Coca-Cola", 18.5, 10, "Bebidas", false);
        assertEquals("Coca-Cola ($18.5)", p.toString());
    }

}
