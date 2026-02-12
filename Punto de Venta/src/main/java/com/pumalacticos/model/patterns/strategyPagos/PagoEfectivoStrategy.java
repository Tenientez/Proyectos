package com.pumalacticos.model.patterns.strategyPagos;

public class PagoEfectivoStrategy implements IPagoStrategy {

    @Override
    public double cobrar(double montoTotal, double montoRecibido) throws Exception {
        // 1. Validar que el dinero alcance
        if (montoRecibido < montoTotal) {
            throw new Exception("Fondos insuficientes. Faltan: $" + (montoTotal - montoRecibido));
        }

        // 2. Calcular el cambio
        double cambio = montoRecibido - montoTotal;

        // 3. Retornar el cambio
        return cambio;
    }
    
    // Sobreescribimos toString para que se vea bonito en la interfaz si lo necesitas
    @Override
    public String toString() {
        return "Efectivo";
    }
}