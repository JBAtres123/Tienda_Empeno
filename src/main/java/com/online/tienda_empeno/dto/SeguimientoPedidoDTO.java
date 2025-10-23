package com.online.tienda_empeno.dto;

import java.time.LocalDate;

public class SeguimientoPedidoDTO {
    private boolean pedidoPagado;
    private boolean enPreparacion;
    private boolean enCamino;
    private boolean entregado;
    private boolean cancelado;

    private LocalDate fechaEstimadaEntrega;
    private Integer diasRestantes;
    private String mensajeEstado;

    // Getters y Setters
    public boolean isPedidoPagado() {
        return pedidoPagado;
    }

    public void setPedidoPagado(boolean pedidoPagado) {
        this.pedidoPagado = pedidoPagado;
    }

    public boolean isEnPreparacion() {
        return enPreparacion;
    }

    public void setEnPreparacion(boolean enPreparacion) {
        this.enPreparacion = enPreparacion;
    }

    public boolean isEnCamino() {
        return enCamino;
    }

    public void setEnCamino(boolean enCamino) {
        this.enCamino = enCamino;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public LocalDate getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    public void setFechaEstimadaEntrega(LocalDate fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }

    public Integer getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public String getMensajeEstado() {
        return mensajeEstado;
    }

    public void setMensajeEstado(String mensajeEstado) {
        this.mensajeEstado = mensajeEstado;
    }
}