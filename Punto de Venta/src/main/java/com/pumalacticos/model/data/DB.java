package com.pumalacticos.model.data;

import com.pumalacticos.model.domain.Producto;
import com.pumalacticos.model.domain.Venta;

import java.util.ArrayList;
import java.util.List;

public class DB {

    // Estas listas actúan como "TABLAS" de SQL
    public static List<Producto> productos = new ArrayList<>();
    public static List<Venta> ventas = new ArrayList<>();

    // Bloque estático: Se ejecuta solo al iniciar la app para cargar datos de prueba
    static {
        productos.add(new Producto("001", "Leche Santa Clara 1L", 28.00, 50, "Lacteos", false));
        productos.add(new Producto("002", "Coca Cola 600ml", 18.50, 100, "Bebidas", false));
        productos.add(new Producto("003", "Cerveza Victoria Latón", 22.00, 24, "Alcohol", true));
        productos.add(new Producto("004", "Papas Sabritas Sal", 17.00, 30, "Botanas", false));
        productos.add(new Producto("005", "Cigarros Marlboro", 75.00, 20, "Tabaco", true));
        productos.add(new Producto("006", "Galletas Emperador", 15.00, 40, "Galletas", false));
        productos.add(new Producto("793573264107","Skarch 1.5L",13, 4, "Bebidas",false));
        productos.add(new Producto("7501011121997", "Stax Crema y Cebolla", 65,5,"Botanas",false));
    }
}