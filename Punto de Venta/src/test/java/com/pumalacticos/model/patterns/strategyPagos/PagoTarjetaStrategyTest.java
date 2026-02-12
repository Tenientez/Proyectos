package com.pumalacticos.model.patterns.strategyPagos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PagoTarjetaStrategyTest {

    @Test 
    void cobrarPruebaRefresarCeroSiempre()throws Exception{
        PagoTarjetaStrategy pago = new PagoTarjetaStrategy();
        double resultado = pago.cobrar(500, 0);

        assertEquals(0.0, resultado);
    }

    @Test
    void cobrarPruebaNoExcepcionSiAprobado(){
        PagoTarjetaStrategy pago = new PagoTarjetaStrategy();

        assertDoesNotThrow(()->{pago.cobrar(300, 100);

        });
    }

    @Test
    void toStringPruebaRegresarNombreCorrecto(){
        PagoTarjetaStrategy pago = new PagoTarjetaStrategy();

        assertEquals("Tarjeta de Crédito/Débito", pago.toString());
    }
    
}
