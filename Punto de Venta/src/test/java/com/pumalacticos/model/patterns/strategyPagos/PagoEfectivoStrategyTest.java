package com.pumalacticos.model.patterns.strategyPagos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PagoEfectivoStrategyTest {
    @Test 
    void cobrarPruebaCalcularCambio()throws Exception{
        PagoEfectivoStrategy pago = new PagoEfectivoStrategy();
        double cambio = pago.cobrar(80,100);

        assertEquals(20, cambio);
    }

    @Test
    void cobrarPruebaRegresarCeroCOnCambioJusto()throws Exception{
        PagoEfectivoStrategy pago = new PagoEfectivoStrategy();
        double cambio = pago.cobrar(50, 50);
        
        assertEquals(0, cambio);
    }

    @Test
    void cobrarPruebaExcepcionSiInsuficienteDinero(){
        PagoEfectivoStrategy pago = new PagoEfectivoStrategy();
        Exception ex = assertThrows(Exception.class, ()->{pago.cobrar(100, 60);

        });

        assertTrue(ex.getMessage().contains("Fondos insuficientes"));
        assertTrue(ex.getMessage().contains("40"));
    }

    @Test
    void toStringPruebaRegresarEfectivo(){
        PagoEfectivoStrategy pago = new PagoEfectivoStrategy();

        assertEquals("Efectivo", pago.toString());
    }
    
}
