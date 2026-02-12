package com.pumalacticos.controller;

import java.io.IOException;
import java.util.Optional; // Importamos nuestro nuevo DAO

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

    // Instancia del DAO para comunicarnos con SQLite
    private final ProductoDAO productoDAO = new ProductoDAO();

    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, Number> colPrecio;
    @FXML
    private TableColumn<Producto, Number> colStock;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigoBarras()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()));
        colStock.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()));

        cargarProductos(); // Método extraído para reutilizarlo
    }

    private void cargarProductos() {
        // Obtenemos la lista fresca desde la base de datos
        tablaProductos.setItems(FXCollections.observableArrayList(productoDAO.obtenerTodos()));
    }

    @FXML
    private void agregarProducto() {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle("Agregar Nuevo Producto");

        ButtonType addButton = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField codigoField = new TextField();
        codigoField.setPromptText("Código de Barras");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");
        TextField precioField = new TextField();
        precioField.setPromptText("Precio");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");

        grid.add(new Label("Código:"), 0, 0);
        grid.add(codigoField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(precioField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(codigoField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String codigo = codigoField.getText();
                    String nombre = nombreField.getText();
                    double precio = Double.parseDouble(precioField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    // Asumimos categoría "General" y sin restricción por defecto
                    return new Producto(codigo, nombre, precio, stock, "General", false);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error de Formato", "Por favor, ingrese valores numéricos válidos.", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Producto> result = dialog.showAndWait();

        result.ifPresent(producto -> {
            // VERIFICACIÓN CON BASE DE DATOS
            Producto existente = productoDAO.buscarPorCodigo(producto.getCodigoBarras());

            if (existente != null) {
                mostrarAlerta("Error", "Ya existe un producto con el código: " + producto.getCodigoBarras(), Alert.AlertType.ERROR);
            } else {
                productoDAO.guardar(producto); // Guardamos en SQLite
                cargarProductos(); // Refrescamos la tabla visual
                mostrarAlerta("Éxito", "Producto guardado correctamente.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void eliminarProducto() {
        Producto selectedProducto = tablaProductos.getSelectionModel().getSelectedItem();
        if (selectedProducto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de eliminar: " + selectedProducto.getNombre() + "?");
            alert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // ELIMINACIÓN EN BASE DE DATOS
                productoDAO.eliminar(selectedProducto.getCodigoBarras());
                cargarProductos(); // Refrescamos la tabla
            }
        } else {
            mostrarAlerta("Ningún Producto Seleccionado", "Por favor, seleccione un producto para eliminar.", Alert.AlertType.WARNING);
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