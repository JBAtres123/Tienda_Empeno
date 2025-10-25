package com.online.tienda_empeno.dto;

public class CambioContraseniaDTO {
    private String contraseniaActual;
    private String contraseniaNueva;

    public String getContraseniaActual() {
        return contraseniaActual;
    }

    public void setContraseñaActual(String contraseniaActual) {
        this.contraseniaActual = contraseniaActual;
    }

    public String getContraseñaNueva() {
        return contraseniaNueva;
    }

    public void setContraseniaNueva(String contraseniaNueva) {
        this.contraseniaNueva = contraseniaNueva;
    }
}