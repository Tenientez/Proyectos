package com.pumalacticos.model.patterns.builderVenta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pumalacticos.model.domain.DatosFacturacion;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;

public class VentaBuilder {

    private List<LineaVenta> lineas;
    private double totalCalculado;
    private double montoRecibido;
    private double cambio;
    private String metodoPago;
    private DatosFacturacion datosFacturacion;

    public VentaBuilder() {
        this.limpiar();
    }

    public void limpiar() {
        this.lineas = new ArrayList<>();
        this.totalCalculado = 0.0;
        this.montoRecibido = 0.0;
        this.cambio = 0.0;
        this.metodoPago = "PENDIENTE";
        this.datosFacturacion = null;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        Optional<LineaVenta> lineaExistente = lineas.stream()
                .filter(l -> l.getProducto().getCodigoBarras().equals(producto.getCodigoBarras()))
                .findFirst();

        if (lineaExistente.isPresent()) {
            lineaExistente.get().agregarCantidad(cantidad);
        } else {
            lineas.add(new LineaVenta(producto, cantidad));
        }
        recalcularTotal();
    }

    public void eliminarProducto(String codigoBarras) {
        lineas.removeIf(l -> l.getProducto().getCodigoBarras().equals(codigoBarras));
        recalcularTotal();
    }

    public void establecerPago(double montoRecibido, double cambioCalculado, String metodoPago) {
        this.montoRecibido = montoRecibido;
        this.cambio = cambioCalculado;
        this.metodoPago = metodoPago;
    }

    public void establecerDatosFacturacion(String rfc, String correo) {
        this.datosFacturacion = new DatosFacturacion(rfc, correo);
    }

    private void recalcularTotal() {
        this.totalCalculado = lineas.stream()
                .mapToDouble(LineaVenta::getSubtotal)
                .sum();
    }

    public List<LineaVenta> obtenerLineas() {
        return lineas;
    }

    public double obtenerTotalActual() {
        return totalCalculado;
    }

    public void disminuirCantidadProducto(String codigoBarras, int cantidadARestar) {
        Optional<LineaVenta> lineaOpt = lineas.stream()
                .filter(l -> l.getProducto().getCodigoBarras().equals(codigoBarras))
                .findFirst();

        if (lineaOpt.isPresent()) {
            LineaVenta linea = lineaOpt.get();
            int nuevaCantidad = linea.getCantidad() - cantidadARestar;

            if (nuevaCantidad > 0) {
                linea.setCantidad(nuevaCantidad);
            } else {
                eliminarProducto(codigoBarras);
                return;
            }
            recalcularTotal();
        }
    }

    public Venta build() {
        LocalDateTime ahora = LocalDateTime.now();
        // FORMATO SOLICITADO: ddmmaaaahhmmss
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        String idFolio = ahora.format(fmt);

        return new Venta(
            idFolio,
            ahora,
            new ArrayList<>(lineas),
            totalCalculado,
            montoRecibido,
            cambio,
            metodoPago,
            datosFacturacion
        );
    }
}