package com.online.tienda_empeno.dto;

public class ItemPedidoDTO {
    private Integer idProductoTienda;
    private Integer cantidad;

    // Getters y Setters
    public Integer getIdProductoTienda() {
        return idProductoTienda;
    }

    public void setIdProductoTienda(Integer idProductoTienda) {
        this.idProductoTienda = idProductoTienda;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}