package com.online.tienda_empeno.dto;

import java.util.List;

public class CrearPedidoDTO {
    private List<ItemPedidoDTO> items;
    private Integer idDireccion; // Dirección de envío

    // Getters y Setters
    public List<ItemPedidoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoDTO> items) {
        this.items = items;
    }

    public Integer getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
    }
}