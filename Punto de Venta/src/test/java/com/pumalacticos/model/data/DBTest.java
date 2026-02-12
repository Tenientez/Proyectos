package com.pumalacticos.model.data;

import com.pumalacticos.model.domain.Producto;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DBTest {

    @BeforeEach
    void limpiarVentas(){
        DB.productos.clear();
        DB.ventas.clear();

        DB.productos.add(new Producto("001", "Leche Santa Clara 1L", 28.00, 50, "Lacteos", false));
        DB.productos.add(new Producto("002", "Coca Cola 600ml", 18.50, 100, "Bebidas", false));
        DB.productos.add(new Producto("003", "Cerveza Victoria Latón", 22.00, 24, "Alcohol", true));
        DB.productos.add(new Producto("004", "Papas Sabritas Sal", 17.00, 30, "Botanas", false));
        DB.productos.add(new Producto("005", "Cigarros Malboro", 75.00, 20, "Tabaco", true));
        DB.productos.add(new Producto("006", "Galletas Emperador", 15.00, 40, "Galletas", false));

    }


    @Test
    void productosPruebaTenerProductosInciales(){
        List<Producto> productos = DB.productos;

        assertEquals(6, productos.size());

        assertEquals("001", productos.get(0).getCodigoBarras());
        assertEquals("Leche Santa Clara 1L", productos.get(0).getNombre());
        assertEquals(28.00, productos.get(0).getPrecio());
        assertEquals("006", productos.get(5).getCodigoBarras());
        assertEquals("Galletas Emperador", productos.get(5).getNombre());
    }

    @Test 
    void ventasPruebaEstarIncioVacia(){
        assertTrue(DB.ventas.isEmpty());
    }

    @Test 
    void ventasPruebaAgregarVentas(){
        assertTrue(DB.ventas.isEmpty());
        DB.ventas.add(null);
        assertEquals(1, DB.ventas.size());

    }
    
}
