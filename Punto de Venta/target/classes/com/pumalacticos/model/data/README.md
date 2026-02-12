# Módulo de Persistencia de Datos (Data Layer)

## Descripción General
Este paquete gestiona la capa de acceso a datos del sistema **OdinApp (Punto de Venta)**. Su responsabilidad principal es garantizar la persistencia de la información crítica del negocio (Inventario y Ventas) más allá del ciclo de vida de ejecución de la aplicación.

## Evolución de la Arquitectura: De Memoria a SQLite

### Estado Anterior (Legacy)
Originalmente, el sistema utilizaba un almacenamiento volátil basado en estructuras de datos en memoria (`static List<Producto>` en la clase `DB.java`).
* **Limitación:** La información se perdía al cerrar la aplicación.
* **Uso actual:** Se mantiene `DB.java` parcialmente como referencia histórica o caché temporal, pero ya no es la fuente de la verdad.

### Estado Actual (Persistencia Robusta)
Se ha migrado a una base de datos relacional embebida (**SQLite**) implementando una arquitectura profesional de acceso a datos.

**Ventajas de la nueva implementación:**
1.  **Persistencia:** Los datos de ventas, clientes e inventario se almacenan permanentemente en disco (`punto_venta.db`).
2.  **Integridad Transaccional:** Uso de transacciones SQL para asegurar que las ventas complejas (cabecera + detalles) se guarden completamente o no se guarden en absoluto, evitando datos corruptos.
3.  **Escalabilidad:** Permite manejar miles de registros sin saturar la memoria RAM del equipo.

---

## 🛠 Patrones de Diseño Implementados

Para esta migración, se han aplicado estrictamente patrones de diseño de software para mantener el código limpio y desacoplado:

### 1. Patrón DAO (Data Access Object)
Separa la lógica de negocio de los detalles de la base de datos.
* **Interfaces (`IProductoDAO`, `IVentaDAO`):** Definen el contrato de operaciones (CRUD).
* **Implementaciones (`ProductoDAO`, `VentaDAO`):** Contienen el código SQL específico (INSERT, SELECT, UPDATE).
* **Beneficio:** Si en el futuro decidimos cambiar SQLite por MySQL o PostgreSQL, solo modificamos estas clases sin tocar la interfaz gráfica ni los controladores.

### 2. Patrón Singleton
Aplicado en la clase `ConexionDB`.
* **Objetivo:** Garantizar que exista una **única instancia** de la conexión a la base de datos durante toda la ejecución.
* **Beneficio:** Optimiza el uso de recursos y previene conflictos de acceso al archivo de base de datos.

---

## Estructura de Archivos

* **`ConexionDB.java`**: Gestiona la conexión JDBC con el driver de SQLite.
* **`dao/`**: Paquete que contiene las interfaces e implementaciones de acceso a datos.
* **`punto_venta.db`**: Archivo binario (generado automáticamente) que contiene las tablas:
    * `productos`
    * `ventas`
    * `detalles_venta`

## Notas para Desarrolladores

* **Autoconfiguración:** El sistema es "Serverless" y "Zero-Config". Si el archivo `.db` no existe, las clases DAO ejecutarán automáticamente los scripts `CREATE TABLE IF NOT EXISTS` al iniciar.
* **Visualización:** Para inspeccionar los datos crudos, se recomienda utilizar la extensión *SQLite Viewer* en VS Code o *DB Browser for SQLite*.