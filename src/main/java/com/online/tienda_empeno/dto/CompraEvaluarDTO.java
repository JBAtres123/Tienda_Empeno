package com.online.tienda_empeno.dto;
import java.math.BigDecimal;

public class CompraEvaluarDTO {
    private Integer idEstado; // 14 = Comprado, 15 = No Aceptado
    private BigDecimal precioCompra; // Solo si se acepta
    private String mensaje; // Mensaje de aceptación o rechazo

    // Getters y Setters
    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}