package com.pumalacticos.model.patterns.facade;

import java.util.ArrayList; // Mantenemos DB solo para historial de ventas por ahora
import java.util.List; // Importante: Nuevo DAO

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.data.dao.ProductoDAO;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;
import com.pumalacticos.model.patterns.builderVenta.VentaBuilder;
import com.pumalacticos.model.patterns.observer.HistorialObserver;
import com.pumalacticos.model.patterns.observer.IVentaObserver;
import com.pumalacticos.model.patterns.observer.InventarioObserver;
import com.pumalacticos.model.patterns.strategyPagos.IPagoStrategy;

public class PuntoDeVentaFacade {

    private final VentaBuilder ventaBuilder;
    private final List<IVentaObserver> observers;
    private final ProductoDAO productoDAO; // Instancia del DAO

    public PuntoDeVentaFacade() {
        this.ventaBuilder = new VentaBuilder();
        this.productoDAO = new ProductoDAO(); // Inicializamos la conexión
        
        this.observers = new ArrayList<>();
        this.observers.add(new InventarioObserver()); 
        this.observers.add(new HistorialObserver());
    }

    public void iniciarNuevaVenta() {
        ventaBuilder.limpiar();
    }

    public void agregarProducto(String codigoBarras, int cantidad) throws Exception {
        // 1. BUSCAR EN BASE DE DATOS (SQLITE)
        Producto producto = productoDAO.buscarPorCodigo(codigoBarras);

        if (producto == null) {
            throw new Exception("Producto no encontrado en base de datos: " + codigoBarras);
        }

        // 2. Verificación de stock (Usando el dato real de la DB)
        if (producto.getStock() <= 0) {
            throw new Exception("Agotado: " + producto.getNombre());
        }
    
        // 3. Verificar carrito actual
        int cantidadEnCarrito = ventaBuilder.obtenerLineas().stream()
                .filter(linea -> linea.getProducto().getCodigoBarras().equals(codigoBarras))
                .mapToInt(LineaVenta::getCantidad)
                .sum();
    
        if ((cantidadEnCarrito + cantidad) > producto.getStock()) {
            int disponibles = producto.getStock() - cantidadEnCarrito;
            throw new Exception("Stock insuficiente. Disponibles: " + disponibles);
        }
        
        ventaBuilder.agregarProducto(producto, cantidad);
    }

    public void eliminarProducto(String codigoBarras) {
        ventaBuilder.eliminarProducto(codigoBarras);
    }

    public Venta cobrar(IPagoStrategy estrategiaPago, double montoRecibido, String rfc, String correo) throws Exception {
        double totalAPagar = ventaBuilder.obtenerTotalActual();

        if (totalAPagar <= 0) {
            throw new Exception("El carrito está vacío.");
        }

        double cambio = estrategiaPago.cobrar(totalAPagar, montoRecibido);

        ventaBuilder.establecerPago(montoRecibido, cambio, estrategiaPago.toString());
        
        if (rfc != null && !rfc.isEmpty()) {
            ventaBuilder.establecerDatosFacturacion(rfc, correo);
        }

        Venta ventaFinalizada = ventaBuilder.build();

        notificarObservers(ventaFinalizada);
        ventaBuilder.limpiar();

        return ventaFinalizada;
    }

    public double obtenerTotalActual() {
        return ventaBuilder.obtenerTotalActual();
    }

    public List<LineaVenta> obtenerLineasCarrito() {
        return ventaBuilder.obtenerLineas();
    }
    
    private void notificarObservers(Venta venta) {
        for (IVentaObserver observer : observers) {
            observer.update(venta);
        }
    }

    public void disminuirCantidad(String codigoBarras, int cantidad) {
        ventaBuilder.disminuirCantidadProducto(codigoBarras, cantidad);
    }
    
    public List<Venta> obtenerHistorialVentas() {
        // Por ahora seguimos usando la lista en memoria para Ventas
        // Más adelante crearemos VentaDAO
        return DB.ventas;
    }
}