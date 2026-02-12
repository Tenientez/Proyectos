package com.pumalacticos.model.patterns.observer;

import com.pumalacticos.model.domain.Venta;

public interface IVentaObserver {
    /**
     * Método estándar del patrón Observer.
     * Se llama cuando el Sujeto (la Venta/Facade) notifica un cambio.
     * @param venta El objeto con los datos de la transacción.
     */
    void update(Venta venta);
}