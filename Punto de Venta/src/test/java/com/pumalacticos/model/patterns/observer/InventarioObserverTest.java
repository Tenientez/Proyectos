package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioObserverTest {
    private InventarioObserver observer;

    private Producto p1;
    private Producto p2;

    @BeforeEach
    void setUp(){
        observer = new InventarioObserver();
        DB.productos.clear();
        DB.ventas.clear();

        p1 = new Producto("001", "Coca-Cola", 22, 10, "Bebidas", false);
        p2 = new Producto("002", "Gansito", 15, 5, "Dulce", false);

        DB.productos.addAll(List.of(p1,p2));
    }

    @Test
    void updatePruebaDisminuirStock(){
        LineaVenta l = new LineaVenta(p1, 3);
        Venta venta = new Venta("X1", LocalDateTime.now(), List.of(l), 66, 70, 4, "Efectivo", null);
        observer.update(venta);
        assertEquals(7, p1.getStock());
    }
    
    @Test
    void updatePruebaEvitarStockNegativo(){
        LineaVenta l = new LineaVenta(p2, 99);

        Venta venta = new Venta("X2", LocalDateTime.now(), List.of(l), 1485, 1500, 15, "Efectivo", null);
        observer.update(venta);
        
        assertEquals(0, p2.getStock());
    }

    @Test
    void updatePruebaProductoNoExistente(){
        Producto falso = new Producto("999", "Nada", 10, 5, "Testeo", false);
        LineaVenta l = new LineaVenta(falso, 2);
        Venta venta = new Venta("X3", LocalDateTime.now(), List.of(l), 20, 50, 30, "Tarjeta", null);

        assertDoesNotThrow(()->observer.update(venta));

        assertEquals(10, p1.getStock());
        assertEquals(5, p2.getStock());
    }

    @Test
    void updatePruebaActualizarMuchosProductos(){
        LineaVenta l1 = new LineaVenta(p1, 2);
        LineaVenta l2 = new LineaVenta(p2, 4);
        Venta venta = new Venta("X4", LocalDateTime.now(), List.of(l1,l2), 100, 120, 20, "Efectivo", null);
        observer.update(venta);

        assertEquals(8, p1.getStock());
        assertEquals(1, p2.getStock());

    }
}
