package com.pumalacticos.model.patterns.facade;

import com.pumalacticos.model.data.DB;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;
import com.pumalacticos.model.patterns.builderVenta.VentaBuilder;
import com.pumalacticos.model.patterns.observer.HistorialObserver;
import com.pumalacticos.model.patterns.observer.IVentaObserver;
import com.pumalacticos.model.patterns.observer.InventarioObserver;
import com.pumalacticos.model.patterns.strategyPagos.IPagoStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PuntoDeVentaFacade {

    // 1. Subsistemas que la Facade administra
    private final VentaBuilder ventaBuilder;
    private final List<IVentaObserver> observers;

    public PuntoDeVentaFacade() {
        // Inicializamos el Builder
        this.ventaBuilder = new VentaBuilder();
        
        // 2. Registramos los Observers automáticamente al iniciar
        this.observers = new ArrayList<>();
        this.observers.add(new InventarioObserver()); // Para restar stock
        this.observers.add(new HistorialObserver());  // Para guardar venta
    }

    /**
     * Reinicia todo para atender a un nuevo cliente.
     */
    public void iniciarNuevaVenta() {
        ventaBuilder.limpiar();
    }

    /**
     * Busca un producto en la 'DB' en memoria y lo agrega al carrito del Builder.
     * @param codigoBarras El código escaneado.
     * @throws Exception Si el producto no existe o no hay stock.
     */
    public void agregarProducto(String codigoBarras, int cantidad) throws Exception {
        // A. Lectura Directa de Datos (Sin DAO)
        Optional<Producto> productoOpt = DB.productos.stream()
                .filter(p -> p.getCodigoBarras().equals(codigoBarras))
                .findFirst();

        if (productoOpt.isEmpty()) {
            throw new Exception("Producto no encontrado: " + codigoBarras);
        }

        Producto producto = productoOpt.get();

        // --- INICIO DE LA CORRECCIÓN DEL BUG DE STOCK ---

        // 1. Verificación de stock general. Si el producto no tiene stock, no se puede agregar.
        if (producto.getStock() <= 0) {
            throw new Exception("No hay más productos en stock para: " + producto.getNombre());
        }
    
        // 2. Ver cuántos de este producto ya están en el carrito actual
        int cantidadEnCarrito = ventaBuilder.obtenerLineas().stream()
                .filter(linea -> linea.getProducto().getCodigoBarras().equals(codigoBarras))
                .mapToInt(LineaVenta::getCantidad)
                .sum();
    
        // 3. Validar si al agregar la nueva cantidad se excede el stock total
        if ((cantidadEnCarrito + cantidad) > producto.getStock()) {
            int disponiblesReales = producto.getStock() - cantidadEnCarrito;
            throw new Exception("Stock insuficiente para '" + producto.getNombre() + "'.\nDisponibles: " + disponiblesReales + ", en carrito ya hay: " + cantidadEnCarrito);
        }
        
        // --- FIN DE LA CORRECCIÓN ---

        // B. Delegar al Builder
        ventaBuilder.agregarProducto(producto, cantidad);
    }

    public void eliminarProducto(String codigoBarras) {
        ventaBuilder.eliminarProducto(codigoBarras);
    }

    /**
     * El método Maestro. Coordina Strategy, Builder y Observers.
     * @return El objeto Venta finalizado (útil para imprimir ticket).
     */
    public Venta cobrar(IPagoStrategy estrategiaPago, double montoRecibido, String rfc, String correo) throws Exception {
        // 1. Obtener total actual del Builder
        double totalAPagar = ventaBuilder.obtenerTotalActual();

        if (totalAPagar <= 0) {
            throw new Exception("El carrito está vacío.");
        }

        // 2. Ejecutar Estrategia de Pago (Valida fondos, calcula cambio)
        double cambio = estrategiaPago.cobrar(totalAPagar, montoRecibido);

        // 3. Configurar datos finales en el Builder
        ventaBuilder.establecerPago(montoRecibido, cambio, estrategiaPago.toString());
        
        if (rfc != null && !rfc.isEmpty()) {
            ventaBuilder.establecerDatosFacturacion(rfc, correo);
        }

        // 4. Construir la Venta Inmutable (Build)
        Venta ventaFinalizada = ventaBuilder.build();

        // 5. Notificar a los Observers (PUSH model)
        notificarObservers(ventaFinalizada);

        // 6. Limpiar el builder para la siguiente venta
        ventaBuilder.limpiar();

        return ventaFinalizada;
    }

    // --- Métodos Auxiliares para la Interfaz Gráfica (Getters) ---

    public double obtenerTotalActual() {
        return ventaBuilder.obtenerTotalActual();
    }

    public List<LineaVenta> obtenerLineasCarrito() {
        return ventaBuilder.obtenerLineas();
    }
    
    // --- Manejo de Observadores ---

    private void notificarObservers(Venta venta) {
        for (IVentaObserver observer : observers) {
            observer.update(venta);
        }
    }

    /**
     * Resta una cantidad específica de un producto en el carrito.
     */
    public void disminuirCantidad(String codigoBarras, int cantidad) {
        ventaBuilder.disminuirCantidadProducto(codigoBarras, cantidad);
    }
    
    /**
     * Obtiene el historial completo de ventas registradas en la base de datos.
     * Para generar reportes y cortes de caja.
     * @return Lista de objetos Venta.
     */
    public List<Venta> obtenerHistorialVentas() {
        // Retornamos la lista estática de la DB. 
        // En un sistema real, aquí haríamos una query "SELECT * FROM ventas"
        return DB.ventas;
    }
}