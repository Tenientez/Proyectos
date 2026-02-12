package com.pumalacticos.model.patterns.strategyPagos;

public class PagoTarjetaStrategy implements IPagoStrategy {

    @Override
    public double cobrar(double montoTotal, double montoRecibido) throws Exception {
        // En pago con tarjeta, usualmente ignoramos el montoRecibido
        // o asumimos que se cobra el exacto.
        
        System.out.println("Conectando con el banco...");
        
        // Simulamos una validación básica (podrías agregar lógica de fallo aleatorio si quieres)
        boolean tarjetaAprobada = true; 

        if (!tarjetaAprobada) {
            throw new Exception("Tarjeta Declinada por el banco.");
        }

        System.out.println("Pago con tarjeta aprobado.");

        // En tarjeta no hay cambio, así que retornamos 0.0
        return 0.0;
    }

    @Override
    public String toString() {
        return "Tarjeta de Crédito/Débito";
    }
}