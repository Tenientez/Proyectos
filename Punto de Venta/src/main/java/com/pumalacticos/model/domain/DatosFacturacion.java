package com.pumalacticos.model.domain;

public class DatosFacturacion {
    private String rfc;
    private String correo;

    public DatosFacturacion(String rfc, String correo) {
        this.rfc = rfc;
        this.correo = correo;
    }

    // Getters
    public String getRfc() { return rfc; }
    public String getCorreo() { return correo; }
    
    @Override
    public String toString() {
        return "RFC: " + rfc + " | Correo: " + correo;
    }
}