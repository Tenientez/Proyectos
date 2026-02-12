package com.pumalacticos.controller;

import java.io.IOException;
import java.util.Optional;

import com.pumalacticos.OdinApp;
import com.pumalacticos.model.data.dao.ProductoDAO;
import com.pumalacticos.model.domain.Producto;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ProductosController {

    private final ProductoDAO productoDAO = new ProductoDAO();

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Number> colPrecio;
    @FXML private TableColumn<Producto, Number> colStock;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigoBarras()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()));
        colStock.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()));

        cargarProductos();
    }

    private void cargarProductos() {
        // Imprimimos en consola para verificar que la BD trae el dato correcto (Depuración)
        var listaActualizada = productoDAO.obtenerTodos();
        System.out.println("Recargando Tabla");
        System.out.println("Productos encontrados: " + listaActualizada.size());
        
        // Cargamos la nueva lista en la tabla
        tablaProductos.setItems(FXCollections.observableArrayList(listaActualizada));
        
        //Forzamos a la tabla a "repintar" cada celda visualmente
        tablaProductos.refresh(); 
    }

    // --- NUEVO MÉTODO: EDITAR ---
    @FXML
    private void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un producto para editar.", Alert.AlertType.WARNING);
            return;
        }

        // Reutilizamos la lógica del diálogo, pasando el producto existente
        mostrarDialogoProducto(seleccionado); 
    }

    // --- MÉTODO ACTUALIZADO: AGREGAR ---
    @FXML
    private void agregarProducto() {
        // Pasamos 'null' para indicar que es un producto nuevo
        mostrarDialogoProducto(null);
    }

    /**
     * Método maestro para mostrar el formulario.
     * Sirve tanto para CREAR como para EDITAR.
     */
    private void mostrarDialogoProducto(Producto productoAEditar) {
        Dialog<Producto> dialog = new Dialog<>();
        boolean esEdicion = (productoAEditar != null);
        
        dialog.setTitle(esEdicion ? "Editar Producto" : "Agregar Nuevo Producto");

        ButtonType actionButtonType = new ButtonType(esEdicion ? "Actualizar" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        // Campos del formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField codigoField = new TextField();
        TextField nombreField = new TextField();
        TextField precioField = new TextField();
        TextField stockField = new TextField();

        // Configuración visual
        codigoField.setPromptText("Código de Barras");
        nombreField.setPromptText("Nombre del Producto");
        precioField.setPromptText("0.00");
        stockField.setPromptText("0");

        grid.add(new Label("Código:"), 0, 0); grid.add(codigoField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1); grid.add(nombreField, 1, 1);
        grid.add(new Label("Precio:"), 0, 2); grid.add(precioField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3); grid.add(stockField, 1, 3);

        // --- LÓGICA DE PRE-LLENADO ---
        if (esEdicion) {
            // Rellenamos los campos con los datos actuales
            codigoField.setText(productoAEditar.getCodigoBarras());
            nombreField.setText(productoAEditar.getNombre());
            precioField.setText(String.valueOf(productoAEditar.getPrecio()));
            stockField.setText(String.valueOf(productoAEditar.getStock()));
            
            // IMPORTANTE: Bloqueamos el código de barras. No se debe editar la llave primaria.
            codigoField.setDisable(true);
        }

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> (esEdicion ? nombreField : codigoField).requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == actionButtonType) {
                try {
                    String codigo = codigoField.getText();
                    String nombre = nombreField.getText();
                    double precio = Double.parseDouble(precioField.getText());
                    int stock = Integer.parseInt(stockField.getText());

                    // Mantenemos la categoría y restricción originales si editamos, o default si es nuevo
                    String cat = esEdicion ? productoAEditar.getCategoria() : "General";
                    boolean restringido = esEdicion ? productoAEditar.isEsRestringido() : false;

                    return new Producto(codigo, nombre, precio, stock, cat, restringido);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "Precio y Stock deben ser números.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Producto> result = dialog.showAndWait();

        result.ifPresent(productoResultado -> {
            if (esEdicion) {
                // ACTUALIZAR
                productoDAO.actualizar(productoResultado);
                mostrarAlerta("Éxito", "Producto actualizado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                // GUARDAR NUEVO
                if (productoDAO.buscarPorCodigo(productoResultado.getCodigoBarras()) != null) {
                    mostrarAlerta("Error", "El código ya existe.", Alert.AlertType.ERROR);
                } else {
                    productoDAO.guardar(productoResultado);
                    mostrarAlerta("Éxito", "Producto guardado.", Alert.AlertType.INFORMATION);
                }
            }
            cargarProductos(); // Refrescar tabla
        });
    }

    @FXML
    private void eliminarProducto() {
        Producto selectedProducto = tablaProductos.getSelectionModel().getSelectedItem();
        if (selectedProducto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar " + selectedProducto.getNombre() + "?");
            alert.setContentText("Esta acción es irreversible.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                productoDAO.eliminar(selectedProducto.getCodigoBarras());
                cargarProductos();
            }
        } else {
            mostrarAlerta("Selección requerida", "Selecciona un producto para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void irAPrincipal() throws IOException {
        OdinApp.setRoot("primary");
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}