package com.pumalacticos.model.domain;

public class Producto {
    private String codigoBarras; // Este será tu ID único
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;
    private boolean esRestringido; // True = Solo mayores de 18

    public Producto(String codigoBarras, String nombre, double precio, int stock, String categoria, boolean esRestringido) {
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.esRestringido = esRestringido;
    }

    // --- Getters y Setters ---
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isEsRestringido() { return esRestringido; }
    public void setEsRestringido(boolean esRestringido) { this.esRestringido = esRestringido; }

    // toString útil para imprimir en consola o debugging
    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return codigoBarras.equals(producto.codigoBarras);
    }

    @Override
    public int hashCode() {
        return codigoBarras.hashCode();
    }
}