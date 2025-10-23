package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class ProductoTiendaDTO {
    private Integer idArticulo;
    private String nombreEditado;
    private String descripcionEditada;
    private BigDecimal precioVentaTienda;

    // Getters y Setters
    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombreEditado() {
        return nombreEditado;
    }

    public void setNombreEditado(String nombreEditado) {
        this.nombreEditado = nombreEditado;
    }

    public String getDescripcionEditada() {
        return descripcionEditada;
    }

    public void setDescripcionEditada(String descripcionEditada) {
        this.descripcionEditada = descripcionEditada;
    }

    public BigDecimal getPrecioVentaTienda() {
        return precioVentaTienda;
    }

    public void setPrecioVentaTienda(BigDecimal precioVentaTienda) {
        this.precioVentaTienda = precioVentaTienda;
    }
}