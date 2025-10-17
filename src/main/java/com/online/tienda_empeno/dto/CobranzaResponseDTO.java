package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CobranzaResponseDTO {
    private Integer idCobranza;
    private Integer idPagoPrestamo;
    private String comentario;
    private LocalDate fechaDeVisita;
    private BigDecimal monto;
    private String nombreCliente;
    private String direccionCliente;
    private String nombreCobrador;
    private String asignacionRuta;
    private BigDecimal saldoRestante; // ✨ AGREGAR
    private Boolean prestamoCancelado; // ✨ AGREGAR

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

    // Getters y Setters
    public Integer getIdCobranza() { return idCobranza; }
    public void setIdCobranza(Integer idCobranza) { this.idCobranza = idCobranza; }

    public Integer getIdPagoPrestamo() { return idPagoPrestamo; }
    public void setIdPagoPrestamo(Integer idPagoPrestamo) { this.idPagoPrestamo = idPagoPrestamo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFechaDeVisita() { return fechaDeVisita; }
    public void setFechaDeVisita(LocalDate fechaDeVisita) { this.fechaDeVisita = fechaDeVisita; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    public String getNombreCobrador() { return nombreCobrador; }
    public void setNombreCobrador(String nombreCobrador) { this.nombreCobrador = nombreCobrador; }

    public String getAsignacionRuta() { return asignacionRuta; }
    public void setAsignacionRuta(String asignacionRuta) { this.asignacionRuta = asignacionRuta; }
}