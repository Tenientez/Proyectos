package com.pumalacticos.model.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatosFacturacionTest {

    @Test 
    void asignarValores(){
        DatosFacturacion d =new DatosFacturacion("GOMA560315M58", "maria.gonzalez.2024@outlook.com");

        assertEquals("GOMA560315M58", d.getRfc());
        assertEquals("maria.gonzalez.2024@outlook.com", d.getCorreo());
    }

    @Test 
    void probarToStringTexto(){
        DatosFacturacion d = new DatosFacturacion("TME231207J48", "javier.m82@gmail.com");
        assertEquals("RFC: TME231207J48 | Correo: javier.m82@gmail.com", d.toString());
    }

    
}
