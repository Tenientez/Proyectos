package com.pumalacticos.model.patterns.builderVenta;

import com.pumalacticos.model.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VentaBuilderTest {

    private Producto p1;
    private Producto p2;
    private VentaBuilder builder;

    @BeforeEach 
    void setUp(){
        p1 = new Producto("004", "Chokis", 18.0, 10, "Galletas", false);
        p2 = new Producto("009", "Monster", 29.0, 8, "Bebidas", false);
        builder = new VentaBuilder();
    }

    @Test
    void agregarProductoPruebaNuevaLinea(){
        builder.agregarProducto(p1, 2);
        assertEquals(1, builder.obtenerLineas().size());
        assertEquals(2, builder.obtenerLineas().get(0).getCantidad());
        assertEquals(36.0, builder.obtenerTotalActual());
    }

    @Test
    void agregarProductoPruebaConProductoYaExistente(){
        builder.agregarProducto(p1, 1);
        builder.agregarProducto(p1, 3);

        LineaVenta l = builder.obtenerLineas().get(0);

        assertEquals(1, builder.obtenerLineas().size());
        assertEquals(4, l.getCantidad());
        assertEquals(72, builder.obtenerTotalActual());
    }

    @Test
    void eliminarProductoPruebaQuitarLineaDada(){
        builder.agregarProducto(p1, 2);
        builder.agregarProducto(p2, 1);

        builder.eliminarProducto("009");

        assertEquals(1, builder.obtenerLineas().size());
        assertEquals("004", builder.obtenerLineas().get(0).getProducto().getCodigoBarras());
        assertEquals(36, builder.obtenerTotalActual());

    }

    @Test 
    void disminuirCantidadProductoPruebaActualizarSubtotal(){
        builder.agregarProducto(p1, 4);
        builder.disminuirCantidadProducto("004", 1);

        assertEquals(3, builder.obtenerLineas().get(0).getCantidad());
        assertEquals(54, builder.obtenerTotalActual());
    }

    @Test 
    void disminuirCantidadProductoPruebaEliminarLineaSiCero(){
        builder.agregarProducto(p1, 2);
        builder.disminuirCantidadProducto("004", 2);

        assertEquals(0, builder.obtenerLineas().size());
        assertEquals(0.0, builder.obtenerTotalActual());
    }

    @Test
    void disminuirCantidadProductoPruebaSiCantidadNegativa(){
        builder.agregarProducto(p1, 2);
        builder.disminuirCantidadProducto("004", 5);

        assertEquals(0, builder.obtenerLineas().size());
        assertEquals(0.0, builder.obtenerTotalActual());
    }

    @Test
    void establecerPagoPruebaAlmacenarDatos(){
        builder.establecerPago(100, 20, "Tarjeta");

        Venta venta = builder.build();

        assertEquals(100, venta.getMontoRecibido());
        assertEquals(20, venta.getCambio());
        assertEquals("Tarjeta", venta.getMetodoPago());
    }

    @Test 
    void establecerDatosFacturacionPruebaGuardarRFCCorreo(){
        builder.establecerDatosFacturacion("GOMA560315M58", "maria.gonzalez.2024@outlook.com");

        Venta venta = builder.build();

        assertNotNull(venta.getDatosFacturacion());
        assertEquals("GOMA560315M58", venta.getDatosFacturacion().getRfc());
        assertEquals("maria.gonzalez.2024@outlook.com", venta.getDatosFacturacion().getCorreo());
    }

    @Test
    void buildPruebaCrearVentaConCopia(){
        builder.agregarProducto(p1, 2);

        Venta venta = builder.build();

        assertEquals(36.0, venta.getTotal());
        assertEquals(1, venta.getLineas().size());

        venta.getLineas().clear();
        assertEquals(1, builder.obtenerLineas().size());
    }

    @Test
    void buildPruebaGenerarID(){
        Venta venta = builder.build();
        assertEquals(8, venta.getId().length());
    }

    @Test
    void limpiarPruebaReiniciarEstado(){
        builder.agregarProducto(p1, 5);
        builder.establecerPago(100, 10, "Efectivo");
        builder.establecerDatosFacturacion("GOMA560315M58","maria.gonzalez.2024@outlook.com");

        builder.limpiar();

        assertEquals(0, builder.obtenerLineas().size());
        assertEquals(0.0, builder.obtenerTotalActual());

        Venta venta = builder.build();
        assertEquals("PENDIENTE", venta.getMetodoPago());
        assertNull(venta.getDatosFacturacion());
    }
    
}
