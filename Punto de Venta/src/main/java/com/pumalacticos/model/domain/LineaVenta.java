package com.pumalacticos.model.domain;

public class LineaVenta {
    private Producto producto;
    private int cantidad;
    private double subtotal; // Se calcula solo: precio * cantidad

    public LineaVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.actualizarSubtotal();
    }

    /**
     * Método útil para el Builder: si vuelves a escanear el mismo producto,
     * solo sumamos la cantidad en lugar de crear una línea nueva.
     */
    public void agregarCantidad(int cantidadExtra) {
        this.cantidad += cantidadExtra;
        this.actualizarSubtotal();
    }

    private void actualizarSubtotal() {
        this.subtotal = this.producto.getPrecio() * this.cantidad;
    }

    // --- Getters ---
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
    
    // Setters (por si acaso necesitamos editar manualmente en la tabla)
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.actualizarSubtotal();
    }
}