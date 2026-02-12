package com.pumalacticos.model.patterns.strategyPagos;

public interface IPagoStrategy {
    /**
     * Procesa el cobro de la venta.
     * @param montoTotal El total a pagar de la venta.
     * @param montoRecibido El dinero que entrega el cliente (importante para efectivo).
     * @return El cambio a devolver (double). Si es tarjeta, retorna 0.0.
     * @throws Exception Si el pago falla (fondos insuficientes, tarjeta rechazada, etc).
     */
    double cobrar(double montoTotal, double montoRecibido) throws Exception;
}