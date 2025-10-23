package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class DetallePedidoDTO {
    private Integer idDetalle;
    private Integer idProductoTienda;
    private String nombreProducto;
    private BigDecimal precioVenta;
    private Integer cantidad;
    private BigDecimal subtotal;

    // Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getIdProductoTienda() {
        return idProductoTienda;
    }

    public void setIdProductoTienda(Integer idProductoTienda) {
        this.idProductoTienda = idProductoTienda;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}