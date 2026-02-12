package com.pumalacticos.model.patterns.facade;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.*;
import com.pumalacticos.model.patterns.strategyPagos.IPagoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PuntoDeVentaFacadeTest{

    private PuntoDeVentaFacade facade;

    private Producto p1;
    private Producto p2;

    @BeforeEach
    void setUp(){
        DB.productos.clear();
        DB.ventas.clear();

        p1 = new Producto("002", "Gansito", 23, 10, "Dulce", false);
        p2 = new Producto("001", "Coca-Cola", 18.5, 5, "Bebidas", false);

        DB.productos.addAll(List.of(p1,p2));

        facade = new PuntoDeVentaFacade();
    }

    @Test
    void agregarProductosPruebaAgregarAlCarro() throws Exception{
        facade.agregarProducto("001", 2);

        List<LineaVenta> l = facade.obtenerLineasCarrito();

        assertEquals(1, l.size());
        assertEquals(2, l.get(0).getCantidad());
        assertEquals(37, facade.obtenerTotalActual());
    }

    @Test
    void agregarProductoPruebaExcepcion(){
        Exception ex = assertThrows(Exception.class, ()->{facade.agregarProducto("999", 1);

        });

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void eliminarProductoPruebaQuitarCosaDelCarrito() throws Exception{
        facade.agregarProducto("001", 2);
        facade.agregarProducto("002", 1);

        facade.eliminarProducto("002");
        List<LineaVenta> lineas = facade.obtenerLineasCarrito();
        
        assertEquals(1, lineas.size());
        assertEquals("001", lineas.get(0).getProducto().getCodigoBarras());
    }

    @Test
    void disminuirCantidadPruebaRestar()throws Exception{
        facade.agregarProducto("001", 3);
        facade.disminuirCantidad("001", 1);

        LineaVenta l = facade.obtenerLineasCarrito().get(0);

        assertEquals(2, l.getCantidad());
        assertEquals(37.0, facade.obtenerTotalActual());
    }

    @Test
    void disminuirCantidadPruebaEliminarLineaSiCero() throws Exception{
        facade.agregarProducto("001", 2);
        facade.disminuirCantidad("001", 2);

        assertEquals(0, facade.obtenerLineasCarrito().size());
        assertEquals(0.0, facade.obtenerTotalActual());
    }

    @Test
    void cobrarPruebaExcepcionSiCarroVacio(){
        IPagoStrategy mockPago = mock(IPagoStrategy.class);

        Exception ex = assertThrows(Exception.class, ()->{facade.cobrar(mockPago, 100, null, null);

        });
        assertTrue(ex.getMessage().contains("carrito está vacío"));
    }

    @Test
    void cobrarPruebaEstrategiaPagoYGuardarVenta() throws Exception{
        facade.agregarProducto("001", 2);
        IPagoStrategy mockPago = mock(IPagoStrategy.class);
        when(mockPago.cobrar(37, 50)).thenReturn(13.0);
        when(mockPago.toString()).thenReturn("MockPago");

        Venta venta = facade.cobrar(mockPago, 50, null, null);
        assertEquals(37, venta.getTotal());
        assertEquals(50, venta.getMontoRecibido());
        assertEquals(13, venta.getCambio());

        assertEquals(1, DB.ventas.size());
    }

    @Test
    void cobrarPruebaAsignarDatosFacturaSiDados() throws Exception{
        facade.agregarProducto("001", 2);

        IPagoStrategy mockPago = mock(IPagoStrategy.class);
        when(mockPago.cobrar(37,50)).thenReturn(13.0);
        when(mockPago.toString()).thenReturn("PagoTarjeta");

        Venta venta = facade.cobrar(mockPago, 50, "GOMA560315M58", "maria.gonzalez.2024@outlook.com");

        assertNotNull(venta.getDatosFacturacion());
        assertEquals("GOMA560315M58", venta.getDatosFacturacion().getRfc());
    }

    @Test
    void iniciarNuevaVentaPruebaLimpiarBuilder()throws Exception{
        facade.agregarProducto("001", 2);
        facade.iniciarNuevaVenta();

        assertEquals(0, facade.obtenerLineasCarrito().size());
        assertEquals(0.0, facade.obtenerTotalActual());
    }

    @Test
    void obtenerHistorialVentasPruebaLeerDB(){
        DB.ventas.add(mock(Venta.class));
        assertEquals(1, facade.obtenerHistorialVentas().size());
    }
    
}