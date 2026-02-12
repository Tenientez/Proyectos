package com.pumalacticos.model.patterns.builderTicket;

import com.pumalacticos.model.domain.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


import static org.junit.jupiter.api.Assertions.*;

public class TicketBuilderTest {
    private Venta ventaBase;

    @BeforeAll
    static void configurarLocale(){
        Locale.setDefault(Locale.US);
    }

    @BeforeEach
    void setUp(){
        Producto p1 = new Producto("005", "Doritos Nachos", 28, 10, "Frituras", false);
        Producto p2 = new Producto("007", "Power Ade Mora", 58, 20, "Bebidas", false);

        LineaVenta l1 = new LineaVenta(p1, 3);
        LineaVenta l2 = new LineaVenta(p2, 2);

        ventaBase = new Venta("V132", LocalDateTime.of(2025, 11, 22, 16, 35), List.of(l1,l2), 200, 500, 300, "Efectivo", null);

    }

    @Test 
    void construirEncabezadoPrueba(){
        TicketBuilder t = new TicketBuilder(ventaBase);
        t.construirEncabezado();
        String texto = t.obtenerTicket();

        assertTrue(texto.contains("ABARROTES PUMALACTICOS"));
        assertTrue(texto.contains("Folio: V132"));
        assertTrue(texto.contains("Fecha: 22/11/2025 16:35"));

    }

    @Test 
    void construirDetallePrueba(){
        TicketBuilder t = new TicketBuilder(ventaBase);
        t.construirEncabezado();
        t.construirDetalle();
        String texto = t.obtenerTicket();

        assertTrue(texto.contains("Doritos Nachos"));
        assertTrue(texto.contains("x3"));
        assertTrue(texto.contains("$   84.00"));

        assertTrue(texto.contains("Power Ade Mora"));
        assertTrue(texto.contains("x2"));
        assertTrue(texto.contains("$  116.00"));

    }

    @Test
    void nombresLargosSeCortan(){
        Producto pLargo = new Producto("023", "CacahuatesTostadosJaponeses", 75, 5, "Dulces", false);
        LineaVenta l = new LineaVenta(pLargo, 1);

        Venta v = new Venta("023", LocalDateTime.now(), List.of(l), 75, 75, 0, "Efectivo", null);

        TicketBuilder t = new TicketBuilder(v);
        t.construirDetalle();
        String texto = t.obtenerTicket();

        assertTrue(texto.contains("CacahuatesTostado"));
    }

    @Test 
    void construirTotalesPruebaMontos(){
        TicketBuilder t = new TicketBuilder(ventaBase);
        t.construirEncabezado();
        t.construirDetalle();
        t.construirTotales();
        String texto = t.obtenerTicket();

        assertTrue(texto.contains("TOTAL A PAGAR"));
        assertTrue(texto.contains("$   200.00"));
        assertTrue(texto.contains("Metodo Pago"));
        assertTrue(texto.contains("Efectivo"));
        assertTrue(texto.contains("Recibido"));
        assertTrue(texto.contains("$   500.00"));
        assertTrue(texto.contains("Cambio"));
        assertTrue(texto.contains("$   300.00"));

    }

    @Test
    void construirDatosFiscalesPrueba(){
        DatosFacturacion d = new DatosFacturacion("PELR820910H78", "contacto.felipe.lopez@gmail.com");
        Venta v = new Venta("V987", LocalDateTime.now(), ventaBase.getLineas(), 49.5, 50, 0.5, "tarjeta", d);

        TicketBuilder t = new TicketBuilder(v);
        t.construirDatosFiscales();
        String texto = t.obtenerTicket();

        assertTrue(texto.contains(">>> DATOS DE FACTURACIÓN <<<"));
        assertTrue(texto.contains("RFC: PELR820910H78"));
        assertTrue(texto.contains("Correo: contacto.felipe.lopez@gmail.com"));

    }

    @Test 
    void obtenerTicketPruebaTodosLosPasos(){
        TicketBuilder t = new TicketBuilder(ventaBase);
        String ticket = t.obtenerTicket();

        assertTrue(ticket.contains("ABARROTES PUMALACTICOS"));
        assertTrue(ticket.contains("Producto"));
        assertTrue(ticket.contains("TOTAL A PAGAR"));
        assertTrue(ticket.contains("¡Gracias por su compra!"));

    
    }
    
}
