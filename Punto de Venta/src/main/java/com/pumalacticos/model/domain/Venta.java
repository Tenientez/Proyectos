package com.pumalacticos.model.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private String id;
    private LocalDateTime fecha; // Fecha y hora exacta
    private List<LineaVenta> lineas;
    
    // Totales monetarios
    private double total;
    private double montoRecibido;
    private double cambio;
    
    private String metodoPago;   // "Efectivo" o "Tarjeta"
    private DatosFacturacion datosFacturacion; // Opcional (puede ser null)

    // Constructor completo
    public Venta(String id, LocalDateTime fecha, List<LineaVenta> lineas, 
                 double total, double montoRecibido, double cambio, 
                 String metodoPago, DatosFacturacion datosFacturacion) {
        this.id = id;
        this.fecha = fecha;
        this.lineas = lineas;
        this.total = total;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.metodoPago = metodoPago;
        this.datosFacturacion = datosFacturacion;
    }

    // --- Getters ---
    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public List<LineaVenta> getLineas() { return lineas; }
    
    // ESTE ES EL IMPORTANTE para los Observers
    public double getTotal() { return total; }
    
    public double getMontoRecibido() { return montoRecibido; }
    public double getCambio() { return cambio; }
    public String getMetodoPago() { return metodoPago; }
    public DatosFacturacion getDatosFacturacion() { return datosFacturacion; }
    
    // toString para debug rápido
    @Override
    public String toString() {
        return "Venta #" + id + " ($" + total + ")";
    }
}