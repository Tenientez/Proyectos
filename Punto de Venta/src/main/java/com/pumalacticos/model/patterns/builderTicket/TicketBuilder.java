package com.pumalacticos.model.patterns.builderTicket;

import com.pumalacticos.model.domain.LineaVenta;
import com.pumalacticos.model.domain.Venta;

import java.time.format.DateTimeFormatter;

public class TicketBuilder {

    private Venta venta;
    private StringBuilder sb; // Usamos StringBuilder para concatenar texto eficientemente

    public TicketBuilder(Venta venta) {
        this.venta = venta;
        this.sb = new StringBuilder();
    }

    /**
     * Paso 1: Construye la cabecera con datos de la tienda y fecha.
     */
    public void construirEncabezado() {
        sb.append("================================\n");
        sb.append("      ABARROTES PUMALACTICOS    \n");
        sb.append("      Facultad de Ciencias      \n");
        sb.append("================================\n");
        sb.append("Folio: ").append(venta.getId()).append("\n");
        
        // Formatear la fecha bonita (ej. 21/11/2025 14:30)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        sb.append("Fecha: ").append(venta.getFecha().format(formatter)).append("\n");
        sb.append("--------------------------------\n");
    }

    /**
     * Paso 2: Construye la lista de productos.
     */
    public void construirDetalle() {
        sb.append(String.format("%-18s %3s %8s\n", "Producto", "Cant", "Total"));
        sb.append("--------------------------------\n");

        for (LineaVenta linea : venta.getLineas()) {
            String nombreCorto = linea.getProducto().getNombre();
            // Si el nombre es muy largo, lo cortamos para que no rompa el ticket
            if (nombreCorto.length() > 18) {
                nombreCorto = nombreCorto.substring(0, 18);
            }

            // Formato de columnas: Nombre a la izq, Cantidad, Subtotal a la der
            sb.append(String.format("%-18s x%-2d $%7.2f\n", 
                    nombreCorto, 
                    linea.getCantidad(), 
                    linea.getSubtotal()));
        }
        sb.append("--------------------------------\n");
    }

    /**
     * Paso 3: Construye los totales financieros.
     */
    public void construirTotales() {
        sb.append(String.format("TOTAL A PAGAR:      $%8.2f\n", venta.getTotal()));
        sb.append(String.format("Metodo Pago: %18s\n", venta.getMetodoPago()));
        sb.append(String.format("Recibido:           $%8.2f\n", venta.getMontoRecibido()));
        sb.append(String.format("Cambio:             $%8.2f\n", venta.getCambio()));
    }

    /**
     * Paso 4 (Opcional): Si hay datos de facturación, los agrega al pie.
     */
    public void construirDatosFiscales() {
        if (venta.getDatosFacturacion() != null) {
            sb.append("--------------------------------\n");
            sb.append("   >>> DATOS DE FACTURACIÓN <<<\n");
            sb.append("RFC: ").append(venta.getDatosFacturacion().getRfc()).append("\n");
            sb.append("Correo: ").append(venta.getDatosFacturacion().getCorreo()).append("\n");
        }
    }

    /**
     * Paso 5: Pie de página y despedida.
     */
    public void construirPie() {
        sb.append("================================\n");
        sb.append("      ¡Gracias por su compra!   \n");
        sb.append("           Vuelva pronto        \n");
        sb.append("================================\n");
    }

    /**
     * Método final que devuelve el Ticket armado como String.
     */
    public String obtenerTicket() {
        // Ejecutamos los pasos en orden (aquí el Builder actúa un poco como Director)
        construirEncabezado();
        construirDetalle();
        construirTotales();
        construirDatosFiscales();
        construirPie();
        
        return sb.toString();
    }
}