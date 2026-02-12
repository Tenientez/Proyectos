package com.pumalacticos.controller;

import com.pumalacticos.OdinApp;
import com.pumalacticos.model.domain.Venta;
import com.pumalacticos.model.patterns.builderTicket.TicketBuilder;
import com.pumalacticos.model.patterns.facade.PuntoDeVentaFacade;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para la vista de reportes (Secondary View).
 * Se encarga de mostrar el historial de ventas y calcular los totales.
 */
public class SecondaryController {

    // Instancia de la fachada para acceder a los datos
    private final PuntoDeVentaFacade facade = new PuntoDeVentaFacade();

    // Elementos de la Interfaz Gráfica
    @FXML private TableView<Venta> tablaHistorial;
    @FXML private TableColumn<Venta, String> colFolio;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colMetodo;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private Label lblGranTotal;

    /**
     * Inicializa el controlador.
     * Configura las columnas de la tabla y carga los datos del historial.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    /**
     * Configura el "Data Binding" de las columnas de la tabla.
     * Define cómo se extrae la información de cada objeto Venta.
     */
    private void configurarTabla() {
        colFolio.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        
        // Formateamos la fecha (ej. "21/11/2025 10:30")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getFecha().format(formatter)
        ));

        colMetodo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMetodoPago()));
        colTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotal()));
    }

    /**
     * Recupera los datos de la Facade, llena la tabla y calcula el gran total.
     */
    private void cargarDatos() {
        List<Venta> historial = facade.obtenerHistorialVentas();
        
        // Llenar tabla
        tablaHistorial.setItems(FXCollections.observableArrayList(historial));

        // Calcular Total de Ingresos usando Java Streams (Programación Funcional)
        // Mejor que un ciclo 'for'.
        double ingresosTotales = historial.stream()
                                          .mapToDouble(Venta::getTotal)
                                          .sum();

        lblGranTotal.setText(String.format("$%.2f", ingresosTotales));
    }

    /**
     * Recupera la venta seleccionada y reconstruye su ticket.
     */
    @FXML
    private void verTicketSeleccionado() {
        // Obtener la venta seleccionada en la tabla
        Venta ventaSeleccionada = tablaHistorial.getSelectionModel().getSelectedItem();

        if (ventaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una venta de la lista para ver su ticket.");
            alert.showAndWait();
            return;
        }

        // Usar el TicketBuilder (REUTILIZACIÓN DE CÓDIGO)
        // Le pasamos la venta histórica y él nos devuelve el String formateado.
        TicketBuilder builder = new TicketBuilder(ventaSeleccionada);
        String ticketTexto = builder.obtenerTicket();

        // Mostrarlo en pantalla
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de Venta");
        alert.setHeaderText("Folio: " + ventaSeleccionada.getId());
        alert.setContentText(ticketTexto);
        alert.getDialogPane().setMinWidth(400); 
        alert.showAndWait();
    }

    /**
     * Regresa a la pantalla de ventas.
     */
    @FXML
    private void switchToPrimary() throws IOException {
        OdinApp.setRoot("primary");
    }
}