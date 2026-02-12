package com.pumalacticos.controller;

import com.pumalacticos.OdinApp;
import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Venta;
import com.pumalacticos.model.patterns.facade.PuntoDeVentaFacade;
import com.pumalacticos.model.patterns.strategyPagos.PagoEfectivoStrategy;
import com.pumalacticos.model.patterns.strategyPagos.PagoTarjetaStrategy;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador de la vista principal de ventas (Primary View).
 * Actúa como intermediario entre la GUI (JavaFX) y la Lógica de Negocio (Facade).
 * * <p>Aplica el principio de Separación de Intereses: la vista solo se encarga de mostrar,
 * el controlador coordina los eventos del usuario y el modelo ejecuta la lógica pesada.</p>
 */
public class PrimaryController {

    /**
     * Instancia de la Facade (Patrón Facade).
     * Mantiene el estado de la venta actual y simplifica la interacción con los subsistemas
     * (Inventario, Ventas, Pagos, etc.) a través de una única interfaz.
     */
    private final PuntoDeVentaFacade facade = new PuntoDeVentaFacade();

    // Elementos de la GUI inyectados por @FXML 

    // Estos nombres DEBEN coincidir exactamente con los fx:id del archivo FXML
    @FXML private TextField txtCodigo;
    @FXML private TableView<LineaVenta> tablaVentas;
    @FXML private TableColumn<LineaVenta, String> colProducto;
    @FXML private TableColumn<LineaVenta, Integer> colCantidad;
    @FXML private TableColumn<LineaVenta, Double> colPrecioU;
    @FXML private TableColumn<LineaVenta, Double> colSubtotal;
    @FXML private Label lblTotal;

