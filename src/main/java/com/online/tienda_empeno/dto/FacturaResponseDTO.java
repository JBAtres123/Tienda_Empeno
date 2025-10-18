package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaResponseDTO {
    private Integer idFactura;
    private Integer idPagoPrestamo;
    private LocalDate fechaPago;
    private BigDecimal monto;
    private String nombreCliente;
    private String nombreArticulo;
    private String metodoPago;
    private Integer idPrestamo;
    private BigDecimal saldoRestante; // ✨ AGREGAR
    private Boolean prestamoCancelado; // ✨ AGREGAR
    private String mensajeAdicional; // ✨ AGREGAR

    // Getters y Setters
    public BigDecimal getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(BigDecimal saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public Boolean getPrestamoCancelado() {
        return prestamoCancelado;
    }

    public void setPrestamoCancelado(Boolean prestamoCancelado) {
        this.prestamoCancelado = prestamoCancelado;
    }

    public String getMensajeAdicional() {
        return mensajeAdicional;
    }

    public void setMensajeAdicional(String mensajeAdicional) {
        this.mensajeAdicional = mensajeAdicional;
    }

    // Getters y Setters
    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public Integer getIdPagoPrestamo() { return idPagoPrestamo; }
    public void setIdPagoPrestamo(Integer idPagoPrestamo) { this.idPagoPrestamo = idPagoPrestamo; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Integer getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Integer idPrestamo) { this.idPrestamo = idPrestamo; }
}
