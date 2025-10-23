package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {
    private Integer idPedido;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idEstado;
    private String nombreEstado;
    private BigDecimal total;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaPago;
    private LocalDate fechaEstimadaEntrega; // ✨ NUEVO
    private List<DetallePedidoDTO> items;

    // ✨ NUEVO: Para el seguimiento visual
    private SeguimientoPedidoDTO seguimiento;

    // Getters y Setters existentes...

    public LocalDate getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    public void setFechaEstimadaEntrega(LocalDate fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }

    public SeguimientoPedidoDTO getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(SeguimientoPedidoDTO seguimiento) {
        this.seguimiento = seguimiento;
    }

    // ... resto de getters y setters
    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public List<DetallePedidoDTO> getItems() {
        return items;
    }

    public void setItems(List<DetallePedidoDTO> items) {
        this.items = items;
    }
}