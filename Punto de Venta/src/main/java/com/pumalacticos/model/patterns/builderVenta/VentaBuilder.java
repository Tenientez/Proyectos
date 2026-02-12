package com.pumalacticos.model.patterns.builderVenta;

import com.pumalacticos.model.domain.DatosFacturacion;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VentaBuilder {

    // --- Estado Temporal de la Venta (El Carrito) ---
    private List<LineaVenta> lineas;
    private double totalCalculado;
    private double montoRecibido;
    private double cambio;
    private String metodoPago;
    private DatosFacturacion datosFacturacion;

    public VentaBuilder() {
        this.limpiar();
    }

    /**
     * Reinicia el builder para comenzar una nueva transacción desde cero.
     */
    public void limpiar() {
        this.lineas = new ArrayList<>();
        this.totalCalculado = 0.0;
        this.montoRecibido = 0.0;
        this.cambio = 0.0;
        this.metodoPago = "PENDIENTE";
        this.datosFacturacion = null;
    }

    /**
     * Agrega un producto al carrito.
     * Lógica Inteligente: Si el producto ya existe, solo aumenta la cantidad.
     */
    public void agregarProducto(Producto producto, int cantidad) {
        // 1. Buscar si ya existe en el carrito
        Optional<LineaVenta> lineaExistente = lineas.stream()
                .filter(l -> l.getProducto().getCodigoBarras().equals(producto.getCodigoBarras()))
                .findFirst();

        if (lineaExistente.isPresent()) {
            // Si existe, sumamos a la cantidad actual
            lineaExistente.get().agregarCantidad(cantidad);
        } else {
            // Si no existe, creamos una línea nueva
            lineas.add(new LineaVenta(producto, cantidad));
        }

        // 2. Siempre recalculamos el total después de modificar el carrito
        recalcularTotal();
    }

    /**
     * Elimina un producto completo del carrito por su código.
     */
    public void eliminarProducto(String codigoBarras) {
        lineas.removeIf(l -> l.getProducto().getCodigoBarras().equals(codigoBarras));
        recalcularTotal();
    }

    /**
     * Registra los detalles del pago (esto lo llamará la Facade después de usar la Strategy).
     */
    public void establecerPago(double montoRecibido, double cambioCalculado, String metodoPago) {
        this.montoRecibido = montoRecibido;
        this.cambio = cambioCalculado;
        this.metodoPago = metodoPago;
    }

    /**
     * Opcional: Si el cliente pide factura.
     */
    public void establecerDatosFacturacion(String rfc, String correo) {
        this.datosFacturacion = new DatosFacturacion(rfc, correo);
    }

    /**
     * Suma los subtotales de todas las líneas para tener el total actualizado.
     */
    private void recalcularTotal() {
        this.totalCalculado = lineas.stream()
                .mapToDouble(LineaVenta::getSubtotal)
                .sum();
    }

    // Getters para que la Facade pueda mostrar el estado actual al Frontend
    
    public List<LineaVenta> obtenerLineas() {
        return lineas; // Devolvemos la lista actual para mostrar en la Tabla
    }

    public double obtenerTotalActual() {
        return totalCalculado;
    }

    /**
     * Empaqueta todo en un objeto Venta inmutable.
     */
    public Venta build() {
        // Generamos un ID único (UUID) ya que no tenemos base de datos SQL
        String idUnico = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return new Venta(
            idUnico,
            LocalDateTime.now(),     // Fecha y hora actual
            new ArrayList<>(lineas), // Copia de la lista para seguridad
            totalCalculado,
            montoRecibido,
            cambio,
            metodoPago,
            datosFacturacion
        );
    }

    /**
     * Disminuye la cantidad de un producto específico.
     * Si la cantidad restante es 0 o menos, elimina la línea completa.
     */
    public void disminuirCantidadProducto(String codigoBarras, int cantidadARestar) {
        Optional<LineaVenta> lineaOpt = lineas.stream()
                .filter(l -> l.getProducto().getCodigoBarras().equals(codigoBarras))
                .findFirst();

        if (lineaOpt.isPresent()) {
            LineaVenta linea = lineaOpt.get();
            int nuevaCantidad = linea.getCantidad() - cantidadARestar;

            if (nuevaCantidad > 0) {
                // Si todavía sobran productos, actualizamos la cantidad
                // (La clase LineaVenta recalcula su subtotal automáticamente al hacer set)
                linea.setCantidad(nuevaCantidad);
            } else {
                // Si la resta da 0 o negativo, borramos toda la línea
                eliminarProducto(codigoBarras);
                return; // eliminarProducto ya recalcula el total, así que nos salimos
            }
            
            // Recalculamos el total de la venta
            recalcularTotal();
        }
    }

}