    /**
     * Método inicializador de JavaFX. 
     * Se ejecuta automáticamente justo después de cargar el archivo FXML.
     * Configuramos cómo se comportan las columnas de la tabla y limpiamos el estado inicial.
     */
    @FXML
    public void initialize() {
        // Configurar columnas de la tabla (Binding de datos)
        // Usamos expresiones lambda para decirle a la columna qué dato del objeto LineaVenta debe mostrar.
        colProducto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCantidad()));
        colPrecioU.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProducto().getPrecio()));
        colSubtotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getSubtotal()));

        // Aseguramos que la fachada inicie limpia (carrito vacío) al abrir la ventana
        facade.iniciarNuevaVenta();
        actualizarVista();
    }

    /**
     * Maneja el evento de "Enter" en el campo de texto.
     * Ideal para el uso de pistolas de código de barras que envían un carácter de 'Nueva Línea' al final.
     */
    @FXML
    private void agregarProductoPorEnter() {
        agregarProducto();
    }

    /**
     * Maneja el clic en el botón "Agregar".
     */
    @FXML
    private void agregarProductoPorBoton() {
        agregarProducto();
    }

    /**
    Elimina el producto que el usuario seleccionó en la tabla.
        */
   @FXML
    private void eliminarProductoSeleccionado() {
        LineaVenta seleccion = tablaVentas.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            mostrarAlerta("Aviso", "Selecciona un producto de la tabla para eliminarlo.", Alert.AlertType.WARNING);
            return;
        }

        String codigo = seleccion.getProducto().getCodigoBarras();
        int cantidadActual = seleccion.getCantidad();

        // CASO 1: Solo hay uno, o el usuario quiere borrar todo de golpe
        if (cantidadActual == 1) {
            facade.eliminarProducto(codigo);
        } 
        // CASO 2: Hay múltiples items (< 1), preguntamos cuántos quiere quitar
        else {
            // Usamos un diálogo para preguntar la cantidad
            TextInputDialog dialog = new TextInputDialog("1"); // Por defecto sugerimos quitar 1
            dialog.setTitle("Reducir Cantidad");
            dialog.setHeaderText("Producto: " + seleccion.getProducto().getNombre());
            dialog.setContentText("Tienes " + cantidadActual + " unidades.\n¿Cuántas deseas quitar?");

            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                try {
                    int cantidadAQuitar = Integer.parseInt(result.get());

                    if (cantidadAQuitar >= cantidadActual) {
                        // Si quiere quitar todo (o más), borramos la línea completa
                        facade.eliminarProducto(codigo);
                    } else if (cantidadAQuitar > 0) {
                        // Si es una resta parcial (ej. quitar 1 de 6)
                        facade.disminuirCantidad(codigo, cantidadAQuitar);
                    } else {
                        mostrarAlerta("Error", "La cantidad debe ser mayor a 0", Alert.AlertType.ERROR);
                    }
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "Ingresa un número válido.", Alert.AlertType.ERROR);
                }
            } else {
                return; // El usuario canceló el diálogo, no hacemos nada
            }
        }

        actualizarVista();
        txtCodigo.requestFocus();
    }

    /**
     * Lógica central para agregar productos al carrito.
     * Obtiene el texto, llama a la fachada para procesarlo y actualiza la interfaz.
     */
    private void agregarProducto() {
        String codigo = txtCodigo.getText().trim();
        
        // Validación básica: no procesar si está vacío
        if (codigo.isEmpty()) return;

        try {
            // Delegamos a la Facade la lógica de negocio.
            // Ella sabrá internamente si hay stock, si el producto existe, etc.
            // Por defecto agregamos 1 unidad cada vez que se escanea.
            facade.agregarProducto(codigo, 1); 
            
            // Limpiamos el campo para el siguiente escaneo rápido
            txtCodigo.clear();
            txtCodigo.requestFocus(); // Regresamos el foco para seguir escaneando sin usar el mouse
            tablaVentas.refresh(); 
            // Refrescamos la tabla y los totales
            actualizarVista();

        } catch (Exception e) {
            // Si el producto no existe o no hay stock, la Facade lanzará una excepción.
            // Aquí la atrapamos y mostramos una alerta amigable al usuario.
            mostrarAlerta("Aviso", e.getMessage(), Alert.AlertType.WARNING);
            txtCodigo.selectAll();
        }
    }

    /**
     * Ejecuta el cobro usando la Estrategia de Efectivo.
     * Invoca al Patrón Strategy a través de la Facade.
     */
    @FXML
    private void pagarEfectivo() {
        procesarPago(true);
    }

    /**
     * Ejecuta el cobro usando la Estrategia de Tarjeta.
     * Invoca al Patrón Strategy a través de la Facade.
     */
    @FXML
    private void pagarTarjeta() {
        procesarPago(false);
    }

    /**
     * Método auxiliar para manejar el flujo de pago y evitar duplicidad de código.
     * Utiliza el Patrón Strategy para definir el comportamiento del cobro.
     * * @param esEfectivo true si se desea pagar con efectivo, false para tarjeta.
     */
    private void procesarPago(boolean esEfectivo) {
        try {
            // Validar que haya algo que cobrar
            double total = facade.obtenerTotalActual();
            if (total <= 0) {
                mostrarAlerta("Carrito vacío", "No hay productos para cobrar.", Alert.AlertType.WARNING);
                return;
            }

            double recibido = total; // Valor por defecto (para tarjeta)
            
            // Si es efectivo, necesitamos pedir cuánto dinero entrega el cliente
            if (esEfectivo) {
                String input = mostrarInput("Cobro en Efectivo", 
                        "Total a pagar: $" + String.format("%.2f", total) + "\nIngrese monto recibido:");
                if (input == null) return; // El usuario canceló el diálogo
                recibido = Double.parseDouble(input);
            }

            // USO DEL PATRÓN STRATEGY
            // Aquí instanciamos la estrategia concreta y se la pasamos a la Facade.
            // La Facade no sabe los detalles del cobro, solo delega a la estrategia recibida.
            Venta venta;
            if (esEfectivo) {
                venta = facade.cobrar(new PagoEfectivoStrategy(), recibido, null, null);
            } else {
                venta = facade.cobrar(new PagoTarjetaStrategy(), total, null, null);
            }
            
            // Si se llega aquí, el pago fue exitoso (si no, la Facade lanza excepción)
            mostrarTicket(venta);
            
            // Preparamos la interfaz para el siguiente cliente
            reiniciarInterfaz();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto debe ser un número válido.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            // Captura errores de negocio (ej. Fondos insuficientes o tarjeta declinada)
            mostrarAlerta("Error en el pago", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Cancela la venta actual, limpia el carrito y resetea la interfaz.
     */
    @FXML
    private void cancelarVenta() {
        facade.iniciarNuevaVenta();
        actualizarVista();
        txtCodigo.requestFocus();
    }

    /**
     * Navega a la vista secundaria (Reportes).
     * @throws IOException si no se puede cargar el archivo FXML.
     */
    @FXML
    private void irAReportes() throws IOException {
        OdinApp.setRoot("secondary");
    }

    @FXML
    private void irAProductos() throws IOException {
        OdinApp.setRoot("productos");
    }

    // Métodos Auxiliares Privados (Helpers UI)

    /**
     * Sincroniza la tabla y la etiqueta de total con el estado actual del Builder.
     * Incluye un refresh forzado para visualizar cambios en cantidades.
     */
    private void actualizarVista() {
        // Actualizamos la lista de items
        tablaVentas.setItems(FXCollections.observableArrayList(facade.obtenerLineasCarrito()));
        
        // Forzamos a la tabla a "repintar" cada celda. 
        // Esto hace que lea de nuevo la cantidad ya sea al eliminar o agregar productos.
        tablaVentas.refresh(); 

        // Actualizamos el total monetario
        lblTotal.setText(String.format("$%.2f", facade.obtenerTotalActual()));
    }

    private void reiniciarInterfaz() {
        // No necesitamos llamar a iniciarNuevaVenta() aquí porque la Facade ya lo hizo al finalizar el cobro.
        actualizarVista();
        txtCodigo.requestFocus();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private String mostrarInput(String titulo, String contenido) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(contenido);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    /**
     * Muestra el ticket generado utilizando el Patrón Builder.
     * @param venta El objeto venta finalizado.
     */
    private void mostrarTicket(Venta venta) {
        // Usamos el TicketBuilder existente para generar el String del ticket
        com.pumalacticos.model.patterns.builderTicket.TicketBuilder tb = 
            new com.pumalacticos.model.patterns.builderTicket.TicketBuilder(venta);
        
        String ticket = tb.obtenerTicket();
        
        // Mostramos el ticket en un cuadro de diálogo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket de Compra");
        alert.setHeaderText("Venta Finalizada con Éxito");
        alert.setContentText(ticket);
        alert.getDialogPane().setMinWidth(400); 
        alert.showAndWait();
    }
}