package com.pumalacticos.model.data.dao;

import java.util.List;

import com.pumalacticos.model.domain.Producto;

/**
 * Interfaz que define las operaciones disponibles para la gestión de Productos.
 * Esto permite desacoplar la lógica de negocio de la implementación específica de la base de datos.
 */
public interface IProductoDAO {
    // CRUD: Create, Read, Update, Delete
    
    // Guardar un nuevo producto
    void guardar(Producto producto);
    
    // Obtener un producto por su código de barras (ID)
    Producto buscarPorCodigo(String codigoBarras);
    
    // Obtener todos los productos (para llenar la tabla de la vista)
    List<Producto> obtenerTodos();
    
    // Actualizar un producto existente
    void actualizar(Producto producto);
    
    // Eliminar un producto
    void eliminar(String codigoBarras);
}